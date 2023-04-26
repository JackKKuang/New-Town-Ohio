import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class Tile {

	private BufferedImage Tile;
	public int collision;
	private Sprite Sprite;

	// constructor
	public Tile() {
	} // Tile

	// draws the Tile
	public void draw(Graphics g, int x, int y) {
		Sprite.draw(g, (int) x, (int) y);
	} // draw

	// sets the Tile image
	public void setTileImage(String newSprite) {
		this.Sprite = (SpriteStore.get()).getSprite(newSprite);
	} // setTileImage

	// returns the Sprite of the Tile;
	public Sprite getTileImage() {
		return this.Sprite;
	} // getTileImage

	// sets the collision status of the Tile
	public void setCollision(int collide) {
		this.collision = collide;
	} // setCollision

	// getCollision
	public int getCollision() {
		return collision;
	} // getCollision

	// getCollision overloaded
	public boolean getCollision(int x) {

		// returns collision as a boolean
		if (collision == 0) {
			return false;
		} // if
		return true;
	} // getCollision
} // Tile
