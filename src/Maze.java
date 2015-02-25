import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Maze class, handling the reading of a file and the creation of a map
 * @author Samy Narrainen
 *
 */
public class Maze implements Serializable {

	private static final long serialVersionUID = 334664502184146667L;
	
	BufferedReader reader;
	//Represents the map in an integer format
	public ArrayList<List<Integer>> maze;
	// represents file on device
	File mazeFile;
	// res/levels directory for better file management
	String levelDirectory;

	public int columns, rows;

	String levelSelection;

	Random randomNo;
	
	public ArrayList<List<Tile>> tiles;

	// Level structure
	final int EMPTY = 0;
	final int MONSTER = 1;
	final int TRAP = 2;
	final int TREASURE = 3;
	final int NULL = 4; // un-traversable.

	
	/**
	 * Puts together the map reading methods to fully read a map from a file and translate it to a tile array. 
	 * @param levelSelection, the map file to load
	 */
	public Maze(String levelSelection) {
		this.levelSelection = levelSelection;
		
		maze = new ArrayList<List<Integer>>();
		
		if(maze.size() <= 0) readMaze(levelSelection);
		
		
		tiles = new ArrayList<List<Tile>>();
		
		if(tiles.size() <= 0) createTiles();
	}
	

	/**
	 * Reads the file and converts it into a 2d integer array
	 * @param levelSelection
	 */
	public void readMaze(String levelSelection) {
		try {
			// Setup directory
			levelDirectory = new java.io.File(".").getCanonicalPath()
					+ "\\res\\levels\\" + levelSelection;
			levelDirectory = levelDirectory.replace('\\', '/');

			mazeFile = new File(levelDirectory);
			reader = new BufferedReader(new FileReader(mazeFile));

			String x = "";

			while (reader.ready()) {
				x = reader.readLine();
				List<Integer> lineList = new ArrayList<Integer>();
				maze.add(lineList);

				while (x.length() > 0) {
					if (x.charAt(0) != ' ' && x.charAt(0) != '\r' && x.charAt(0) != '\n') {
						lineList.add(Character.getNumericValue(x.charAt(0))); // converts char to integer then adds
						x = x.substring(1);
					} else {
						x = x.substring(1);
					}
				}
			}

			System.out.println("maze = " + maze);
			System.out.println("Columns: " + maze.get(0).size());

			System.out.println("Rows: " + maze.size());

			columns = maze.get(0).size();
			rows = maze.size();

		} catch (Exception e) {
			System.out.println("Failed to read file or a map was loaded."); 
		}

	}
	
	/**
	 * Reads the 2d integer array and translates it into actual tiles
	 */
	public void createTiles() {
		for (int i = 0; i < this.rows; i++) {
			List<Integer> k = maze.get(i);
			List<Tile> tileGroup = new ArrayList<Tile>();
			
			for (int p = 0; p < k.size(); p++) {

				try {
					Tile t = new Tile();
					if(northTileExists(i, p)) t.setNorthTile(true);
					if(eastTileExists(i, p)) t.setEastTile(true);
					if(southTileExists(i, p)) t.setSouthTile(true);
					if(westTileExists(i, p)) t.setWestTile(true);
					
					if(k.get(p) == 4) t.setNull(true);
					
					t.setType(k.get(p));
					
					tileGroup.add(t);
				} catch (IndexOutOfBoundsException e) {}
				
			}
			
			tiles.add(tileGroup);

		}

	}
	
	
	
	/**
	 * Methods to check whether or not a tile exists on an adjacent side
	 */
	public boolean northTileExists(int i, int p) {
		try {
			i--;
			if(maze.get(i).get(p) != 4) return true;
				
			
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	public boolean eastTileExists(int i, int p) {
		try {
			p++;
			if(maze.get(i).get(p) != 4) return true;
			
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	public boolean southTileExists(int i, int p) {
		try {
			i++;
			if(maze.get(i).get(p) != 4) return true;
			
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	public boolean westTileExists(int i, int p) {
		try {
			p--;
			if(maze.get(i).get(p) != 4) return true;
			
		} catch (IndexOutOfBoundsException e) {}
		return false;
	}
	
	
	public boolean isMazeCleared() {
		for(List<Tile> k : tiles) 
			for(Tile t : k)
				if(t.getType() == MONSTER || t.getType() == TRAP || t.getType() == TREASURE) return false;
		 
		return true;
	}
}
