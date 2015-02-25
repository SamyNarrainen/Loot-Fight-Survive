import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * The main menu, shown on start up of the program
 * 
 * @author Samy Narrainen
 * 
 */
public class Menu extends JFrame implements ActionListener {
	// The JFrame's container
	Container cp;
	// The buttons to navigate the GUI and perform actions
	JButton newGameButton, loadButton, createMapButton, newGameStart,
			createMapSubmitBtn;
	// The panels that hold relevant content
	JPanel newGamePanel, selection, loadPanel, createMapPanel,
			createMapOptionsPanel;
	// The text fields for user input, the player's name and map name
	JTextField nametf, createMapName;
	// Containing the selection of level files available to the player
	JComboBox levelcb;
	// The text area for creating a map
	JTextArea mapTextArea;
	// The directory to the level files
	String levelsPath;
	// An array of level names
	ArrayList<String> levels;
	// An array of actual files available
	ArrayList<File> levelFiles;
	// When creating a new map, we need to know the largest row so we can match
	// every row
	int largestWidth;
	// Play class that we start/load
	Play p;
	// Invalid characters in a file name so we can validate our new files
	private final ArrayList<Character> ILLEGAL_CHARACTERS = new ArrayList<Character>(
			Arrays.asList('/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*',
					'\\', '<', '>', '|', '\"', ':'));

	/**
	 * Constructor
	 */
	public Menu() {
		levels = new ArrayList<String>();
		levelFiles = new ArrayList<File>();

		cp = getContentPane();
		cp.setLayout(new BorderLayout());

		/**
		 * The panel of buttons to navigate the main sections of the menu
		 */
		selection = new JPanel(new GridLayout(1, 3));
		// New game
		newGameButton = new JButton("New Game");
		newGameButton.addActionListener(this);
		selection.add(newGameButton);

		// Load (continue)
		loadButton = new JButton("Continue");
		loadButton.addActionListener(this);
		selection.add(loadButton);

		// Create Map
		createMapButton = new JButton("Create Map");
		createMapButton.addActionListener(this);
		selection.add(createMapButton);

		cp.add(selection, BorderLayout.SOUTH);

		/**
		 * Load splash image
		 */
		try {
			String imgPath = new java.io.File(".").getCanonicalPath()
					+ "\\res\\images\\";
			imgPath = imgPath.replace('\\', '/');
			ImageIcon splash = new ImageIcon(imgPath + "splash.png");
			JLabel splashLabel = new JLabel(splash);
			splashLabel.setBounds(400, 400, 400, 400);
			cp.add(splashLabel, BorderLayout.CENTER);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		/**
		 * NEW GAME PANEL
		 */
		newGamePanel = new JPanel();
		newGamePanel.setLayout(new BoxLayout(newGamePanel, BoxLayout.Y_AXIS));

		JLabel nameLabel = new JLabel("Name: ");
		nametf = new JTextField();
		nametf.setSize(new Dimension(400, 50));
		nametf.setPreferredSize(new Dimension(400, 50));
		nametf.setMaximumSize(nametf.getPreferredSize());
		nametf.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel levelLabel = new JLabel("Level: ");

		// Grab level files
		File[] levelFiles = null;
		String[] levels = null;

		try {
			levelsPath = new java.io.File(".").getCanonicalPath()
					+ "\\res\\levels\\";
			levelsPath = levelsPath.replace('\\', '/');

			File levelsDirectory = new File(levelsPath);
			levelFiles = levelsDirectory.listFiles();

			levels = new String[levelFiles.length];

			for (int i = 0; i < levelFiles.length; i++) {
				levels[i] = levelFiles[i].getName();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		levelcb = new JComboBox(levels);
		levelcb.setSize(new Dimension(400, 50));
		levelcb.setPreferredSize(new Dimension(400, 50));
		levelcb.setMaximumSize(levelcb.getPreferredSize());

		newGameStart = new JButton("Start!");
		newGameStart.addActionListener(this);
		newGameStart.setSize(new Dimension(400, 100));
		newGameStart.setPreferredSize(new Dimension(400, 100));

		Box box1 = new Box(BoxLayout.X_AXIS);
		box1.setSize(new Dimension(500, 150));
		box1.add(nameLabel);
		box1.add(nametf);

		Box box2 = new Box(BoxLayout.X_AXIS);
		box2.setSize(new Dimension(500, 150));
		box2.add(levelLabel);
		box2.add(levelcb);

		newGamePanel.add(box1);
		newGamePanel.add(box2);

		newGamePanel.add(newGameStart);

		/**
		 * CREATE MAP Panel
		 */
		createMapPanel = new JPanel(new BorderLayout());

		mapTextArea = new JTextArea();
		mapTextArea.setLineWrap(true); // bloody fantastic!
		mapTextArea.setFont(new Font("Arial", Font.PLAIN, 25));

		createMapOptionsPanel = new JPanel(new GridLayout(1, 2));
		createMapName = new JTextField("name");
		createMapSubmitBtn = new JButton("Create!");
		createMapSubmitBtn.addActionListener(this);

		createMapOptionsPanel.add(createMapName);
		createMapOptionsPanel.add(createMapSubmitBtn);

		createMapPanel.add(mapTextArea, BorderLayout.CENTER);
		createMapPanel.add(createMapOptionsPanel, BorderLayout.SOUTH);

		/**
		 * JPanel modifications
		 */
		this.setTitle("Loot, Fight Survive!: Menu");
		this.setSize(500, 300);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		Menu m = new Menu();
	}

	/**
	 * Functionality to the various buttons
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton clicked = (JButton) e.getSource();

		if (clicked == newGameButton) {
			// Refresh files in directory in case new one was made.
			getFilesInDirectory();

			cp.removeAll();
			newGameButton.setEnabled(false);
			createMapButton.setEnabled(true);
			cp.add(selection, BorderLayout.SOUTH);
			cp.add(newGamePanel, BorderLayout.CENTER);

		} else if (clicked == loadButton) {
			try {
				File save = new File("save.data");
				if (save.exists()) {

					FileInputStream fis = new FileInputStream(new File(
							"save.data"));
					ObjectInputStream ois = new ObjectInputStream(fis);
					ArrayList<Object> saveData = new ArrayList<Object>();

					saveData = (ArrayList<Object>) ois.readObject();
					ArrayList<List<Integer>> maze = (ArrayList<List<Integer>>) saveData
							.get(0);
					String levelDirectory = (String) saveData.get(1);
					Player player = (Player) saveData.get(2);
					ArrayList<List<Tile>> tiles = (ArrayList<List<Tile>>) saveData
							.get(3);

					p = new Play(player.name, levelDirectory);
					p.p = player;
					p.m.maze.addAll(maze);
					p.m.columns = maze.get(0).size();
					p.m.rows = maze.size();
					p.m.tiles.addAll(tiles);

					System.out.println(p.m.maze);
					System.out.println(maze);

					ois.close();
				} else if (!save.exists()) {
					JOptionPane.showMessageDialog(this,
							"WARNING! No save file was found!");
				}

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			this.setVisible(false);

		} else if (clicked == createMapButton) {
			cp.removeAll();
			newGameButton.setEnabled(true);
			createMapButton.setEnabled(false);
			cp.add(selection, BorderLayout.SOUTH);
			cp.add(createMapPanel, BorderLayout.CENTER);

		} else if (clicked == newGameStart) {
			p = new Play(nametf.getText(), (String) levelcb.getSelectedItem());
			this.setVisible(false);
		} else if (clicked == createMapSubmitBtn) {
			if (validateMap()) {
				JOptionPane.showMessageDialog(this, "SUCUESS! Map '"
						+ createMapName.getText() + ".txt' was created!");
				saveMap();
			}

		}

		cp.validate();
		this.validate();
		cp.repaint();
		selection.revalidate();
		newGamePanel.revalidate();
		createMapPanel.revalidate();
	}

	public void changePanel(JPanel jp) {
		cp.add(jp, BorderLayout.CENTER);
	}

	/**
	 * Checks if the map the user created is valid and usable
	 * 
	 * @return true if it is.
	 */
	public boolean validateMap() {
		getFilesInDirectory();
		// Check if the file already exists
		if (levels.contains(createMapName.getText() + ".txt")) {
			JOptionPane.showMessageDialog(this,
					"WARNING! Map with name already exists");
			return false;
		} else {
			// Is the filename valid?
			String newFileName = createMapName.getText();
			while (!newFileName.equals("")) {
				if (ILLEGAL_CHARACTERS.contains(newFileName.charAt(0))) {
					JOptionPane.showMessageDialog(
							this,
							"WARNING! Invalid map name format ("
									+ newFileName.charAt(0) + ")");
					return false;
				} else {
					newFileName = newFileName.substring(1);
				}
			}
		}

		// Check the contents of the map
		largestWidth = 0;
		for (String line : mapTextArea.getText().split("\\n")) {
			line = line.replaceAll(" ", ""); // Clear white space as it's not
												// necessary in saving/reading
			largestWidth = Math.max(largestWidth, line.length());

			if (line.equals("")) {
				JOptionPane.showMessageDialog(this,
						"WARNING! Level is blank or empty rows are present");
				return false;
			}

			// Could use a regex, however we wouldn't be able to easily tell the
			// invalid character to the user
			while (!line.equals("")) {
				if (line.charAt(0) != '0' && line.charAt(0) != '1'
						&& line.charAt(0) != '2' && line.charAt(0) != '3'
						&& line.charAt(0) != '4') {
					JOptionPane.showMessageDialog(this,
							"WARNING! Invalid character: " + line.charAt(0));
					return false;
				} else {
					line = line.substring(1);
				}
			}
		}

		return true;
	}

	/**
	 * Makes the map rectangular and saves the map in a file.
	 */
	public void saveMap() {
		try {
			File newFile = new File(levelsPath + createMapName.getText()
					+ ".txt");
			BufferedWriter reader = new BufferedWriter(new FileWriter(newFile));

			for (String line : mapTextArea.getText().split("\\n")) {
				line = line.replaceAll(" ", "");
				if (line.length() < largestWidth) {
					for (int i = line.length(); i < largestWidth; i++)
						line += 4;
				}
				reader.write(line + "\n");
			}
			reader.close();

			levelcb.addItem(createMapName.getText() + ".txt");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds all the level files that are available to the levelFiles array
	 */
	public void getFilesInDirectory() {
		File levelsDirectory = new File(levelsPath);
		levelFiles.addAll(Arrays.asList(levelsDirectory.listFiles()));

		for (int i = 0; i < levelFiles.size(); i++) {
			levels.add(levelFiles.get(i).getName());
		}

	}

}
