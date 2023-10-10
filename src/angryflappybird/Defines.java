package angryflappybird;

import java.util.HashMap;
import java.util.Random;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * defines class
 *
 */
public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 45;
    final int BLOB_HEIGHT = 35;
    final int BLOB_POS_X = 90;
    final int BLOB_POS_Y = 220;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 120;    		// the blob drop velocity
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the snooze bird
    final int BIRD_WIDTH = 50;
    final int BIRD_HEIGHT = 70;
    final int BIRD_POS_X = 90;
    final int BIRD_POS_Y = 130;
    final int BIRD_DROP_TIME = 300000000;
    final double BIRD_DROP_VEL = 0.2;
    final int BIRD_IMG_LEN = 4;
    final int BIRD_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
    // coefficients related to the pipes
    Random rand = new Random();
    final int PIPE_X = 300;
    int PIPE1_Y = -rand.nextInt(50);
    int PIPE2_Y = rand.nextInt(100)+350;
    final int PIPE_WIDTH = 50;
    final int PIPE1_HEIGHT = 200;
    final int PIPE2_HEIGHT = 200;
    final double PIPE_MOVE_VEL = -0.5;
    final int PIPE_COUNT = 6;
    
    // coefficients related to the white eggs
    final int WHITEEGG_X = PIPE_X -10;
    final int GOLDEGG_X = PIPE_X-10;
    int EGG_Y = PIPE2_Y-70;
    final int EGG_WIDTH = 60;
    final int EGG_HEIGHT = 70;

    // coefficients related to the pigs
    final int PIG_X = PIPE_X;
    int PIG_Y = PIPE1_Y + 20;
    final int PIG_WIDTH = 60;
    final int PIG_HEIGHT = 60;
    final double PIG_DROP_VEL = 0.4;

    // coefficients related to the game over
    final int GAME_OVER_X = 70;
    final int GAME_OVER_Y = 100;
    final int GAME_OVER_WIDTH = 250;
    final int GAME_OVER_HEIGHT = 60;
    
    // coefficients related to time
    int SCENE_SHIFT_TIME = 3;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;    
    
    int score = 0;
    int pipePass = 0;
    int life = 3;
 
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";

    final String[] IMAGE_FILES = {"bg_day", "blob0", "blob1", "blob2", "blob3",
    		"floor", "pipe1", "pipe2","white egg","gold egg", "bg_night","pig","game_over","snooze bird"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    //nodes on the scene graph
    Button startButton;
    Button easyButton;
    Button mediumButton;
    Button hardButton;

    Label labelWhiteEgg;
    Label labelGoldenEgg;
    Label labelPig;
    Image whiteEggImg;
    Image goldenEggImg;
    Image pigImg;
    ImageView iv1;
    ImageView iv2;
    ImageView iv3;
    
    // constructor
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 5) {
				img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
			}
			else if (i == 1 || i == 2 || i == 3 || i == 4){
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
			}
			else if(i == 6) {
				img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE1_HEIGHT,false, false);
			}
			else if(i == 7) {
				img = new Image(pathImage(IMAGE_FILES[i]), PIPE_WIDTH, PIPE2_HEIGHT,false, false);
			}
			else if(i == 8 || i==9) {
				img = new Image(pathImage(IMAGE_FILES[i]), EGG_WIDTH, EGG_HEIGHT, false, false);
			}
			else if(i == 11) {
				img = new Image(pathImage(IMAGE_FILES[i]), PIG_WIDTH, PIG_HEIGHT, false, false);
			}
			else if(i == 12) {
				img = new Image(pathImage(IMAGE_FILES[i]), GAME_OVER_WIDTH, GAME_OVER_HEIGHT, false, false);
			}
			else if(i == 13) {
				img = new Image(pathImage(IMAGE_FILES[i]), BIRD_WIDTH, BIRD_HEIGHT, false, false);
			}
			else {
				img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
			}
			IMAGE.put(IMAGE_FILES[i],img);
    	}
		// initialize image views
		for(int i=0; i<IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		// initialize scene nodes
		startButton = new Button("Start Game");
		easyButton = new Button("Easy");
		mediumButton = new Button("Medium");
		hardButton = new Button("Hard");
        
		// initialize white eggs
        whiteEggImg = new Image(pathImage(IMAGE_FILES[8]), EGG_WIDTH, EGG_HEIGHT, false, false);
        iv1 = new ImageView(whiteEggImg);
        labelWhiteEgg = new Label("Bonus points");
        labelWhiteEgg.setGraphic(iv1);
        
        // initialize gold eggs
        goldenEggImg = new Image(pathImage(IMAGE_FILES[9]), EGG_WIDTH, EGG_HEIGHT, false, false);
        iv2 = new ImageView(goldenEggImg);
        labelGoldenEgg = new Label("Lets you snooze");
        labelGoldenEgg.setGraphic(iv2);
        
        // initialize pig
        pigImg = new Image(pathImage(IMAGE_FILES[11]), PIG_WIDTH, PIG_HEIGHT, false, false);
        iv3 = new ImageView(pigImg);
        labelPig = new Label("Avoid pigs");
        labelPig.setGraphic(iv3);
	}
	
	/**
	 * adding scores
	 * @param value
	 */
	public void addScore(int value) {
		score += value;
	}
	
	/**
	 * losting scores
	 * @param value
	 */
	public void loseScore(int value) {
		score -= value;
	}
	
	/**
	 * setting scores
	 * @param value
	 */
	public void setScore(int value) {
		score = value;
	}
	
	/**
	 * counting pipe pass
	 */
	public void addPipePass(){
		pipePass += 1;
	}
	
	/**
	 * set the pass pipe
	 * @param value
	 */
	public void setPipePass(int value) {
		pipePass = value;
	}
	
	/**
	 * get path for images
	 * @param filepath
	 * @return
	 */
	public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }
	
	/**
	 * resize the images
	 * @param filepath
	 * @param width
	 * @param height
	 * @return
	 */
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }
}
