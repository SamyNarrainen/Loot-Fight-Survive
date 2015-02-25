import java.io.Serializable;
import java.util.ArrayList;

/**
 * The representation of a tile/block in the game
 * @author Samy Narrainen
 *
 */
public class Tile implements Serializable {
	/**
	 * Variables
	 */
	//The tiles which are connected to this one
	public boolean north, east, south, west; 
	//Associations with the room
	//public Monster m;
	
	//Is the room null (of type 4)
	public boolean isNull;
	//Has the player entered this square at anytime? 
	public boolean found;
	//The type of room it is, 0 EMPTY, 1 MONSTER, 2 TRAP, 3 TREASURE, 4 NULL
	public int type; 
	
	
	/**
	 * Constructor
	 */
	public Tile()  {
		this.north = false;
		this.east = false;
		this.south = false;
		this.west = false;
		
		//this.m = null;
		this.isNull = false;
		this.found = false;
	}
	
    /**
     * Methods to set whether or not the tile has an adjacent tile
     * @param b true if it does
     */
	public void setNorthTile(boolean b) {
		this.north = b;
	}
	
	public void setEastTile(boolean b) {
		this.east = b;
	}
	
	public void setSouthTile(boolean b) {
		this.south = b;
	}
	
	public void setWestTile(boolean b) {
		this.west = b;
	}
	
	/**
	 * Methods to see if the tile has a tile in an adjacent position
	 */
	public boolean hasNorthTile(){
		if(this.north) 
			return true;
		else 
			return false;
	}
	
	public boolean hasEastTile(){
		if(this.east) 
			return true;
		else 
			return false;
	}
	
	public boolean hasSouthTile(){
		if(this.south) 
			return true;
		else 
			return false;
	}
	
	public boolean hasWestTile(){
		if(this.west) 
			return true;
		else 
			return false;
	}
	
	/**
	 * Whether or not the tile is considered null (4)
	 * @return true if the room is null, else false
	 */
	public boolean isNull() {
		if(this.isNull) 
			return true;
		else 
			return false;
	}
	
	/**
	 * Setter for isNull variable
	 * @param b
	 */
	public void setNull(boolean b) {
		this.isNull = b;
	}
	
	
	/**
	 * Has the tile been found?
	 * @return true if it has
	 */
	public boolean isFound(){
		if(this.found)
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Setter for found variable
	 * @param b
	 */
	public void setFound(boolean b) {
		this.found = b;
	}
	
	
	/**
	 * A list of all adjacent tiles, encoded by integer
	 * @return sides, the the tiles represented with this tile
	 */
	public ArrayList<Integer> getAdjacentTiles() {
		ArrayList<Integer> sides = new ArrayList<Integer>();
		
		if(this.north) sides.add(10);
		if(this.east) sides.add(20);
		if(this.south) sides.add(30);
		if(this.west) sides.add(40);
		
		return sides;
		
	}
	
	
	/**
	 * Setter for the room type
	 * @param t:  0 EMPTY, 1 MONSTER, 2 TRAP, 3 TREASURE
	 */
	public void setType(int t) {
		this.type = t;
	}
	
	
	/**
	 * Getter for the type of room
	 * @return
	 */
	public int getType() {
		return this.type;
	}
	
}
