import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotEntity extends Entity {

	private double moveSpeed = -300; // vertical speed shot moves
	private boolean used = false; // true if shot hits something
	private Game Game; // the Game in which the ship exists
	private int shotType; // type of bullet to set properties
	private int shotDistance = 0; // how far the bullet can travel before disappearing
	private int bulletSpeed = 1000; // determines how fast the bullet can travel in any direction
	private int bulletBounces = 0;

	/*
	 * construct the shot input: Game - the Game in which the shot is being created
	 * ref - a string with the name of the image associated to the Sprite for the
	 * shot x, y - initial location of shot
	 */
	public ShotEntity(Game g, String r, int newX, int newY, double directionX, double directionY, int shotType) {
		super(r, newX, newY); // calls the constructor in Entity
		this.setShotType(shotType);
		Game = g;
		this.shotType = shotType;
		
		// boss attakcs move slower
		if(shotType == 4) {
			bulletSpeed = 210;
		} // if
		
		if(shotType == 0) {
			bulletSpeed = 280;
		} // if

		dy = moveSpeed;
		dx = moveSpeed;
		int magX = (int) (directionX - newX);
		int magY = Math.abs(Math.abs((int) directionY) - Math.abs((int) newY));
		if (directionY < newY) {
			magY = magY * -1;
		}

		int total = Math.abs(magX) + Math.abs(magY);

		dx = (((double) magX) / ((double) total)) * bulletSpeed;
		dy = (((double) magY) / ((double) total)) * bulletSpeed;

	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move shot
	 */
	public void move(long delta) {

		// update location of Entity based on move speeds
		if (y <= 0) {
			Game.removeEntity(this);
		} // if
		if (y >= 1000) {
			Game.removeEntity(this);
		} // if
		if (x <= 0) {
			Game.removeEntity(this);
		} // if
		
		// if the bullet hits a wall on the x axis make it bounce back
		if (collision.checkTileX(this, delta) || shotType == 4) {
			x += (delta * dx) / 1000;
		} else {
			if (getShotType() == 0) {
				bulletBounces++;
				dx *= -1;
			} else {
				Game.removeEntity(this);
			} // else
			
			// if the bullet has bounced four times remove it
			if(bulletBounces >= 4) {
				Game.removeEntity(this);
			} // if
		} // else

		// checks for collision on the y axis
		if (collision.checkTileY(this, delta) || shotType == 4) {
			y += (delta * dy) / 1000;
		} else {
			Game.removeEntity(this);
		} // else
		
		// checks if the bullet has reached its max fire distance
		if (this.getShotType() == 2 && this.shotDistance > 17) {
			Game.removeEntity(this);
		} // else

		// if shot moves off top of screen, remove it from Entity list

		this.shotDistance++;

	} // move

	/*
	 * collidedWith input: other - the Entity with which the shot has collided
	 * purpose: notification that the shot has collided with something
	 */
	public void collidedWith(Entity other) {
		// prevents double kills

		if (used) {
			return;
		} // if

		// if it has hit an enemy, reduce its health
		// makes sure the bullet is shot from the player, and hits an enemy
		if (shotType != 4) {
			if (shotType != 0) {
				if (other instanceof EnemyEntity) {
					// remove affect entities from the Entity list
					Game.removeEntity(this);
					
					// remove enemy health based on bullet damage
					for (int i = 0; i < shotType; i++) {
						((EnemyEntity) other).healthLost();
					} // health

					// if the enemy has no health left, kill it!
					if (((EnemyEntity) other).health <= 0) {
						
						try {
							playSound("sounds/enemydeath.wav");
						}catch(Exception e) {							
						} // catch						
						Game.removeEntity(other);
						Game.notifyAlienKilled(other);
					} // if
						// notify the Game that the alien is dead
					used = true;
				} // if
			}
		} // if
	} // collidedWith
	
	// playSound
	void playSound(String soundFile) throws MalformedURLException, UnsupportedAudioFileException, IOException, LineUnavailableException {
	    File f = new File("./" + soundFile);
	    AudioInputStream audioIn = AudioSystem.getAudioInputStream(f.toURI().toURL());  
	    Clip clip = AudioSystem.getClip();
	    clip.open(audioIn);
	    clip.start();
	}// playSound
	
	public int getShotType() {
		return shotType;
	} // getShotType

	public void setShotType(int shotType) {
		this.shotType = shotType;
	} // setShotType

} // ShipEntity class