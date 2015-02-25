/**
 * The representation of a monster in the game and the values it holds
 * @author Samy Narrainen
 *
 */
public class Monster {
	//Health and the original health the monster started with
	int hp, originalhp;
	//Attack power (how much damage it can deal)
	int ap;
	//The monster's name
	String name;
	//How much damage did the monster take last hit?
	int damageTaken; 
	//Drawing positions for the monster
	int drawPosx, drawPosy;
	//Is the monster dead?
	boolean dead = false;
	
	/**
	 * Constructor to instantiate a monster
	 * @param hp
	 * @param ap
	 * @param name
	 */
	public Monster(int hp, int ap, String name) {
		this.hp = hp;
		this.originalhp = hp;
		this.ap = ap;
		this.name = name;
	}
	
	/**
	 * Sets the position for the monster to be drawn
	 * @param x
	 * @param y
	 */
	public void setDrawPosition(int x, int y) {
		this.drawPosx = x;
		this.drawPosy = y;
	}
}
