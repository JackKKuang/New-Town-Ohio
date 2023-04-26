
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileController {
	Tile[][] Tile;
	private int row = 0;
	private int col = 0;
	private int tileSize = 35;

	// constructor fot Tile Controller
	public TileController() {
		Tile = new Tile[1001][1001];

		setTileImage(Game.fileName);
	} // TileController

	public Tile getTileAt(int i, int j) {
		
		
		return Tile[i][j];
	}// getTileAt

	// sets the proper image for the Tile
	public void setTileImage(String fileName) {
		
		// sets an image for each Tile depending on the number that represents them
		String[] tileType1 = getFileContents(fileName);
		row = tileType1.length;
		for (int i = 0; i < row; i++) {
			String[] tileType = tileType1[i].split(" ");
			col = tileType.length;		
			
			for (int j = 0; j < col; j++) {
				Tile[i][j] = new Tile();
				if (Integer.parseInt(tileType[j]) == 0) {
					Tile[i][j].setTileImage("sprites/sandpebbles.png");
					Tile[i][j].setCollision(0);
				} else if (Integer.parseInt(tileType[j]) == 1) {
					Tile[i][j].setTileImage("sprites/sandmiddle.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 2) {
					Tile[i][j].setTileImage("sprites/sandbarrier.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 3) {
					Tile[i][j].setTileImage("sprites/leftcorner.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 4) {
					Tile[i][j].setTileImage("sprites/rightcorner.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 5) {
					Tile[i][j].setTileImage("sprites/sandleftbar.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 6) {
					Tile[i][j].setTileImage("sprites/sandrightbar.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 7) {
					Tile[i][j].setTileImage("sprites/sandup.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 8) {
					Tile[i][j].setTileImage("sprites/sandleftcorner.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 9) {
					Tile[i][j].setTileImage("sprites/sanduprightcorner.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 10) {
					Tile[i][j].setTileImage("sprites/cactus.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 11) {
					Tile[i][j].setTileImage("sprites/background.png");
					Tile[i][j].setCollision(0);
				} else if (Integer.parseInt(tileType[j]) == 12) {
					Tile[i][j].setTileImage("sprites/endzone.png");
					Tile[i][j].setCollision(3);
				} else if (Integer.parseInt(tileType[j]) == 13) {
					Tile[i][j].setTileImage("sprites/endzoneback.png");
					Tile[i][j].setCollision(3);
				} else if (Integer.parseInt(tileType[j]) == 14) {
					Tile[i][j].setTileImage("sprites/sandtile.png");
					Tile[i][j].setCollision(0);
				} else if (Integer.parseInt(tileType[j]) == 15) {
					Tile[i][j].setTileImage("sprites/flowercactus.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 16) {
					Tile[i][j].setTileImage("sprites/bush.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 17) {
					Tile[i][j].setTileImage("sprites/cowhead.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 18) {
					Tile[i][j].setTileImage("sprites/woodenfloor.png");
					Tile[i][j].setCollision(1);
				} else if (Integer.parseInt(tileType[j]) == 19) {
					Tile[i][j].setTileImage("sprites/cowskeleton.png");
					Tile[i][j].setCollision(1);
				} // else if
			} // for
		} // for
	} // getTileImages

	// draws all the tilesa
	public void drawTile(Graphics2D g2) {
		int y = 0;
		int x = 0;

		
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				
				try {
					Tile[i][j].draw(g2, x, y);
					x += tileSize;
				}catch(Exception e) {
					
				}
			} // for
			x = 0;
			y += tileSize;
		} // for
	} // drawTile

	// gets Tile size
	public int getTileSize() {
		return this.tileSize;
	} // getTileSize

	// gets the contents from a file as a String array
	public static String[] getFileContents(String fileName) {

		String[] contents = null;
		int length = 0;
		try {

			// input
			String folderName = "/subFolder/";
			String resource = fileName;

			// this is the path within the jar file
			InputStream input = TileController.class.getResourceAsStream(folderName + resource);
			if (input == null) {
				// this is how we load file within editor (eg eclipse)
				input = TileController.class.getClassLoader().getResourceAsStream(resource);
			} // if
			BufferedReader in = new BufferedReader(new InputStreamReader(input));

			in.mark(Short.MAX_VALUE); // see api

			// count number of lines in file
			while (in.readLine() != null) {
				length++;
			} // while

			in.reset(); // rewind the reader to the start of file
			contents = new String[length]; // give size to contents array

			// read in contents of file and print to screen
			for (int i = 0; i < length; i++) {
				contents[i] = in.readLine();
			} // for
			in.close();
		} catch (Exception e) {
			System.out.println("File Input Error");
		} // catch

		return contents;

	} // getFileContents

	//tileExists
	public boolean tileExists(int y, int x) {
		try {
			if (this.getTileAt(y, x).getCollision() == 0) {
				return true;
			} else {
				return true;
			} // else
		} catch (Exception e) {
			return false;
		} // catch

	} // tileExists

} // getTileImage
