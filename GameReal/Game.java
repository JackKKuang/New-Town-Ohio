/*********************************************************************************
Program Name: Game
Author: Nate Morley, Jack Kuang, Angie Chen
Date: April 24 2023
Purpose: A fun challenging runnable, dungeon crawler game.
*********************************************************************************/

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.*;
import java.awt.event.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class Game extends Canvas {

	private BufferStrategy strategy; // take advantage of accelerated graphics
	private boolean waitingForKeyPress = true; // true if Game held up until
												// a key is pressed
	Color background = new Color(227, 194, 125); // Color white

	private boolean leftPressed = false; // true if left arrow key currently pressed
	private boolean rightPressed = false; // true if right arrow key currently pressed
	private boolean firePressed = false; // true if firing
	private boolean upPressed = false; // true if up arrow currently pressed
	private boolean downPressed = false; // true if down arrow currently pressed
	public double mouseY;
	private double mouseX;
	private int enemyCount = 0;
	private int level = 1;
	static String fileName = "level2.txt";

	static boolean gameOn = false;
	private double musicLength = 46;
	private double lastMusicPlayed;

	private boolean gameRunning = true;
	private boolean invulnerable = true;
	private ArrayList entities = new ArrayList(); // list of entities
													// in Game
	private ArrayList enemyEntities = new ArrayList(); // list of entities
	private ArrayList removeEntities = new ArrayList(); // list of entities
														// to remove this loop
	private Entity player; // the player
	private double moveSpeed = 400; // hor. vel. of player (px/s)
	private long lastFire = 0;
	private long firingInterval = 300; // interval between shots (ms)
	private int shotType = 1;

	private long lastEnemyFire = 0;
	private int enemyFiringInterval = 900;
	private long bossFireStart = 0;
	private long lastBossFire;
	private int bossFiringInterval = 5000;

	private String message = ""; // message to display while waiting
									// for a key press
	private TileController controller = new TileController();
	protected CheckForCollision collision = new CheckForCollision();
	private int numberOfHearts = 3;
	private int screenY = 768;
	private int screenX = 1366;

	boolean weapon1Unlocked = false;
	private boolean weapon2Unlocked = false;
	private boolean weapon3Unlocked = false;

	private int entityX;
	private int entityY;

	private Sprite heartSprite = null;

	private boolean logicRequiredThisLoop = false; // true if logic
													// needs to be
													// applied this loop

	/*
	 * Construct our Game and set it running.
	 */
	public Game() {
		// create a frame to contain Game
		JFrame container = new JFrame("New town Ohio");

		// get hold the content of the frame
		JPanel panel = (JPanel) container.getContentPane();

		// set up the resolution of the Game
		panel.setPreferredSize(new Dimension(screenX, screenY));
		panel.setLayout(null);

		// set up canvas size (this) and add to frame
		setBounds(0, 0, screenX, screenY);
		panel.add(this);

		// Tell AWT not to bother repainting canvas since that will
		// be done using graphics acceleration
		setIgnoreRepaint(true);

		container.pack();
		container.setResizable(false);
		container.setVisible(true);

		// if user closes window, shutdown Game and jre
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			} // windowClosing
		});

		// add key listener to this canvas

		MouseEvents mouseMasterMind = new MouseEvents();

		addKeyListener(new KeyInputHandler());
		addMouseListener(mouseMasterMind);
		addMouseMotionListener(mouseMasterMind);

		// request focus so key events are handled by this canvas
		requestFocus();
		
		// create buffer strategy to take advantage of accelerated graphics
		createBufferStrategy(2);
		strategy = getBufferStrategy();

		// initialize entities
		initEntities();
		// initialize heart
		heartSprite = (SpriteStore.get()).getSprite("sprites/heart.png");

		// gives the player three lives
		numberOfHearts = 3;

		// start the Game
		gameLoop();
	} // constructor

	/*
	 * initEntities input: none output: none purpose: Initialise the starting state
	 * of the player and alien entities. Each Entity will be added to the array of
	 * entities in the Game.
	 */
	private void initEntities() {
		enemyCount = 0;

		// checks if the level is a boss level and spawns boss
		if (level % 3 == 0) {
			EnemyEntity enemy = new BossEntity(this, "sprites/boss.png", 388, 289, 14 * level);
			entities.add(enemy);
			fileName = "bosslevel.txt";
			enemyCount++;
		} // if

		// sets player hearts to 3 if they have died.
		if (numberOfHearts < 1) {
			numberOfHearts = 3;
		} // if

		enemyCount = 0;

		// create the player and put in center of screen
		player = new PlayerEntity(this, "sprites/player.png", 300, 400);
		Graphics2D g = (Graphics2D) strategy.getDrawGraphics();

		// sets sprite values to tiles and draws them
		controller.setTileImage(fileName);
		controller.drawTile(g);
		entities.add(player);

		//if its not the end screen
		if (level != 10) {
			// spawns enemies
			for (int i = 0; i < 5 + (int) Math.sqrt(level); i++) {
				int f = 0;

				// makes sure the enemy doesnt spawn in the walls.
				do {
					f = 0;
					entityY = (int) (Math.random() * 700 + 1);
					entityX = (int) (Math.random() * 1200 + 1);
					Tile Tile = controller.getTileAt(entityY / 35, entityX / 35);
					Tile tileNear = controller.getTileAt((entityY + 92) / 35, (entityX + 92) / 35);
					if (Tile.getCollision() > 0 || tileNear.getCollision() > 0) {
						f++;
						continue;
					} // if
				} while (f != 0);

				// creates enemy
				EnemyEntity enemy = new EnemyEntity(this, "sprites/enemy.png", entityX, entityY, 3);
				entities.add(enemy);
				enemyCount++;
			} // for
			((PlayerEntity) player).setLastCollide((System.currentTimeMillis()));
		} // if
	} // initEntities

	/*
	 * Notification from a Game Entity that the logic of the Game should be run at
	 * the next opportunity
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	} // updateLogic

	/*
	 * Remove an Entity from the Game. It will no longer be moved or drawn.
	 */
	public void removeEntity(Entity Entity) {
		removeEntities.add(Entity);
	} // removeEntity

	/*
	 * Notification that the player has died.
	 */
	public void notifyDeath() {

		// disables enemy shooting
		gameOn = false;

		// plays the death sound
		try {
			playSound("sounds/death.wav");
		} catch (Exception e) {

		} // catch

		message = "Good try!  Try again?";
		waitingForKeyPress = true;

	} // notifyDeath

	// health
	public void health() {

		// takes away one life from the player
		numberOfHearts--;

		// checks if the player has died
		if (numberOfHearts > 0) {
			updateLogic();
		} else {
			notifyDeath();
		} // else
	} // if

	/*
	 * Notification if the player has completed the floor.
	 */
	public void notifyWin() {
		gameOn = false;
		message = "Next Chamber!!!";

		// pick a random floor layout for the next level
		if(level != 10) {
			fileName = "level" + (int) (Math.random() * 3 + 1) + ".txt";
		} // if
		// remove all entities
		entities.removeAll(entities);
		waitingForKeyPress = true;

		// start the next level
		initEntities();
	} // notifyWin

	// use this for the deaths of enemies

	/*
	 * Notification than an alien has been killed
	 */

	public void notifyAlienKilled(Entity deadAlien) {
		int spawnChance = (int) (Math.random() * 12 + 1);
		enemyCount--;

		// A chance that an enemy drops loot when killed
		switch (spawnChance) {
		case 1:
			WeaponEntity flameThrower = new WeaponEntity(this, "sprites/pistol.png", deadAlien.getX(), deadAlien.getY(),
					2);
			entities.add(flameThrower);
			break;
		case 2:
			numberOfHearts++;
			break;
		case 3:
			WeaponEntity pistol = new WeaponEntity(this, "sprites/slimegun.png", deadAlien.getX(), deadAlien.getY(), 3);
			entities.add(pistol);
			break;
		default:
			break;
		} // switch

	} // notifyAlienKilled

	// alienTryToFire
	public void alienTryToFire() {

		// check if the enemies can shoot
		if (gameRunning && gameOn == true) {
			if ((System.currentTimeMillis() - lastEnemyFire) < enemyFiringInterval) {
				return;
			} // if

			// shoots from enemies towards the player
			for (int i = 0; i < entities.size(); i++) {
				Entity Entity = (Entity) entities.get(i);
				if (Entity instanceof EnemyEntity) {
					if ((int) (Math.random() * 2) == (int) (Math.random() * 2)) {
						Entity enemyFire = new ShotEntity(this, "sprites/bullet1.png", Entity.getX(), Entity.getY(),
								player.getX() + 20, player.getY() + 50, 0);

						entities.add(enemyFire);
						lastEnemyFire = System.currentTimeMillis();
					} // if
				} // if
			} // for
		} // for
	} // notifyAlienKilled

	// bossTryToFire
	public void bossTryToFire() {

		// check if boss can shoot
		if (gameOn == true) {
			if ((System.currentTimeMillis() - lastBossFire) < bossFiringInterval) {
				return;
			} // if

			// shoot waves of bullets towards the player
			else {
				for (int i = 0; i < entities.size(); i++) {
					Entity Entity = (Entity) entities.get(i);
					if (Entity instanceof BossEntity) {
						bossFireStart = System.currentTimeMillis();
						if ((System.currentTimeMillis() - bossFireStart) < 6000) {
							int y = 0;
							int x = 0;

							// shoot two waves in a V shape towards the player
							for (int j = 0; j < 10 * Math.sqrt(level); j++) {
								Entity enemyFire = new ShotEntity(this, "sprites/bossbullet.png", Entity.getX(),
										Entity.getY(), player.getX() + x, player.getY() + y, 4);
								entities.add(enemyFire);
								Entity enemyFire2 = new ShotEntity(this, "sprites/bossbullet.png", Entity.getX(),
										Entity.getY(), -1 * (player.getX() + x), -1 * (player.getY() + y), 4);
								entities.add(enemyFire2);

								if (j == 5) {
									y = -250;
								} // if
								y += 50;
							} // for
						} // if
						lastBossFire = System.currentTimeMillis();
					} // if
				} // for
			} // else
		} // if
	} // bossTryToFire

	// tryToFire
	public void tryToFire() {

		// check if it has been long enough since the last shot
		if (gameOn == true) {
			if ((System.currentTimeMillis() - lastFire) < firingInterval) {
				return;
			} // if
			try {
				playSound("sounds/pop.wav");
			} catch (Exception e) {
				System.out.println(e);
			} // catch

			// checks
			switch (shotType) {
			case 1:
				Entity shot1 = new ShotEntity(this, "sprites/bullet3.png", player.getX() + 20, player.getY() + 50,
						mouseX, mouseY, 1);
				entities.add(shot1);
				lastFire = System.currentTimeMillis();
				return;
			case 2:
				Entity shot2 = new ShotEntity(this, "sprites/bullet2.png", player.getX() + 20, player.getY() + 50,
						mouseX, mouseY, 2);
				entities.add(shot2);
				lastFire = System.currentTimeMillis();
				return;
			case 3:
				Entity shot3 = new ShotEntity(this, "sprites/slimeshot.png", player.getX() + 20, player.getY() + 50,
						mouseX, mouseY, 3);
				entities.add(shot3);
				lastFire = System.currentTimeMillis();
				return;
			} // switch

		} // if

	} // tryToFire

	public void nextLevel() {
		gameOn = true;

		// progresses to the next level
		this.level++;
		if (level == 9) {
			fullWin();
			return;
		} // if
		if (level == 10) {
			fileName = "game_end.txt";
		} // if
		if (level == 11) {
			System.exit(0);
		} // if
		notifyWin();
	} // if

	public void fullWin() {
		message = "THE FINAL LEVEL";
		waitingForKeyPress = true;
		fileName = "bosslevel.txt";
		initEntities();
	} // fullWin

	/*
	 * gameLoop input: none output: none purpose: Main Game loop. Runs throughout
	 * Game play. Responsible for the following activities: - calculates speed of
	 * the Game loop to update moves - moves the Game entities - draws the screen
	 * contents (entities, text) - updates Game events - checks input
	 */

	public void gameLoop() {
		long lastLoopTime = System.currentTimeMillis();
		playMusic();
		// keep loop running until Game ends
		while (gameRunning) {

			// calc. time since last update, will be used to calculate
			// entities movement
			long delta = System.currentTimeMillis() - lastLoopTime;
			lastLoopTime = System.currentTimeMillis();

			// get graphics context for the accelerated surface and make it black
			Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
			g.setColor(background);
			g.fillRect(0, 0, screenX, screenY);
			controller.setTileImage(fileName);
			controller.drawTile(g);

			// move each Entity
			if (!waitingForKeyPress) {

				for (int i = 0; i < entities.size(); i++) {
					// EnemyEntity EnemyEntity = entities.get(i);

					Entity Entity = (Entity) entities.get(i);
					if (!(Entity instanceof BossEntity)) {
						if (Entity.toString() == "enemy") {
							int[] movement = ((EnemyEntity) Entity).pathFindMovement(player, delta);

							Entity.dx = movement[0];
							Entity.dy = movement[1];

						} // if
					} // if
					Entity.move(delta);

				} // for
			} // if

			// draw all entities
			for (int i = 0; i < entities.size(); i++) {
				Entity Entity = (Entity) entities.get(i);
				Entity.draw(g);
			} // for

			// draws the hearts
			int heartX = 20;
			for (int i = 0; i < numberOfHearts; i++) {
				heartSprite.draw(g, heartX, 20);
				heartX += 70;
			} // for

			// brute force collisions, compare every Entity
			// against every other Entity. If any collisions
			// are detected notify both entities that it has
			// occurred
			for (int i = 0; i < entities.size(); i++) {
				for (int j = i + 1; j < entities.size(); j++) {
					Entity me = (Entity) entities.get(i);
					Entity him = (Entity) entities.get(j);

					if (me.collidesWith(him)) {
						me.collidedWith(him);
						him.collidedWith(me);
					} // if
				} // inner for
			} // outer for

			// remove dead entities
			entities.removeAll(removeEntities);
			removeEntities.clear();

			// run logic if required
			if (logicRequiredThisLoop) {
				for (int i = 0; i < entities.size(); i++) {
					Entity Entity = (Entity) entities.get(i);
					Entity.doLogic();
				} // for
				logicRequiredThisLoop = false;
			} // if

			// if waiting for "any key press", draw message
			if (waitingForKeyPress) {
				g.setColor(Color.white);
				g.drawString(message, (800 - g.getFontMetrics().stringWidth(message)) / 2, 250);
				g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key")) / 2, 300);
			} // if

			// clear graphics and flip buffer
			g.dispose();
			strategy.show();

			// player should not move without user input
			player.setHorizontalMovement(0);
			player.setVerticalMovement(0);

			// respond to user moving ship
			if ((leftPressed) && (!rightPressed)) {
				player.setHorizontalMovement(-moveSpeed);
			} // if
			else if ((rightPressed) && (!leftPressed)) {
				player.setHorizontalMovement(moveSpeed);
			} // else
			if ((upPressed) && (!downPressed)) {
				player.setVerticalMovement(-moveSpeed);
			} // if
			else if ((downPressed) && (!upPressed)) {
				player.setVerticalMovement(moveSpeed);
			} // else
			if ((firePressed)) {
				tryToFire();
			} // if
			player.doLogic();

			// This rotates the player to the cursor, we should move this to another place
			// later

			double magX = (mouseX - player.x) * -1;
			double magY = Math.abs(Math.abs(mouseY) - Math.abs(player.y)) * -1;
			if (mouseY < player.y) {
				magY = magY * -1;
			} // if

			// rotates player towards mouse
			player.changeRotation((int) (Math.toDegrees(Math.atan2(magY, magX)) + 75));

			// rotates enemies towards the player
			for (int i = 0; i < entities.size(); i++) {
				Entity Entity = (Entity) entities.get(i);
				if (Entity instanceof EnemyEntity) {

					double x = (player.x - Entity.x);
					double y = Math.abs(Math.abs(player.y) - Math.abs(Entity.y));

					if (player.y < Entity.y) {
						y = y * -1;
					} // if

					Entity.changeRotation((int) (Math.toDegrees(Math.atan2(y, x))));
				} // if
			} // for

			alienTryToFire();
			bossTryToFire();
			if (collision.checkWin(player, delta) && enemyCount <= 0) {

				nextLevel();

			} // if

			// pause
			try {
				Thread.sleep(10);
			} catch (Exception e) {
			} // catch

		} // while

	} // gameLoop

	/*
	 * startGame input: none output: none purpose: start a fresh Game, clear old
	 * data
	 */
	private void startGame() {

		// clear out any existing entities and initalize a new set
		entities.clear();

		initEntities();

		// blank out any keyboard settings that might exist
		leftPressed = false;
		rightPressed = false;
		firePressed = false;
		upPressed = false;
		downPressed = false;
	} // startGame

	// MouseEvents mouseEvents = new MouseEvents();
	// addMouseListener(MouseEvents);
	// addMouseMotionListener(MouseEvents);

	private class MouseEvents extends MouseInputAdapter {

		public void mousePressed(MouseEvent e) {

			mouseX = e.getX();
			mouseY = e.getY();
			firePressed = true;
		} // mousePressed

		public void mouseReleased(MouseEvent e) {
			firePressed = false;
		} // mouseReleased

		public void mouseEntered(MouseEvent e) {

		} // mouseEntered

		public void mouseExited(MouseEvent e) {

		} // mouseExited

		public void mouseClicked(MouseEvent e) {

		} // mouseClicked

		public void mouseMoved(MouseEvent e) {

			mouseX = e.getX();
			mouseY = e.getY();
		}

	} // MouseListener

	/*
	 * inner class KeyInputHandler handles keyboard input from the user
	 */
	private class KeyInputHandler extends KeyAdapter {

		private int pressCount = 1; // the number of key presses since
									// waiting for 'any' key press

		/*
		 * The following methods are required for any class that extends the abstract
		 * class KeyAdapter. They handle keyPressed, keyReleased and keyTyped events.
		 */
		public void keyPressed(KeyEvent e) {

			// if waiting for keypress to start Game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or fire
			if (e.getKeyCode() == KeyEvent.VK_A) {

				leftPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_W) {
				upPressed = true;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_S) {
				downPressed = true;
			} // if
			if (e.getKeyCode() == KeyEvent.VK_1) {
				shotType = 1;
			}
			if (e.getKeyCode() == KeyEvent.VK_2 && weapon2Unlocked) {
				shotType = 2;
			}
			if (e.getKeyCode() == KeyEvent.VK_3 && weapon3Unlocked) {
				shotType = 3;
			}

		} // keyPressed

		public void keyReleased(KeyEvent e) {
			// if waiting for keyPress to start Game, do nothing
			if (waitingForKeyPress) {
				return;
			} // if

			// respond to move left, right or fire
			if (e.getKeyCode() == KeyEvent.VK_A) {

				leftPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_D) {
				rightPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_W) {
				upPressed = false;
			} // if

			if (e.getKeyCode() == KeyEvent.VK_S) {
				downPressed = false;
			} // if

		} // keyReleased

		public void keyTyped(KeyEvent e) {

			// if waiting for key press to start Game
			if (waitingForKeyPress) {
				if (pressCount == 1) {
					waitingForKeyPress = false;
					gameOn = true;
					startGame();
					pressCount = 0;
				} else {
					pressCount++;
				} // else
			} // if waitingForKeyPress

			// if escape is pressed, end Game
			if (e.getKeyChar() == 27) {
				System.exit(0);
			} // if escape pressed

		} // keyTyped

	} // class KeyInputHandler

	// setWeaponUnlock
	public void setWeaponUnlock(int type) {
		try {
			playSound("sounds/collectWeapon.wav");
		} catch (Exception e) {
		} // catch

		// allows users to shoot with the gun they picked up
		switch (type) {
		case 1:
			weapon1Unlocked = true;
			break;
		case 2:
			weapon2Unlocked = true;
			break;
		case 3:
			weapon3Unlocked = true;
		} // switch
	} // setWeaponUnlock

	// playSound
	void playSound(String soundFile)
			throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
		File f = new File("./" + soundFile);
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	} // playSound

	/**
	 * Main Program
	 */

	// playMusic
	public void playMusic() {
		// checks if the song is over
		if ((System.currentTimeMillis() - lastMusicPlayed) < musicLength) {
			return;
		} // if
			// plays sound
		try {
			playSound("sounds/music.wav");
		} catch (Exception e) {
		} // catch
		lastMusicPlayed = System.currentTimeMillis();
	} // playMusic

	public static void main(String[] args) {
		// instantiate this object
		new Game();
	} // main
} // Game