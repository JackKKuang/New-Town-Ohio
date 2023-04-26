/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class WeaponEntity extends Entity {

  private Game Game; // the Game in which the ship exists
  private int shotType;

  /* construct the player's ship
   * input: Game - the Game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the Sprite for the ship
   *        x, y - initial location of ship
   */
  public WeaponEntity(Game g, String r, int newX, int newY, int shotType) {
    super(r, newX, newY);  // calls the constructor in Entity
    Game = g;
    this.shotType = shotType;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move ship 
   */
  public void move (long delta){
    // stop at left side of screen

    super.move(delta);  // calls the move method in Entity
  } // move
  
  public void doLogic() {
	  
	  //dy -= -9.81*8;
	  
  } //doLogic
  
  /* collidedWith
   * input: other - the Entity with which the ship has collided
   * purpose: notification that the player's ship has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     if (other instanceof PlayerEntity) {
    	 Game.setWeaponUnlock(shotType);
    	 Game.removeEntity(this);
     } // if
   } // collidedWith    

} // ShipEntity class