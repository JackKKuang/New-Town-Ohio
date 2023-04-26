/* sprite.java
 * March 23, 2006
 * Store no state information, this allows the image to be stored only
 * once, but to be used in many different places.  For example, one
 * copy of alien.gif can be used over and over.
 */
 
 import java.awt.Graphics;
 import java.awt.Graphics2D;
 import java.awt.Image;
 import java.io.File;
 import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
 import java.nio.Buffer;

import javax.imageio.ImageIO;
 
 public class Sprite {

   public Image image;  // the image to be drawn for this sprite
   private BufferedImage bufferedImageSaved; // this is the rotated sprite
   //public BufferedImage bufferedImage;
   
   
   // constructor
   public Sprite (Image i) {
     image = i;
     bufferedImageSaved = (BufferedImage)i;
   } // constructor
   
   // return width of image in pixels
   public int getWidth() {
      return image.getWidth(null);
   } // getWidth

   // return height of image in pixels
   public int getHeight() {
      return image.getHeight(null);
   } // getHeight

   // draw the sprite in the graphics object provided at location (x,y)
   public void draw(Graphics g, int x, int y) {
      g.drawImage(bufferedImageSaved, x, y, null);
   } // draw
   
   // draw the sprite with a rotation
   public void draw(Graphics g, int x, int y, int rotation) {
	   
	   // convert the rotation given to radians
	   double rotationRequired = Math.toRadians (rotation);
	   
	   // create a new bufferedImage
	   BufferedImage bufferedImage = (BufferedImage)image; 
		 
	   // rotate the image
	   AffineTransform transform = new AffineTransform();
	   transform.rotate(rotationRequired, bufferedImage.getWidth()/2, bufferedImage.getHeight()/2);
	   AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
	   bufferedImage = op.filter(bufferedImage, null); 
	   
	   // draw the image
	   g.drawImage(bufferedImage, x, y, null);
   } // draw
   
 } // sprite
 