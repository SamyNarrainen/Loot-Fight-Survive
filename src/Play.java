import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Handles the majority of the game logic 
 * @author Samy Narrainen
 *
 */
public class Play extends JFrame implements Runnable {
	/**
	 * Variables
	 */
	//The maze
	Maze m;
	//The player
	Player p;
	//The class responsible for painting the game 
	gameCanvas gc;
	//Actions available in the game
	Actions actions; 
	//Threads relating to animating the paint
	Thread animatePlayer, animateMap, animateMisc;
	//If the player is moving, we don't let him move until done
	boolean movementLocked = false; 
	//Are we in an encounter, example a monster
	boolean encounter = false;
	//The monster that's currently encountered
	Monster encounteredMonster = null;
	
	
	/**
	 * Constructor
	 * @param n the player's name
	 * @param l the level's file name
	 */
	public Play(String n, String l) {
		
		p = new Player(n);
		m = new Maze(l);
		gc = new gameCanvas(this);
		actions = new Actions(this);

		Container x = getContentPane();
		x.setLayout(new BorderLayout());
		x.setBackground(Color.BLACK);
		x.add(gc, BorderLayout.CENTER);

		setPlayerStartPosition();
		
		/**
		 * Implements action shortcuts
		 */
		this.setFocusable(true);
		this.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				try {
					//Outside of combat
					if (!encounter && !movementLocked && !gc.moving && !gc.movingMap) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_DOWN:
							actions.movePlayerSouth();
							break;
						case KeyEvent.VK_LEFT:
							actions.movePlayerWest();
							break;
						case KeyEvent.VK_RIGHT:
							actions.movePlayerEast();
							break;
						case KeyEvent.VK_UP:
							actions.movePlayerNorth();
							break;
						case KeyEvent.VK_S:
							actions.save();
							break;
						}
					}

					//Whilst in combat
					if (encounter && !gc.moving && !gc.movingMap) {
						switch (e.getKeyCode()) {
						case KeyEvent.VK_A: 
							actions.attack();
							break;
						
						case KeyEvent.VK_F: 
							actions.flee();
							break;
						}
					}
					
					
					gc.repaint();
				} catch (java.lang.IndexOutOfBoundsException exception) {
					System.out.println("The player is unable to move in this direction");
				}

			}
		});

		/**
		 * JPanel modifications
		 */
		this.setTitle("Loot, Fight Survive!");
		this.getContentPane().setPreferredSize(new Dimension(150 * 5, (150 * 5) + 100));
		this.setMinimumSize(new Dimension(150 * 5, (150 * 5) + 100));
		this.pack();
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}


	
	public String generateTrap() {
		ArrayList<String> traps = new ArrayList<String>();
		traps.add("small bomb");
		traps.add("medium bomb");
		traps.add("large bomb");

		String randomTrap = traps.get((int) (Math.random() * (traps.size() - 0)));

		if (randomTrap.equals("small bomb")) {
			p.hp -= randomInt(1, 4);
		} else if (randomTrap.equals("medium bomb")) {
			p.hp -= randomInt(5, 8);
		} else if (randomTrap.equals("large bomb")) {
			p.hp -= randomInt(9, 15);
		}

		return randomTrap;

	}

	public String generateTreasure() {
		ArrayList<String> treasures = new ArrayList<String>();
		treasures.add("some gold");
		treasures.add("some health");
		treasures.add("a lot of health");
		treasures.add("a lot of gold");
		treasures.add("gold and health");

		String randomTreasure = treasures.get(randomInt(0, treasures.size()));

		if (randomTreasure.equals("some gold")) {
			p.ap += (int) (1 + Math.random() * ((2 - 1) + 1));
		} else if (randomTreasure.equals("some health")) {
			p.hp = Math.min(100, p.hp + (int) (1 + Math.random() * ((20 - 10) + 1)));
		} else if (randomTreasure.equals("a lot of health")) {
			p.hp = Math.min(100, p.hp + (int) (1 + Math.random() * ((40 - 20) + 1)));
		} else if (randomTreasure.equals("a lot of gold")) {
			p.ap += (int) (1 + Math.random() * ((8 - 2) + 1));
		} else if(randomTreasure.equals("gold and health")) {
			p.hp = Math.min(100, p.hp + (int) (1 + Math.random() * ((40 - 20) + 1)));
			p.ap += (int) (1 + Math.random() * ((8 - 2) + 1));
		}

		return randomTreasure;

	}

	public String generateMonster() {
		if (encounter == false) {
			ArrayList<Monster> monsters = new ArrayList<Monster>();

			Monster monster1 = new Monster(200, 2, "Slime");
			monsters.add(monster1);

			Monster monster2 = new Monster(50, 8, "Amabo");
			monsters.add(monster2);

			Monster monster3 = new Monster(100, 5, "Lite Svart");
			monsters.add(monster3);

			encounteredMonster = monsters.get(randomInt(0, monsters.size()));

		}
		return encounteredMonster.name;

	}


	/**
	 * Starts the player in an available EMPTY position
	 */
	public void setPlayerStartPosition() {
		if (!m.tiles.isEmpty()) {
			if (m.tiles.get(p.posy).get(p.posx).isNull()) {
				for (int i = 0; i < m.tiles.size(); i++) {
					List<Tile> l = m.tiles.get(i);

					for (int j = 0; j < l.size(); j++)
						if (!l.get(j).isNull())
							p.setPosition(i, j);
				}
			}
			m.tiles.get(p.posy).get(p.posx).setFound(true);
		}
	}

	
	/**
	 * Methods to start threads
	 */
	public void startAnimatePlayer() {
		animatePlayer = new Thread(this);
		animatePlayer.start();
	}
	
	public void startAnimateMap() {
		animateMap = new Thread(this);
		animateMap.start();
	}
	
	public void startAnimateMisc() {
		animateMisc = new Thread(this);
		animateMisc.start();
	}

	/**
	 * Running the threads, relating to animating the game
	 */
	@Override
	public void run() {
		//Animating the player moving
		if (Thread.currentThread() == animatePlayer) {
			movementLocked = true;

			int destinationx = 0, destinationy = 0;

			if (p.direction == p.UP) {
				gc.animatex = (150 * (Math.min(4, p.posx))) + 150 / 3;
				gc.animatey = (150 * (Math.min(4, p.previousPosy))) + 150 / 3;
				destinationx = (150 * (Math.min(4, p.posx))) + 150 / 3;
				destinationy = (150 * (Math.min(4, p.posy))) + 150 / 3;
			} else if (p.direction == p.RIGHT) {
				gc.animatex = (150 * (Math.min(4, p.previousPosx))) + 150 / 3;
				gc.animatey = (150 * (Math.min(4, p.posy))) + 150 / 3;
				destinationx = (150 * (Math.min(4, p.posx))) + 150 / 3;
				destinationy = (150 * (Math.min(4, p.posy))) + 150 / 3;

			} else if (p.direction == p.DOWN) {
				gc.animatex = (150 * (Math.min(4, p.posx))) + 150 / 3;
				gc.animatey = (150 * (Math.min(4, p.previousPosy))) + 150 / 3;
				destinationx = (150 * (Math.min(4, p.posx))) + 150 / 3;
				destinationy = (150 * (Math.min(4, p.posy))) + 150 / 3;

			} else if (p.direction == p.LEFT) {
				gc.animatex = (150 * (Math.min(4, p.previousPosx))) + 150 / 3;
				gc.animatey = (150 * (Math.min(4, p.posy))) + 150 / 3;
				destinationx = (150 * (Math.min(4, p.posx))) + 150 / 3;
				destinationy = (150 * (Math.min(4, p.posy))) + 150 / 3;
			}

			gc.moving = true;


			while (Thread.currentThread() == animatePlayer) {

				if (p.direction == p.DOWN || p.direction == p.RIGHT) {

					if (gc.animatex < destinationx) {
						gc.animatex++;
					} else if (gc.animatey < destinationy) {
						gc.animatey++;
					} else if (gc.animatex >= destinationx
							&& gc.animatey >= destinationy) {
						animatePlayer = null;
						gc.moving = false;
						movementLocked = false;
						
					}

				} else if (p.direction == p.UP || p.direction == p.LEFT) {

					if (gc.animatex > destinationx) {
						gc.animatex--;
					} else if (gc.animatey > destinationy) {
						gc.animatey--;
					} else if (gc.animatex <= destinationx
							&& gc.animatey <= destinationy) {
						animatePlayer = null;
						gc.moving = false;
						movementLocked = false;
					}
				}

				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Animate the map moving
		if(Thread.currentThread() == animateMap) {
			gc.movingMap = true;
			gc.drawnMap = false;
			
			if(p.direction == p.RIGHT) {
				gc.movingRowPos = 0;
				gc.movingColumnPos = 0;
			} else if(p.direction == p.LEFT) {
				gc.movingRowPos = -150;
				gc.movingColumnPos = 0;
			} else if(p.direction == p.DOWN) {
				gc.movingRowPos = 0;
				gc.movingColumnPos = 0;
			} else if(p.direction == p.UP) {
				gc.movingRowPos = 0;
				gc.movingColumnPos = -150;
			}
			
			while(Thread.currentThread() == animateMap) {
				
				if(p.direction == p.RIGHT) {
					if(gc.movingRowPos <= -gc.blocksize) {
						animateMap = null;
						gc.movingMap = false;
						gc.movingRowPos = 0;
					}
					gc.movingRowPos--;
				
				} else if(p.direction == p.LEFT) {
					if(gc.movingRowPos >= 0) {
						animateMap = null;
						gc.movingMap = false;
						gc.movingRowPos = 0;
					}
					gc.movingRowPos++;
				} else if(p.direction == p.DOWN) {
					if(gc.movingColumnPos <= -gc.blocksize) {
						animateMap = null;
						gc.movingMap = false;
						gc.movingColumnPos = 0;
					}
					gc.movingColumnPos--;
				} else if(p.direction == p.UP) {
					if(gc.movingColumnPos >= 0) {
						animateMap = null;
						gc.movingMap = false;
						gc.movingColumnPos = 0;
					}
					gc.movingColumnPos++;
				}
				//repaint();
				//System.out.println("MOVEMENT = " + Integer.toString(gc.movingRowPos));
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			

			
		}
		
		//Animate misc things, such as the explosion of a trap
		if(Thread.currentThread() == animateMisc) {
			
			//Trap animate
			if(m.tiles.get(p.posy).get(p.posx).getType() == 2) {
				int frame = 0;
				
				while(Thread.currentThread() == animateMisc) {
					
					if(frame >= 4) {
						animateMisc = null;
						gc.animateExplosion = false;
						
					} else {
						gc.animateExplosion = true;
					}
					
					
					gc.explosionCount = frame;
					frame++;
					
					repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * Generates a random integer within a range
	 * @param min
	 * @param max
	 * @return
	 */
	int randomInt(int min, int max) {
	   int range = (max - min);     
	   return (int)(Math.random() * range) + min;
	}
}
