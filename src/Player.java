import java.io.Serializable;

/**
 * The representation of the player in the game and the values it holds
 * @author Samy Narrainen
 *
 */
public class Player implements Serializable {
	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	//health
	int hp;
	//attack power (how much dmg it can deal) AKA GOLD!
	int ap;
	// name
	String name;
	//The actual player positions on the array
	int posx = 0, posy = 0;
	//The position the player was in prior to the next move
	int previousPosx = 0, previousPosy = 0;
	//What direction is the player facing?
	int direction;
	//What type of square is the player interacting with currently?
	int interaction;
	// How much damage did the player take last hit?
	int damageTaken;
	//Possible directions
	final int UP = 10, RIGHT = 20, DOWN = 30, LEFT = 40;
	
	/**
	 * Constructor
	 * @param The player's name passed at new game
	 */
	public Player(String name) {
		hp = 100; // TODO CHANGE FOR RELEASE
		ap = 10;
		this.name = name;
	}
	
	/**
	 * Alters the player's previous and current positions based on a direction
	 * @param direction
	 */
	public void move(int direction) {
		previousPosx = posx;
		previousPosy = posy;
		this.direction = direction;

		switch (direction) {
		case LEFT:
			posx--;
			break;
		case RIGHT:
			posx++;
			break;
		case UP:
			posy--;
			break;
		case DOWN:
			posy++;
			break;
		}
	}

	/**
	 * Manually sets the player's position
	 * @param y
	 * @param x
	 */
	public void setPosition(int y, int x) {
		posy = y;
		posx = x;
	}

	/**
	 * Sets what type of tile the player is currently interacting with
	 * @param i
	 */
	public void setInteraction(int i) {
		this.interaction = i;
	}

	/**
	 * Gets the type of tile the player is currently interacting with
	 * @return
	 */
	public int getInteraction() {
		return this.interaction;
	}

}
