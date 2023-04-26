public class CheckForCollision {
	private TileController control = new TileController();

	public CheckForCollision() {

	} // CheckForCollision
	
	// checkTileX
	public boolean checkTileX(Entity Entity, double delta) {
		int entityX = 0;
		int entityY = 0;
		
		// check if the nearby tiles the entity is heading into is solid on the X axis
		for (int i = 1; i <= 2; i++) {
			if(Entity instanceof  PlayerEntity || Entity instanceof EnemyEntity) {
				entityX = (int) (Entity.x + (i) * 35 + (Entity.dx * delta) / 1000);
				entityY = (int) (Entity.y + (i) * 35);
			} else {
				entityX = (int) (Entity.x + (Entity.dx * delta) / 1000);
				entityY = (int) (Entity.y);
			} // else
			try {
				Tile Tile = control.getTileAt((int) (entityY / 35), (int) (entityX / 35));
				if (Tile.getCollision() == 1) {
					return false;
				} // if
			} catch (Exception e) {
			} // catch
		} // for
		return true;
	} // checkTileX

	// checkWin
	public boolean checkWin(Entity Entity, double delta) {
		
		// checks if the tile the entity is heading towards is the tile,
		// which progresses the player to the next level
		
		try {
			for (int i = 1; i <= 2; i++) {
				int entityX = (int) (Entity.x + (i) * 35 + (Entity.dx * delta) / 1000);
				int entityY = (int) (Entity.y + (i) * 35);
				Tile Tile = control.getTileAt((int) (entityY / 35), (int) (entityX / 35));
			
			try {
				if (Tile.getCollision() == 3) {
					return true;
				} // if
			} catch (Exception e) {
			} // catch
			return false;
			} // for
			return false;
		}catch(Exception e) {
			return false;
		} // catch
		
	} // checkWin

	//checkTileY
	public boolean checkTileY(Entity Entity, double delta) {
		int entityX = 0;
		int entityY = 0;
		
		// check if the nearby tiles the entity is heading into is solid on the Y axis
		for (int i = 1; i <= 2; i++) {
			if(Entity instanceof PlayerEntity|| Entity instanceof EnemyEntity) {
				entityX = (int) (Entity.x + (i) * 35);
				entityY = (int) (Entity.y + (i) * 35 + (Entity.dy * delta) / 1000);
			} else{
				entityX = (int) (Entity.x);
				entityY = (int) (Entity.y + (Entity.dy * delta) / 1000);
			} // else
			try {
				Tile Tile = control.getTileAt((int) (entityY / 35), (int) (entityX / 35));
				if (Tile.getCollision() == 1) {
					return false;
				} // try
			} catch (Exception e) {
			} // catch
		} // for
		return true;
	} // checkTileY

} // CheckForCollision