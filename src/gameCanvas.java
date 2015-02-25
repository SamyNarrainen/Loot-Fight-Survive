import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * The 'JPanel' that does all the painting for the state of the game,
 * Panel instead of Canvas for easy double buffering.
 * @author Samy Narrainen
 *
 */
public class gameCanvas extends JPanel {
	/**
	 * Variables
	 */
	private static final long serialVersionUID = 1L;
	//The play being used
	public Play play;
	//The actions available to the player, needed to set functionality to the CustomButtons
	Actions actions;
	//The types of tile, null being untraversable
	final int EMPTY = 0, MONSTER = 1, TRAP = 2, TREASURE = 3, NULL = 4;
	//How big are the tiles? 
	int blocksize = 150;
	//Statuses which are set externally for the JPanel to paint
	String treasureStatus = "", fleeStatus = "";
	//Whether or not the explosion should be painted
	boolean animateExplosion = false;
	//The number of blocks to draw, the tiles that are seen 
	int visibleRange = 5;
	//Are we moving 'animating' the map?
	boolean movingMap = false;
	//The positions the map transitions through during animation
	int movingRowPos = 0, movingColumnPos = 0;
	//Have we successfully drawn the map?
	boolean drawnMap = false;
	//Should we be displaying the movement buttons?
	boolean displayMovementButtons = true;
	//Are we moving 'animating' the player moving?
	boolean moving = false;
	//Is the player attacking? Used to animate the player
	boolean attacking = false;
	//If we're animating the player moving, the positions which be transition through are these
	int animatey = 0, animatex = 0;
	//Various entity related images
	Image monster1, treasure, trap;
	//Tile related images
	Image fullImg, northImg, eastImg, southImg, westImg;
	//Images and integers to animate the player in idle stance
	BufferedImage spriteIdle;
	ArrayList<BufferedImage> spriteIdles;
	int spriteIdleCount = 0, spriteIdleTimer = 0;
	//Images and integers to animate the trap explosion
	BufferedImage explosion;
	ArrayList<BufferedImage> explosions;
	int explosionCount = 0;
	//Has the player won the game?
	boolean gameWon = false;
	
	/**
	 * Menu related items
	 */
	BufferedImage heart, gold;
	
	BufferedImage walking;
	ArrayList<BufferedImage> walkingImgs;
	int walkingCount = 0;
	int walkingTimer = 0;
	
	BufferedImage attackingImg;
	ArrayList<BufferedImage> attackingImgs;
	int attackingCount = 0;
	int attackingTimer = 0;
	
	/**
	 * Directional Buttons
	 */
	CustomButton upArrow, rightArrow, downArrow, leftArrow;
	
	BufferedImage unselectedArrowUp, selectedArrowUp;
	BufferedImage unselectedArrowRight, selectedArrowRight;
	BufferedImage unselectedArrowDown, selectedArrowDown;
	BufferedImage unselectedArrowLeft, selectedArrowLeft;
	
	/**
	 * Menu Buttons
	 */
	CustomButton attackButton, saveButton, fleeButton;
	
	BufferedImage unselectedAttackButton, selectedAttackButton;
	BufferedImage unselectedFleeButton, selectedFleeButton;
	BufferedImage unselectedSaveButton, selectedSaveButton;
	
	
	
	/**
	 * Misc. Buttons
	 */
	CustomButton mainMenuButton;
	BufferedImage unselectedMainMenuButton, selectedMainMenuButton;
	
	CustomButton helpButton;
	BufferedImage unselectedHelpButton, selectedHelpButton;
	

	
	int rangex, rangey;
	

	/**
	 * Constructor
	 * @param p, the Play in use
	 */
	public gameCanvas(Play p) {
		this.setLayout(null);
		this.play = p;
		actions = new Actions(p);
		this.setDoubleBuffered(true);

		try {
			loadImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		rangex = Math.min(4, play.p.posx);
		rangey = Math.min(4, play.p.posy);
		
		/**
		 * Instantiate Directional Buttons
		 */
		upArrow = new CustomButton((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)), 50, 50, unselectedArrowUp, selectedArrowUp, this, actions, 1);
		rightArrow = new CustomButton((blocksize * Math.min(4, play.p.posx)) + (blocksize * 2) / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, 50, 50, unselectedArrowRight, selectedArrowRight, this, actions, 2);
		downArrow = new CustomButton((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + (blocksize * 2) / 3, 50, 50, unselectedArrowDown, selectedArrowDown, this, actions, 3);
		leftArrow = new CustomButton((blocksize * Math.min(4, play.p.posx)), (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, 50, 50, unselectedArrowLeft, selectedArrowLeft, this, actions, 4);
		
		this.add(upArrow);
		this.add(rightArrow);
		this.add(downArrow);
		this.add(leftArrow);
		
		/**
		 * Instantiate Menu Buttons
		 */
	
		attackButton = new CustomButton(0, 0, 0, 0, unselectedAttackButton, selectedAttackButton, this, actions, 5);
		fleeButton = new CustomButton(0, 0, 0, 0, unselectedFleeButton, selectedFleeButton, this, actions, 6);
		saveButton = new CustomButton(0, 0, 0, 0, unselectedSaveButton, selectedSaveButton, this, actions, 7);
		
		this.add(attackButton);
		this.add(fleeButton);
		this.add(saveButton);
		
		/**
		 * Instantiate Misc. Buttons
		 */
		mainMenuButton = new CustomButton(0, 0, 0, 0, unselectedMainMenuButton, selectedMainMenuButton, this, actions, 8);
		this.add(mainMenuButton);
		
		helpButton = new CustomButton(0, 0, 0, 0, unselectedHelpButton, selectedHelpButton, this, actions, 9);
		this.add(helpButton);
		
	}
	
	
	public void loadImages() throws IOException {
		String imageDirectory = new java.io.File(".").getCanonicalPath() + "\\res\\images\\";
		imageDirectory = imageDirectory.replace('\\', '/');
		
		/**
		 * Misc. entity images
		 */
		monster1 = new ImageIcon(imageDirectory + "monster.png").getImage();
		treasure = new ImageIcon(imageDirectory + "chest.png").getImage();
		heart = ImageIO.read(new File(imageDirectory + "heart.png")); 
		gold = ImageIO.read(new File(imageDirectory + "gold.gif"));
		
		/**
		 * Tile images
		 */
		fullImg = new ImageIcon(imageDirectory + "full.png").getImage();
		northImg = new ImageIcon(imageDirectory + "north.png").getImage();
		eastImg = new ImageIcon(imageDirectory + "east.png").getImage();
		southImg = new ImageIcon(imageDirectory + "south.png").getImage();
		westImg = new ImageIcon(imageDirectory + "west.png").getImage();
		
		/**
		 * Set Up Idle Animation Image Array
		 */
		spriteIdles = new ArrayList<BufferedImage>();
		spriteIdle = ImageIO.read(new File(imageDirectory + "spriteIdle.png")); 
		spriteIdles.add(spriteIdle.getSubimage(0, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(32, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(64, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(96, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(128, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(160, 0, 32, 32));
		spriteIdles.add(spriteIdle.getSubimage(224, 0, 32, 32));
		
		/**
		 * Set up walking animation image array
		 */
		walkingImgs = new ArrayList<BufferedImage>();
		walking = ImageIO.read(new File(imageDirectory + "walking.png")); 
		walkingImgs.add(walking.getSubimage(0, 0, 32, 32));
		walkingImgs.add(walking.getSubimage(32, 0, 32, 32));
		walkingImgs.add(walking.getSubimage(64, 0, 32, 32));
		walkingImgs.add(walking.getSubimage(96, 0, 32, 32));
		walkingImgs.add(walking.getSubimage(128, 0, 32, 32));
		walkingImgs.add(walking.getSubimage(160, 0, 32, 32));
		
		/**
		 * Set up attack animation image Array
		 */
		attackingImgs = new ArrayList<BufferedImage>();
		attackingImg = ImageIO.read(new File(imageDirectory + "attack.png")); 
		attackingImgs.add(attackingImg.getSubimage(0, 0, 32, 32));
		attackingImgs.add(attackingImg.getSubimage(32, 0, 32, 32));
		attackingImgs.add(attackingImg.getSubimage(64, 0, 32, 32));
		attackingImgs.add(attackingImg.getSubimage(96, 0, 32, 32));
		attackingImgs.add(attackingImg.getSubimage(128, 0, 32, 32));

		/**
		 * Set Up Explosion Animation Image Array
		 */
		explosions = new ArrayList<BufferedImage>();
		explosion = ImageIO.read(new File(imageDirectory + "BombExploding.png"));
		explosions.add(explosion.getSubimage(224, 0, 32, 64));
		explosions.add(explosion.getSubimage(256, 0, 32, 64));
		explosions.add(explosion.getSubimage(288, 0, 32, 64));
		explosions.add(explosion.getSubimage(320, 0, 32, 64));
		explosions.add(explosion.getSubimage(352, 0, 32, 64));
		
		/**
		 * Instantiate Directional Key Images
		 */
		unselectedArrowUp = ImageIO.read(new File(imageDirectory + "unselectedArrowUp.png"));
		selectedArrowUp = ImageIO.read(new File(imageDirectory + "selectedArrowUp.png"));
		
		unselectedArrowRight = ImageIO.read(new File(imageDirectory + "unselectedArrowRight.png"));
		selectedArrowRight = ImageIO.read(new File(imageDirectory + "selectedArrowRight.png"));

		unselectedArrowDown = ImageIO.read(new File(imageDirectory + "unselectedArrowDown.png"));
		selectedArrowDown = ImageIO.read(new File(imageDirectory + "selectedArrowDown.png"));
		
		unselectedArrowLeft = ImageIO.read(new File(imageDirectory + "unselectedArrowLeft.png"));
		selectedArrowLeft = ImageIO.read(new File(imageDirectory + "selectedArrowLeft.png"));
		
		/**
		 * Instantiate Menu Button Images
		 */
		unselectedAttackButton = ImageIO.read(new File(imageDirectory + "unselectedAttackButton.png"));
		selectedAttackButton = ImageIO.read(new File(imageDirectory + "selectedAttackButton.png"));
		
		unselectedFleeButton = ImageIO.read(new File(imageDirectory + "unselectedFleeButton.png"));
		selectedFleeButton = ImageIO.read(new File(imageDirectory + "selectedFleeButton.png"));
		
		unselectedSaveButton = ImageIO.read(new File(imageDirectory + "unselectedSaveButton.png"));
		selectedSaveButton = ImageIO.read(new File(imageDirectory + "selectedSaveButton.png"));
		
		/**
		 * Instantiate Misc. Button Images
		 */
		unselectedMainMenuButton = ImageIO.read(new File(imageDirectory + "unselectedMainMenuButton.png"));
		selectedMainMenuButton = ImageIO.read(new File(imageDirectory + "selectedMainMenuButton.png"));
		
		unselectedHelpButton = ImageIO.read(new File(imageDirectory + "unselectedHelpButton.png")); 
		selectedHelpButton = ImageIO.read(new File(imageDirectory + "selectedHelpButton.png")); 
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, play.getWidth(), play.getHeight());

		/**
		 * Drawing the Static Map
		 */
		if(!movingMap) {
		
		int rowPos = 0;
		int columnPos = 0;

		int rangey = Math.max(0, (play.p.posy - 4));
		
		for(int i = rangey; i <= rangey + 4; i++) {
			if(i < play.m.tiles.size()) {
			List<Tile> k = play.m.tiles.get(i);
			int rangex = Math.max(0, (play.p.posx - 4));

			for(int p = rangex; p <= rangex + 4; p++) {
				if(p < k.size()) {
				Tile t = k.get(p);
				
				if(!t.isNull() && t.isFound()) {
					g.drawImage(fullImg, rowPos, columnPos, blocksize, blocksize, this);
					if(!t.hasNorthTile()) g.drawImage(northImg, rowPos, columnPos, blocksize, blocksize, this);
					if(!t.hasEastTile()) g.drawImage(eastImg, rowPos, columnPos, blocksize, blocksize, this);
					if(!t.hasSouthTile()) g.drawImage(southImg, rowPos, columnPos, blocksize, blocksize, this);
					if(!t.hasWestTile()) g.drawImage(westImg, rowPos, columnPos, blocksize, blocksize, this);
				} else {
					g.fillRect(rowPos, columnPos, blocksize, blocksize);						
				}
				rowPos += blocksize;
				}

			} 
			 
			rowPos = 0;
			columnPos += blocksize;
			}
			}
		
		/**
		 * Animate the Map Moving
		 */
		} else if(movingMap) {
			int rowPos = movingRowPos; //decrement this to - blocksize
			int columnPos = movingColumnPos;
			
			int rangey = Math.max(0, (play.p.previousPosy - 4));
			int maxRangey = rangey + 4;
			if(play.p.direction == play.p.DOWN) {
				rangey = Math.max(0, (play.p.previousPosy - 4));
				maxRangey = rangey + 5;
			} else if(play.p.direction == play.p.UP) {
				rangey = Math.max(0, (play.p.previousPosy - 4)) - 1;
				maxRangey = rangey + 5;
			}
			
			for(int i = rangey; i <= maxRangey; i++) {
				if(i < play.m.tiles.size() && i >= 0) {
				List<Tile> k = play.m.tiles.get(i);
				
				int rangex = Math.max(0, (play.p.previousPosx - 4));
				int maxRangex = rangex + 4;
				
				if(play.p.direction == play.p.LEFT) {
					rangex = Math.max(0, (play.p.previousPosx - 4)) - 1;
					maxRangex = rangex + 5;
				
				} else if(play.p.direction == play.p.RIGHT) {
					rangex = Math.max(0, (play.p.previousPosx - 4));
					maxRangex = rangex + 5;
				}
				
				
				
				for(int p = rangex; p <= maxRangex; p++) {
					if(p < k.size() && p >= 0) {
					Tile t = k.get(p);
					
					if(!t.isNull() && t.isFound()) {
						g.drawImage(fullImg, rowPos, columnPos, blocksize, blocksize, this);
						if(!t.hasNorthTile()) g.drawImage(northImg, rowPos, columnPos, blocksize, blocksize, this);
						if(!t.hasEastTile()) g.drawImage(eastImg, rowPos, columnPos, blocksize, blocksize, this);
						if(!t.hasSouthTile()) g.drawImage(southImg, rowPos, columnPos, blocksize, blocksize, this);
						if(!t.hasWestTile()) g.drawImage(westImg, rowPos, columnPos, blocksize, blocksize, this);
					} else {
						g.fillRect(rowPos, columnPos, blocksize, blocksize);						
					}
					rowPos += blocksize;
					} 

				} 
				 
				rowPos = movingRowPos;
				columnPos += blocksize;
				}
				}
			drawnMap = true;
			
		}
		
		
		/**
		 * Animate player's various stances
		 */
		if(attacking) 
			paintAttackingPlayer(g);
		else if(!attacking && !moving) 
			paintIdlePlayer(g);
		else if(moving) 
			paintWalkingPlayer(g, animatex, animatey);
			
	
		
		/**
		 * Draw borders beyond visible range
		 */
		g.fillRect(visibleRange * blocksize, 0, 150, 150 * visibleRange); //Eastern border
		g.fillRect(0, blocksize * Math.min(visibleRange, play.m.rows), blocksize * Math.min(visibleRange, play.m.columns), 500); //Southern border
		
		
		
		
		if(play.encounter) {
			/**
			 * Draw The Player's Health Bar
			 */
			g.setColor(Color.RED);
			g.fillRect((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 4, 40,  10);
			
			g.setColor(Color.GREEN);
			g.fillRect((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 4, (int) (play.p.hp * 0.4),  10);
			
			/**
			 * Draw Monster's Health Bar
			 */
			g.setColor(Color.RED);
			g.fillRect(play.encounteredMonster.drawPosx, play.encounteredMonster.drawPosy, 40,  10);

			g.setColor(Color.GREEN);
			float hptowidth = 40 / (float) play.encounteredMonster.originalhp; 
			g.fillRect(play.encounteredMonster.drawPosx, play.encounteredMonster.drawPosy, (int) (play.encounteredMonster.hp * hptowidth),  10);
			
		}
		

		
		/**
		 * Menu box
		 */
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, blocksize * visibleRange,  blocksize * visibleRange, 100);
		g.setColor(Color.BLACK);
		g.fillRect(10, (blocksize * visibleRange + 10) ,  (blocksize * visibleRange - 20), 80);
	
		/**
		 * Health status
		 */
		g.drawImage(heart, 20, (blocksize * visibleRange + 20), 60, 60, this);
		
		g.setFont(new Font("arial", Font.BOLD, 20)); 
		String spacing = "";
		if(play.p.hp < 100) spacing = " ";
		
		paintShadowedString(spacing + Integer.toString(play.p.hp), 32, (blocksize * visibleRange + 54), g, new Font("arial", Font.BOLD, 20));
	
		/**
		 * Gold status
		 */
		g.drawImage(gold, 80, (blocksize * visibleRange + 20), 60, 60, this);
		if(play.p.hp < 100) spacing = " ";
		paintShadowedString(spacing + Integer.toString(play.p.ap), 95, (blocksize * visibleRange + 54), g, new Font("arial", Font.BOLD, 20));
		
		/**
		 * Display/Hide Menu Buttons
		 */
		helpButton.customSetBounds(670, (blocksize * visibleRange + 20), 60, 60);
		helpButton.paintComponent(g);
		
		if(play.encounter) {
			attackButton.customSetBounds(145, (blocksize * visibleRange + 20), 60, 60); 
			attackButton.paintComponent(g);
			fleeButton.customSetBounds(215, (blocksize * visibleRange + 20), 60, 60);
			fleeButton.paintComponent(g);
			saveButton.hide(); 

		} else if (!play.encounter) {
			attackButton.hide(); 
			fleeButton.hide();
			saveButton.customSetBounds(285, (blocksize * visibleRange + 20), 60, 60);
			saveButton.paintComponent(g);

		}
		

		
		/**
		 * Display status message
		 */
		switch(play.m.tiles.get(play.p.posy).get(play.p.posx).getType()) {
		case TRAP : 
			paintStatus(g, "You encountered a bomb!");
			break;
		case MONSTER : {
			paintStatus(g, "You encountered a monster, fight!");
			paintMonster(g);
			}
			break;
		case TREASURE :
			paintStatus(g, "You found " + treasureStatus);
			break;
		}
	
		if(!fleeStatus.equals("")) {
			paintStatus(g, fleeStatus, 80);
		}
		
		
		/**
		 * Paint player interaction
		 */
		if(animateExplosion) {
			paintExplosion(g, (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 4, explosionCount);
			
		} if(play.m.tiles.get(play.p.posy).get(play.p.posx).getType() == TREASURE && !moving && !movingMap) {
			paintTreasure(g);
		}
		
		
		/**
		 * Paint Damage Done
		 */
		if(play.encounter && !moving) {
			//Player Damage:
			String damage = "";
			if(play.p.damageTaken > 0) damage = "-" + Integer.toString(play.p.damageTaken);
			paintShadowedString(damage, (blocksize * Math.min(4, play.p.posx)) + (blocksize * 2) / 5, (blocksize * Math.min(4, play.p.posy)) + blocksize / 2, g, new Font("arial", Font.BOLD, 15));
			
			//Monster Damage:
			damage = "";
			if(play.encounteredMonster.damageTaken > 0) damage = "-" + Integer.toString(play.encounteredMonster.damageTaken);
			paintShadowedString(damage, play.encounteredMonster.drawPosx + 8, play.encounteredMonster.drawPosy + 35, g, new Font("arial", Font.BOLD, 15));
		}
		
		/**
		 * Draw Directional Buttons
		 */
		Tile t = play.m.tiles.get(play.p.posy).get(play.p.posx);
		
		if(displayMovementButtons && !play.encounter && !movingMap && !moving) {
			if(t.hasNorthTile()) {
				upArrow.customSetBounds((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)), 50, 50);
				upArrow.paintComponent(g);
			} else if(!t.hasNorthTile()) {
				upArrow.hide();
			}
			
			if(t.hasEastTile()) {
				rightArrow.customSetBounds((blocksize * Math.min(4, play.p.posx)) + (blocksize * 2) / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, 50, 50);
				rightArrow.paintComponent(g);
			} else if(!t.hasEastTile()) {
				rightArrow.hide();
			}
	
			if(t.hasSouthTile()) {
				downArrow.customSetBounds((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + (blocksize * 2) / 3, 50, 50);
				downArrow.paintComponent(g);
			} else if(!t.hasEastTile()) {
				downArrow.hide();
			}
			
			if(t.hasWestTile()) {
				leftArrow.customSetBounds((blocksize * Math.min(4, play.p.posx)), (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, 50, 50);
				leftArrow.paintComponent(g);
			} else if(!t.hasEastTile()) {
				leftArrow.hide();
			}
		} else {
			upArrow.hide();
			rightArrow.hide();
			downArrow.hide();
			leftArrow.hide();
		}
		
		
		
		/**
		 * Paint Game Over Screen
		 */
		if(play.p.hp <= 0) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0,  (blocksize * play.m.maze.get(0).size()), (blocksize * play.m.maze.get(0).size()) + 100);
			paintStatus(g, "GAME OVER! " + play.p.name + " has died!");
			
			mainMenuButton.customSetBounds(380, (blocksize * 4) + 100, 120, 60);
			mainMenuButton.paintComponent(g);
			
			//Hide all other buttons
			upArrow.hide();
			rightArrow.hide();
			downArrow.hide();
			leftArrow.hide();
			attackButton.hide();
			fleeButton.hide();
			saveButton.hide();
			helpButton.hide();
 
		} 
		
		
		/**
		 * Paint game win screen, win if all tiles were found and are now empty
		 */
		if(gameWon) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0,  (blocksize * play.m.maze.get(0).size()), (blocksize * play.m.maze.get(0).size()) + 100);
			paintStatus(g, "Well done " + play.p.name + " you cleared the level!");
			
			mainMenuButton.customSetBounds(380, (blocksize * 4) + 100, 120, 60);
			mainMenuButton.paintComponent(g);
		}
		
	}
	
	
	/**
	 * Pains a treasure chest onto the screen by the player
	 * @param g
	 */
	public void paintTreasure(Graphics g) {
		g.drawImage(treasure, (blocksize * Math.min(4,play.p.posx)) + blocksize / 2, (blocksize * Math.min(4,play.p.posy)) + (blocksize) / 2, blocksize / 4, blocksize / 4, this); 
	}
	

	/**
	 * Draw monster based on preference and available space
	 */
	//	g.fillRect((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 4, 40,  10);

	public void paintMonster(Graphics g) {
		if(play.encounter == true) {
			if(play.m.eastTileExists(play.p.posy, play.p.posx)) {
				g.drawImage(monster1, (blocksize * Math.min(4, play.p.posx)) + (blocksize * 2) / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, blocksize / 4, blocksize / 4, this); 
				play.encounteredMonster.setDrawPosition((blocksize * Math.min(4, play.p.posx)) + (blocksize * 2) / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 4);
				
			} else if(play.m.westTileExists(play.p.posy, play.p.posx)) {
				g.drawImage(monster1, (blocksize * Math.min(4, play.p.posx)), (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, blocksize / 4, blocksize / 4, this); 
				play.encounteredMonster.setDrawPosition((blocksize * Math.min(4, play.p.posx)), (blocksize * Math.min(4, play.p.posy)) + blocksize / 4);
				
			} else if(play.m.southTileExists(play.p.posy, play.p.posx)) {
				g.drawImage(monster1, (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + (blocksize * 2) / 3, blocksize / 4, blocksize / 4, this); 
				play.encounteredMonster.setDrawPosition((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + (blocksize * 2) / 4);
				
			} else if(play.m.northTileExists(play.p.posy, play.p.posx)) {
				g.drawImage(monster1, (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)), blocksize / 4, blocksize / 4, this); 
				play.encounteredMonster.setDrawPosition((blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + (blocksize * 2) / 4);
				
			}
		}
	}

	
	/**
	 * Self Animates the Idle Player
	 * @param g
	 */
	public void paintIdlePlayer(Graphics g) {
		if(spriteIdleCount == 6) spriteIdleCount = 0;
		g.drawImage(spriteIdles.get(spriteIdleCount), (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, blocksize / 4, blocksize / 4, this);
		spriteIdleTimer += 10;
		
		if(spriteIdleTimer == 300) {
			spriteIdleCount++;
			spriteIdleTimer = 0;
		}
		
		repaint();
		
	}
	
	/**
	 * Animates the Player Walking
	 * @param g
	 * @param drawplayerx
	 * @param drawplayery
	 */
	public void paintWalkingPlayer(Graphics g, int drawplayerx, int drawplayery) {
		if(walkingCount == 5) walkingCount = 0;
		g.drawImage(walkingImgs.get(walkingCount), animatex, drawplayery, blocksize / 4, blocksize / 4, this);
		walkingTimer += 10;
		
		if(walkingTimer == 300) {
			walkingCount++;
			walkingTimer = 0;
		}
		
		repaint();
	}
	
	
	public void paintStaticWalkingPlayer(Graphics g) {
		if(walkingCount == 5) {
			walkingCount = 0;
			moving = false;
		}
		moving = true;
		g.drawImage(walkingImgs.get(walkingCount), (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, blocksize / 4, blocksize / 4, this);
		walkingTimer += 10;
		
		if(walkingTimer == 300) {
			walkingCount++;
			walkingTimer = 0;
		}
		
		repaint();
	}
	
	/**
	 * Self Animates the Player Attacking
	 * @param g
	 */
	public void paintAttackingPlayer(Graphics g) {
		if(attackingCount == 4) {
			attackingCount = 0;
			attacking = false;
		}
		g.drawImage(attackingImgs.get(attackingCount), (blocksize * Math.min(4, play.p.posx)) + blocksize / 3, (blocksize * Math.min(4, play.p.posy)) + blocksize / 3, blocksize / 4, blocksize / 4, this);
		attackingTimer += 10;
		
		
		if(attackingTimer % 300 == 0) {
			attackingCount++;
			attackingTimer = 0;
		}
		
		repaint();
	}
	
	
	/**
	 * Paints an explosion onto the screen
	 * @param g
	 * @param x
	 * @param y
	 * @param frame, the explosion image
	 */
	public void paintExplosion(Graphics g, int x, int y, int frame) {
			g.drawImage(explosions.get(frame), x, y, 32, 64, this);
	}

	
	/**
	 * Paint a status message at a standard position
	 * @param g
	 * @param s
	 */
	public void paintStatus(Graphics g, String s) {
		paintShadowedString(s, 380, (blocksize * visibleRange + 50), g, new Font("arial", Font.BOLD, 14));
	}
	
	/**
	 * Paints a string with a custom message and custom y position
	 * @param g
	 * @param s
	 * @param y
	 */
	public void paintStatus(Graphics g, String s, int y) {
		paintShadowedString(s, 380, (blocksize * visibleRange + y), g, new Font("arial", Font.BOLD, 14));
	}
	
	
	
	/**
	 * Paints a string with a shadowed effect
	 * @param s
	 * @param x
	 * @param y
	 * @param g
	 */
	public void paintShadowedString(String s, int x, int y, Graphics g, Font f) {
		g.setFont(f); 

		g.setColor(Color.BLACK);
		g.drawString(s, x - 1, y - 1);
		g.drawString(s, x - 1, y + 1);
		g.drawString(s, x + 1, y - 1);
		g.drawString(s, x + 1, y + 1);
		
		g.setColor(Color.WHITE);
		g.drawString(s, x, y);
	}

}
