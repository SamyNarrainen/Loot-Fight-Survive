import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * The actions available to a player within the game
 * @author Samy Narrainen
 *
 */
public class Actions {
	//Necessary to keep track player fleeing for room clearing
	boolean fleed = false;

	public Play p;

	public Actions(Play p) {
		this.p = p;
	}

	/**
	 * Methods to move the player, providing it's a valid move
	 */
	public void movePlayerNorth() {
		if (p.m.tiles.get(p.p.posy).get(p.p.posx).hasNorthTile()) {
			p.p.move(p.p.UP);
			p.m.tiles.get(p.p.posy).get(p.p.posx).setFound(true);
			
			p.startAnimatePlayer();
			
			//If the player is beyond the drawable range
			if (p.p.posy >= 4)
				p.startAnimateMap();

			setPlayerInteractionTile();
		}
	}

	public void movePlayerEast() {
		if (p.m.tiles.get(p.p.posy).get(p.p.posx).hasEastTile()) {
			p.p.move(p.p.RIGHT);
			p.m.tiles.get(p.p.posy).get(p.p.posx).setFound(true);
			
			p.startAnimatePlayer();
			if (p.p.posx > 4)
				p.startAnimateMap();
				
			
			setPlayerInteractionTile();
		}
	}

	public void movePlayerSouth() {
		if (p.m.tiles.get(p.p.posy).get(p.p.posx).hasSouthTile()) {
			p.p.move(p.p.DOWN);
			p.m.tiles.get(p.p.posy).get(p.p.posx).setFound(true);
			
			p.startAnimatePlayer();
			if (p.p.posy > 4)
				p.startAnimateMap();

			setPlayerInteractionTile();
		}

	}

	public void movePlayerWest() {
		if (p.m.tiles.get(p.p.posy).get(p.p.posx).hasWestTile()) {
			p.p.move(p.p.LEFT);
			p.m.tiles.get(p.p.posy).get(p.p.posx).setFound(true);
			
			p.startAnimatePlayer();
			if (p.p.posx >= 4)
				p.startAnimateMap();

			setPlayerInteractionTile();
		}
	}

	/**
	 * The action performed if the player attacks
	 */
	public void attack() {
		if (p.encounteredMonster.hp > 0) {
			// Player attacking
			int playerChanceToHit = p.randomInt(0, 100);

			if (playerChanceToHit < 80) {
				// 'Critical' strike at 20% chance
				if (playerChanceToHit < 20) {
					p.encounteredMonster.hp -= p.p.ap * 2;
					p.encounteredMonster.damageTaken = p.p.ap * 2;

				} else {
					p.encounteredMonster.hp -= p.p.ap;
					p.encounteredMonster.damageTaken = p.p.ap;

				}
			}

			// Monster attacking
			int monsterChanceToHit = p.randomInt(0, 100);

			if (monsterChanceToHit < 80) {
				int damage = p.randomInt(0, p.encounteredMonster.ap);

				p.p.hp -= damage;
				p.p.damageTaken = damage;
			}
		} else if(p.encounteredMonster.hp <= 0) {
				p.p.damageTaken = 0;
				p.encounteredMonster.dead = true;
				p.encounter = false;
				p.m.tiles.get(p.p.posy).get(p.p.posx).setType(0);
		}
		
		p.gc.attacking = true;
	}
	

	/**
	 * The action performed if the player flees
	 */
	public void flee() {
		
		String fleeResult = "";

		// 50% CHANCE TO FAIL, FLEE FAILURE
		if (p.randomInt(0, 100) <= 50) {

			fleeResult += "You fail to flee. ";

			//monster attacks if you fail, but it can still miss!
			int monsterChance = p.randomInt(0, 100);
			if (monsterChance < 80) {
				//Not random damage because it's an easy hit for the monster!
				p.p.hp -= p.encounteredMonster.ap;
				p.p.damageTaken = p.encounteredMonster.ap;
			}
			
			p.gc.fleeStatus = fleeResult;
		} else {

			fleeResult += "You sucessfully flee!";
			Tile t = p.m.tiles.get(p.p.posy).get(p.p.posx);
			
			ArrayList<Integer> moves = t.getAdjacentTiles();
			
			p.p.damageTaken = 0;
			p.encounter = false;
			fleed = true;
			switch(moves.get(p.randomInt(0, moves.size()))) {
			case 10: 
				movePlayerNorth();
				break;
			case 20:
				movePlayerEast();
				break;
			case 30:
				movePlayerSouth();
				break;
			case 40:
				movePlayerWest();
				break;
			}
			
			p.gc.repaint();
		}

		p.gc.fleeStatus = fleeResult;
		
	}

	/**
	 * Saves the state of the game to a file 'save.data'
	 */
	public void save() {
		if (!p.encounter) {
			try {
				FileOutputStream fos = new FileOutputStream(new File(
						"save.data"));
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				ArrayList<Object> saveData = new ArrayList<Object>();
				saveData.add(p.m.maze);
				saveData.add(p.m.levelDirectory);
				saveData.add(p.p);
				saveData.add(p.m.tiles);

				oos.writeObject(saveData);

				oos.close();
				
				JOptionPane.showMessageDialog(p, "You have sucessfully saved the game!", "Loot, Fight, Survive!: Save Sucessful", JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Handles what the player is currently interacting with. 
	 */
	public void setPlayerInteractionTile() {
		if(p.m.isMazeCleared()) p.gc.gameWon = true;
		
		if(!fleed) {
			p.m.tiles.get(p.p.previousPosy).get(p.p.previousPosx).setType(0);
		} else {
			p.gc.fleeStatus = "";
			fleed = false;
		}
		

		switch (p.m.tiles.get(p.p.posy).get(p.p.posx).getType()) {
		case 1 : {
			p.generateMonster();
			p.encounter = true;
			break;
		}
		case 2 : { //TRAP
			p.startAnimateMisc();
			p.generateTrap();
			break;
		}
		case 3 : { //TREASURE
			p.gc.treasureStatus = p.generateTreasure();
			break;
		}
		}
	}

	/**
	 * Navigates the user back to the original menu
	 */
	public void returnToMainMenu() {
		Menu m = new Menu();
		p.setVisible(false);
	}
	
	/**
	 * Displays a help window
	 */
	public void help() {
		try {
			String imageDirectory = new java.io.File(".").getCanonicalPath() + "\\res\\images\\";
			imageDirectory = imageDirectory.replace('\\', '/');
			ImageIcon helpImage = new ImageIcon(imageDirectory + "helpImage.png");
			
			JOptionPane.showMessageDialog(null, "", "Loot, Fight, Survive!: Help", JOptionPane.INFORMATION_MESSAGE, helpImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Assigns methods a numerical value so they can be called by integer
	 * 
	 * @param selection
	 */
	public void actionSelect(int selection) {
		switch (selection) {
		case 1:
			movePlayerNorth();
			break;

		case 2:
			movePlayerEast();
			break;

		case 3:
			movePlayerSouth();
			break;

		case 4:
			movePlayerWest();
			break;

		case 5:
			attack();
			break;

		case 6:
			flee();
			break;

		case 7:
			save();
			break;

		case 8:
			returnToMainMenu();
			break;
			
		case 9: 
			help();
			break;
		}
	}
	
	

}
