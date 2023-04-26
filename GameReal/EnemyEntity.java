/* AlienEntity.java
 * March 27, 2006
 * Represents one of the aliens
 */
public class EnemyEntity extends Entity {

  private double moveSpeed = 75; // horizontal speed
  private TileController control = new TileController();
  boolean obstruction;
  int health;
  
  private Game game; // the game in which the alien exists

  /* construct a new alien
   * input: game - the game in which the alien is being created
   *        r - the image representing the alien
   *        x, y - initial location of alien
   */
  public EnemyEntity(Game g, String r, int newX, int newY, int health) {
    super(r, newX, newY);  // calls the constructor in Entity
    this.health = health;
    game = g;
    //dx = -moveSpeed;  // start off moving left
  } // constructor

	@Override
	public void collidedWith(Entity other) {
		// TODO Auto-generated method stub
		
	}
	
	// This method calculates the delta x and delta y that the enemy should have, according to where the player is
	// An algorithm will draw a triangle (the path) between the enemy and player, and use trigonometry to proportionalize the dx and dy to create basic pathfinding
	public int[] pathFindMovement(Entity player, double delta) {
		
		// Initialize variables
		
		// Directions the triangle will be drawn from
		int xDirection = -1;
		int yDirection = 1;
		
		// Initialization of the triangle to be drawn
		int triangleX = 0;
		int triangleY = 0;
		
		// Boolean statement if there are no obstructions
		boolean noObstruction = true;
		
		// Create magnitude between enemy and player, then calculate the direction the triangle will attempt to draw from
		int magX = (int) (player.x - this.x);
		int magY = Math.abs(Math.abs((int) player.y) - Math.abs((int) this.y));
		if (player.y < this.y) {
			magY = magY * -1;
			yDirection  = -1;
		}//if
		
		if ((int)(player.y / 35) == (int)(this.y / 35)) {
			yDirection = 0;
		}//if
		
		if (player.x > this.x) {
			xDirection = 1;
		}//if
		
		// For the tile magnitude between the player and the enemy, check if there is a collision in the path
		for(int i = 0; i <= Math.abs(magX/35); i++) {
			
			// If there is no distance between the player and the enemy, break
			if(i == 0 && magX/35 == 0) {
				break;
			}//if
			
			try {
				
				// If there is a collision in the path of the enemy and player, call checkYTile to find a path in the obstruction (triangleY), then set x to the interval where there is an obstruction (triangleX)
				if(control.getTileAt((int)((this.y + 14)/35), (int)((this.x + 14 + 35 * i * xDirection)/35)).getCollision(1) == true) {

					triangleY = checkYTile(this, i, xDirection, yDirection);
					triangleX = i; // sets x
					
					// Call that there is an obstruction, this is used for the enemy to decide to move in the game
					noObstruction = false;
					obstruction = true;
					break;
				 }//if
			}catch(Exception e) {
				System.out.println("tile broken at " + (int)((this.y + 14)/35) + " " + (int)((this.x + 14 + 35 * i * xDirection)/35));
			}//catch
		}//for
		
		// If there is no xObstruction, check if there are any y obstructions
		if (noObstruction == true) {
			
			boolean yObstruction = false;
			obstruction = true;
			
			// Check each tile in between player and enemy
			for(int i = 0; i < Math.abs(magY/35); i++) {
				
				// Check if the tile exists, because sometimes it doesn't
				if(control.tileExists((int)((this.y + 14 + 35 * i * magY / Math.abs(magY))/35), (player.getX() + 14)/35) == true) {
					
					// If there is a collision, pathfind for x (triangleX), then set y (triangleY) to the interval
					if (control.getTileAt((int)((this.y + 14 + 35 * i * magY / Math.abs(magY))/35), (player.getX() + 14)/35).getCollision(1) == true){

						yObstruction = true;

							triangleX = checkXTile(this, i, magY / Math.abs(magY));
							triangleY = i * (magY / Math.abs(magY));
							break;
					}//if
				}//if
			}//for
			
			// If there are no obstructions, simply calculate x and y to the tile magnitude of the player and enemy
			if (yObstruction == false) {
				
				triangleX = Math.abs(magX/35);
				triangleY = magY / 35;
				obstruction = false;
			}//if

		}//if
		
		// Calculate the total tile distance of x and y, then proportionalize the speed of the enemy between dx and dy according to it
		double total = Math.abs(triangleX) + Math.abs(triangleY);
		int[] integer = {(int)(((double)triangleX) / total * 300 * xDirection), (int) (((double)triangleY) / total * 300)};
		
		// Finally, return the dx and dy
		return integer;
		
	}//pathFindMovement
	
	// Pathfind a y position where the enemy can move to, to calculate for triangleY
	private int checkYTile(EnemyEntity entity, int i, int xDirection, int yDirection) {
		
		// Initialize variables
		boolean disableUp = false;
		boolean disableDown = false;
		
		int up = 0;
		int down = 0;
		
		// Run a for loop to check both up and down of the entity, to see if there is a way to get to the player from it
		for(int v = 1; v < 15; v++) {
			
			// Check if disableUp is false and if the tile upwards of the interval exists
			if(disableUp == false && control.tileExists((int)((entity.y + 14 + 35 * v )/35), (int)((entity.x + 14 + 35 * i * xDirection) /35)) == true) {
				
				// Check if the position above the entity at this interval has any collisions, if so, disable checking up
				if(control.getTileAt((int)((entity.y + 14 + 35 * v)/35), (int)((entity.x + 14) / 35)).getCollision(1) == false) {
					
					// Check if the position above the entity at this interval one tile to the x direction of the destination has collision. If not, set up to the interval and disable checking up
					if(control.getTileAt((int)((entity.y + 14 + 35 * v )/35), (int)((entity.x + 14 + 35 * i * xDirection) /35)).getCollision(1) == false) {
						up = v;
						disableUp = true;
					}//if
				}else {
					disableUp = true;
				}//if else

			}
			
			// Check if disableDown is false and if the tile downwards of the interval exists
			if (disableDown == false && control.tileExists((int)((entity.y + 14 + 35 * -v )/35), (int)((entity.x + 14 + 35 * i * xDirection) /35)) == true) {
		
				// Check if the position below the entity at this interval has any collisions, if so, disable checking down
				if(control.getTileAt((int)((entity.y + 14 + 35 * -v)/35), (int)((entity.x + 14) / 35)).getCollision(1) == false) {
					
					// Check if the position below the entity at this interval one tile to the x direction of the destination has collision. If not, set up to the interval and disable checking down
					if (control.getTileAt((int)((entity.y + 14 + 35 * -v )/35), (int)((entity.x + 14 + 35 * i * xDirection) /35)).getCollision(1) == false) {
						down = -v;
						disableDown = true;
					}//if
				}else {
					disableDown = true;
				}//if else
			}//if
			
			// If both checks have been broken, break the for loop
			if(disableUp == true && disableDown == true) {
				break;
			}//if
		}//for

		// If there is a preferred direction for the path,
		if(yDirection != 0) {
			
			// If up, return up if not 0
			if(yDirection == 1 && up != 0) {

				return up;
				
			// If down, return down if not 0
			}else if(yDirection == -1 && down != 0) {
			
				return down;
				
			// If either up or down equals 0,
			}else if(up == 0 || down == 0 ) {
				
				// return down if up is 0
				if (up == 0) {
					return down;
				
				// else return up if down is 0
				}else {
					return up;
				}//if else
				
			// If the preferred direction does not have a path (is 0), decide based on which path is shorter
			}else if(Math.abs(up) > Math.abs(down)) {

				return down;
			}else if (Math.abs(down) > Math.abs(up)) {
				
				return up;
			}else {
				return 0;
			}// if else
			
		// If there is no preferred path direction, 
		}else {
			
			// If one equals zero, return based on the the one that is not
			if(up == 0 || down == 0 ) {
				
				if (up == 0) {
					return down;
				}else {
					return up;
				}
				
			// Otherwise, return the one with the lesser length
			}else if(Math.abs(up) > Math.abs(down)) {
	
				return down;
			}else if (Math.abs(down) > Math.abs(up)) {

				return up;
				
			// If both equal zero, return 0
			}else {
				return 0;
			}// if else
		}// if else
		
	}//checkYTile
	
	// Pathfind a x position where the enemy can move to, to calculate for triangleX
	private int checkXTile(EnemyEntity entity, int i, int yDirection) {
		
		// Initialize variables
		boolean disableLeft = false;
		boolean disableRight = false;
		
		// Run a for loop to check both left and right of the entity, to see if there is a way to get to the player from it
		for(int v = 0; v < 30; v++) {
			
			// If disabledRight is false, check if the position to the right according to the interval exists
			if(disableRight == false && control.tileExists((int)((entity.y + 14 + 35 * i * yDirection )/35), (int)((entity.x + 14 + 35 * v) /35)) == true) {
				
				// Then check if at this interval there is an obstruction. if not, return this interval
				if(control.getTileAt((int)((entity.y + 14 -35 + 35 * i * yDirection )/35), (int)((entity.x + 14 + 35 * v) /35)).getCollision(1) == false) {

					return v;
				
				// Otherwise, disable right side
				}else {
					disableRight = true;
				}//if else
					
					
			// Opposite 
			}if (disableLeft == false && control.tileExists((int)((entity.y + 14 + 35 * i * yDirection )/35), (int)((entity.x + 14 + 35 * -v) /35)) == true) {
					
				if(control.getTileAt((int)((entity.y + 14 -35 + 35 * i * yDirection )/35), (int)((entity.x + 14 + 35 * -v) /35)).getCollision(1) == false) {
	
						return v;
				}else {
					disableLeft = false;
				}//if else
			}//if else
		}//for
		return 0;
	}//checkXTile
	
	public String toString() {
		return "enemy";
	}//toString
	
	// healthLost
		public void healthLost() {
			this.health--;
		} // healtLost
		
		// getHealth
		public int getHealth() {
			return health;
		} // healtLost
  
} // AlienEntity class