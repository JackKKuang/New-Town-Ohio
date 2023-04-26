/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class PlayerEntity extends Entity {
	private long lastCollide;
	private long collisionInterval = 1000;

	private Game Game; // the Game in which the ship exists

	/*
	 * construct the player's ship input: Game - the Game in which the ship is being
	 * created ref - a string with the name of the image associated to the Sprite
	 * for the ship x, y - initial location of ship
	 */
	public PlayerEntity(Game g, String r, int newX, int newY) {
		super(r, newX, newY); // calls the constructor in Entity
		Game = g;
	} // constructor

	/*
	 * move input: delta - time elapsed since last move (ms) purpose: move ship
	 */
	public void move(long delta) {
		// calls move from Entity
		super.move(delta);
		// makes sure the player can't leave the screen
		if (x < 0) {
			x = 1;
		} // if
		if (x + sprite.getWidth() > 1366) {
			x = 1365 - sprite.getWidth();
		} // if
		if (y + sprite.getHeight() > 768) {
			y = 767 - sprite.getHeight();
		} // if
		if (y < 0) {
			y = 1;
		} // if
	} // move

	public void doLogic() {

	} // doLogic

	/*
	 * collidedWith input: other - the Entity with which the ship has collided
	 * purpose: notification that the player's ship has collided with something
	 */
	public void collidedWith(Entity other) {
		
		//makes sure the invulnerability of the player has worn off
		if ((System.currentTimeMillis() - getLastCollide()) < collisionInterval) {
			return;
		} // if

		setLastCollide(System.currentTimeMillis());
		
		// if an enemy hit the player, reduce the player and the enemies health by one!
		if (other instanceof EnemyEntity) {
			((EnemyEntity) other).healthLost();
			Game.health();
			if(((EnemyEntity) other).getHealth() >= 0) {
				Game.notifyAlienKilled(other);
			} // if
		} // if
		
		//checks if an enemy bullet has hit the player
		if (other instanceof ShotEntity) {
			if (((ShotEntity) other).getShotType() == 0 || ((ShotEntity) other).getShotType() == 4)
				Game.health();
				Game.removeEntity(other);
		} // if
	} // collidedWith

	public long getLastCollide() {
		return lastCollide;
	} //getLastCollide

	public void setLastCollide(long lastCollide) {
		this.lastCollide = lastCollide;
	} //lastCollide

	public long getCollisionInterval() {
		return collisionInterval;
	} //setCollisionInterval

	public void setCollisionInterval(long collisionInterval) {
		this.collisionInterval = collisionInterval;
	} //collisionInterval


} // ShipEntity class