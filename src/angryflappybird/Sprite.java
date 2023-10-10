package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

//The sprite class
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";

    /**
     * constructor for sprite
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /**
     * another constructor for sprite
     * @param pX
     * @param pY
     * @param image
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /**
     * set the image with size
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * set position of sprite objects
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * get x axis
     * @return
     */
    public double getPositionX() {
        return positionX;
    }

    
    /**
     * get y axis
     * @return
     */
    public double getPositionY() {
        return positionY;
    }

    /**
     * set velocity
     * @param velocityX
     * @param velocityY
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * add velocity
     * @param x
     * @param y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    /**
     * get the x velocity
     * @return
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * get the y velocity
     * @return
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * get the width
     * @return
     */
    public double getWidth() {
        return width;
    }

    /**
     * render sprite on canvas
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }
    
    /**
     * get boundary of sprite object
     * @return
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * check intersection
     * @param s
     * @return
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    /**
     * update time
     * @param time
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
}
