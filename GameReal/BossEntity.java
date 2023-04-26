public class BossEntity extends EnemyEntity {

	private Game Game; // the Game in which the alien exists
	private int movespeed = 0;

	// BossEntity
	public BossEntity(Game g, String r, int newX, int newY, int health) {
		super(g, r, newX, newY, health); // calls the constructor in Entity
		Game = g;
	} // constructor

} // BossEntity class