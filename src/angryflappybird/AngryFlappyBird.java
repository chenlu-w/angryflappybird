package angryflappybird;


import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;


import java.io.File;
import java.util.ArrayList;
import java.util.Random;

/**
 * The Application layer
 *
 */
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
	private Random rand =new Random();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime, startDropping, startSnooze;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> pipes1;
    private ArrayList<Sprite> pipes2;
    
    //Sprite objects for game features
    private Sprite whiteEgg;
    private Sprite goldEgg;
    private Sprite pig;
    private Sprite snoozeBird;

    private ArrayList<Boolean> checkPass;
    //boolean variable for checking if the eggs are being hit
    private Boolean checkWhiteEgg;
    private Boolean checkGoldEgg;

    private int life = 3;		//remaining life
    private Sprite night;
    private MediaPlayer mediaPlayer, passPipe, die;		//sound effect objects
    AudioClip collectEgg;
   
    
    // game flags
    private boolean HIT_PIPE, CLICKED, GAME_START, GAME_OVER, LOSE_LIFE, DROPPED, IS_SNOOZE;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private VBox levels;
    private GraphicsContext gc;		
    
	// the mandatory main method 
    public static void main(String[] args) {
        launch(args);
    }
       
    /**
     *  the start method sets the Stage layer
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	
        HBox root = new HBox();
		HBox.setMargin(gameScene, new Insets(0,15,0,15));
		root.getChildren().add(gameScene);
		root.getChildren().add(gameControl);
		
		// add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        
        //Sound effect object for background music
    	Media sound = new Media(new File("src/resources/sounds/Angel boring.mp3").toURI().toString());
    	mediaPlayer = new MediaPlayer(sound);
    	mediaPlayer.setVolume(0.2);
    	
    	//Sound effect object for passing pipes
        Media sound2 = new Media(new File("src/resources/sounds/pass pipe.m4a").toURI().toString());
     	passPipe = new MediaPlayer(sound2);
     	passPipe.setVolume(0.5);
    	
     	//Sound effect object when the bird dies
    	Media sound3 = new Media(new File("src/resources/sounds/die.mp3").toURI().toString());
    	die = new MediaPlayer(sound3);
    	die.setVolume(0.5);
    	
    	//Sound effect object when bird hits an egg
    	collectEgg = new AudioClip(new File("src/resources/sounds/pick egg.wav").toURI().toString());
    	collectEgg.setVolume(0.4);
    	
        primaryStage.show();

        
    }
    
    
    /**
     *  the getContent method sets the Scene layer
     */
    private void resetGameControl() {
        
    	levels = new VBox(10);
    	levels.getChildren().add(DEF.easyButton);
    	levels.getChildren().add(DEF.mediumButton);
    	levels.getChildren().add(DEF.hardButton);
        DEF.easyButton.setOnMouseClicked(this::easyLevelClickHandler);
        DEF.mediumButton.setOnMouseClicked(this::mediumLevelClickHandler);
        DEF.hardButton.setOnMouseClicked(this::hardLevelClickHandler);
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        
        gameControl = new VBox(35);
        gameControl.getChildren().add(DEF.startButton);
        gameControl.getChildren().add(levels);
        gameControl.getChildren().add(DEF.labelWhiteEgg);
        gameControl.getChildren().add(DEF.labelGoldenEgg);
        gameControl.getChildren().add(DEF.labelPig);

    }
    
    /**
     *  control the easy level
     * @param e
     */
    private void easyLevelClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	GAME_START = false;
        CLICKED = false;
    }
    
    /**
     *  control the medium level
     * 	@param e
     */
    private void mediumLevelClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	DEF.PIPE1_Y= -rand.nextInt(75);
    	DEF.PIPE2_Y = rand.nextInt(120)+500;
    	DEF.SCENE_SHIFT_TIME = 6;
    	GAME_START = false;
        CLICKED = false;
    }
    
    /**
     *  control the hard level
     * @param e
     */
    private void hardLevelClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	DEF.PIPE1_Y= -rand.nextInt(90);
    	DEF.PIPE2_Y = rand.nextInt(200)+600;
    	DEF.SCENE_SHIFT_TIME = 9;
    	GAME_START = false;
        CLICKED = false;
    }
    
    /**
     * control the bird
     * @param e
     */
    private void mouseClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
    }
    

    /**
     * reset the game scene when first entry
     * @param firstEntry
     */
    private void resetGameScene(boolean firstEntry) {
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        HIT_PIPE = false;
        floors = new ArrayList<>();
        pipes1 = new ArrayList<>();
        pipes2 = new ArrayList<>();
        checkPass = new ArrayList<>();
        checkWhiteEgg = false;
        checkGoldEgg = false;
        
    	if(firstEntry) {
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            ImageView background = DEF.IMVIEW.get("bg_day");;
            gameScene = new Group();
    		    gameScene.getChildren().addAll(background, canvas);

         }
            
    	// initialize floor
    	for(int i = 0; i < DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		
    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    	}
    	

    	// initialize pipe
    	 for(int i = 0; i < DEF.PIPE_COUNT; i++) {
    	 	int posX = (i+1) * DEF.PIPE_X;
    	 	DEF.PIPE2_Y = rand.nextInt(100)+350;
            DEF.PIPE1_Y = -rand.nextInt(50);
    	 	int posY1 = DEF.PIPE1_Y;
    	 	int posY2 = DEF.PIPE2_Y;
    	 	Sprite pipe1 = new Sprite(posX, posY1, DEF.IMAGE.get("pipe1"));
    	 	Sprite pipe2 = new Sprite(posX, posY2, DEF.IMAGE.get("pipe2"));
    	 	pipe1.setVelocity(DEF.PIPE_MOVE_VEL, 0);
    	 	pipe2.setVelocity(DEF.PIPE_MOVE_VEL, 0);
            pipe1.render(gc);
            pipe2.render(gc);
            pipes1.add(pipe1);
            pipes2.add(pipe2);
            checkPass.add(false);
    	 }
    	 

        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("blob0"));
        blob.render(gc);
        
        //initialize white eggs
        DEF.EGG_Y = DEF.PIPE2_Y-70;
        whiteEgg = new Sprite(DEF.WHITEEGG_X, DEF.EGG_Y, DEF.IMAGE.get("white egg"));
        whiteEgg.render(gc);
        
        //initialize gold eggs
        goldEgg = new Sprite(DEF.GOLDEGG_X, DEF.EGG_Y, DEF.IMAGE.get("gold egg"));
        goldEgg.render(gc);
        
        //initialize night background
        night = new Sprite(0,0,DEF.IMAGE.get("bg_night"));

        // initialize pig
        DEF.PIG_Y = DEF.PIPE1_Y + 20;
        pig = new Sprite(DEF.PIG_X, DEF.PIG_Y, DEF.IMAGE.get("pig"));
        pig.render(gc);
        
        //initialize snooze bird
        snoozeBird = new Sprite(DEF.BIRD_POS_X, DEF.BIRD_POS_Y, DEF.IMAGE.get("snooze bird"));


        // initialize timer
        startTime = System.nanoTime();
        
        timer = new MyTimer();
        timer.start();
    }

    /**
     * timer stuff
     *
     */
    class MyTimer extends AnimationTimer {
    	
    	int counter = 0;
    	
    	 @Override
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

    	     if (GAME_START) {

    	    	 //change background between day and night
    	    	 changeBackground();
    	    	 
    	    	 //play background music
    	    	 mediaPlayer.play();
    	    	 
    	    	 //update pipes
    	    	 movePipe();
    	    	 SnoozeTime(7); 
	    		 
    	    	
    	    	 if(IS_SNOOZE == false) {
    	    		 //update pig
        	    	 movePig();
        	    	 //update floor
        	    	 moveFloor();
        	    	 // update blob
        	    	 moveBlob();
        	    	 //update eggs
        	    	 moveWhiteEgg();
        	    	 moveGoldEgg();  
        	    	 checkCollision();
        	    	 //show score and lives
        	    	 gc.strokeText("SCORE: "+String.valueOf(DEF.score), 20, 20);
        	    	 gc.strokeText("REMAINING LIFE: "+String.valueOf(life), 20, 40);
    	    	 }  	 

    	     }
    	 }
    	 
    	 /**
    	  * change day background to night background
    	  */
    	 private void changeBackground() {
    		 if((DEF.pipePass/10)%2 != 0) {
     			night.render(gc);
    		 }
    	 }
    	 
    	 /**switch to parachuting mood
    	  * 
    	  * @param Time
    	  */
    	 public void SnoozeTime(int Time) {
    		 if(IS_SNOOZE == false) {
        		 if(blob.intersectsSprite(goldEgg) && checkGoldEgg == false) {
        			checkGoldEgg = true;
         			startSnooze = System.nanoTime();	
         			blob.setPositionXY(600, -100);
         			blob.setVelocity(0, DEF.BLOB_DROP_VEL);
        			System.out.println("Start snooze is updated");
      			}
    		 }
    		 long snoozeTime = System.nanoTime() - startSnooze;
    		 if(snoozeTime * DEF.NANOSEC_TO_SEC <= Time) {
    			//System.out.println(snoozeTime * DEF.NANOSEC_TO_SEC);
    			IS_SNOOZE = true;
    			gc.strokeText("SCORE: "+String.valueOf(DEF.score), 20, 20);
    			gc.strokeText("REMAINING LIFE: "+String.valueOf(life), 20, 40);
    			int[]secs = {1, 2, 3, 4, 5, 6, 7};
    			for(int sec: secs) {
        			if((int)(snoozeTime * DEF.NANOSEC_TO_SEC) == sec) {
        				gc.strokeText(String.valueOf(7 - sec) + " secs to go", 180, 80);
        			}
        			if((int)(snoozeTime * DEF.NANOSEC_TO_SEC) == 6) {
        				blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
        				pig.setPositionXY(-50, -100);
        				whiteEgg.setPositionXY(600, -100);
        				goldEgg.setPositionXY(600, -100);
        			}
    			}
    			moveBird(snoozeTime);
    		 } 
    		 else {
    			 IS_SNOOZE = false;
    			 checkGoldEgg = false;
    		 }
    	 }   	    	 

    	 /**
    	  * step1: update floor
    	  */
    	 private void moveFloor() {
    		
    		for(int i = 0; i < DEF.FLOOR_COUNT; i++) {
    			if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
    				double newX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
    	        	double newY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    	        	floors.get(i).setPositionXY(newX, newY);
    			}
    			floors.get(i).render(gc);
    			floors.get(i).update(DEF.SCENE_SHIFT_TIME);
    		}
    	 }
    	 

    	 /**
    	  * step2: update blob
    	  */
    	 private void moveBlob() {
    		 
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
				
				int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
				blob.setImage(DEF.IMAGE.get("blob"+String.valueOf(imageIndex)));
				if(HIT_PIPE == false) {
					blob.setVelocity(0, DEF.BLOB_FLY_VEL);
				}
				
			}
			// blob drops after a period of time without button click
			else {
				if(HIT_PIPE == false) {
					blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
					
				}
			    CLICKED = false;
			}

			// render blob on GUI
			blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			blob.render(gc);
    	 }
    	 
    	 /**
    	  * step3: update pipes
    	  */
    	 private void movePipe() {
    		 //loop through 6 pipes
    		 for(int i = 0; i < DEF.PIPE_COUNT; i++) {
    			 if(pipes1.get(i).getPositionX() <= -DEF.PIPE_WIDTH) {
    				 double newX = pipes1.get((i+5)%DEF.PIPE_COUNT).getPositionX()+DEF.PIPE_X;
    				 DEF.PIPE2_Y = rand.nextInt(100)+350;
    		         DEF.PIPE1_Y = -rand.nextInt(50);
    		         //update new x and y axis values
    				 double newY1 = DEF.PIPE1_Y;
    				 double newY2 = DEF.PIPE2_Y;
    				 pipes1.get(i).setPositionXY(newX, newY1);
    				 pipes2.get(i).setPositionXY(newX, newY2);
    				 checkPass.set(i, false); 
    				 
    			 }
    			 //move upper pipes
    			 pipes1.get(i).update(DEF.SCENE_SHIFT_TIME);
    			 pipes1.get(i).render(gc);
    			 
    			 //move lower pipes
    			 pipes2.get(i).update(DEF.SCENE_SHIFT_TIME);
    			 pipes2.get(i).render(gc);
    		 }
    	 }
    	 
    	 /**
    	  * step4: update white eggs
    	  */
    	 private void moveWhiteEgg() {
    		 int[] numbers = {1,3,5};
    		 whiteEgg.setVelocity(DEF.PIPE_MOVE_VEL, 0);
    		 if(whiteEgg.getPositionX() <= -DEF.EGG_WIDTH) {
    			 //randomly generate white eggs
    			 int i = rand.nextInt(0,2);
    			 int index = numbers[i];
    			 //update position of white egg with pipes
    			 double newX = pipes1.get(index+1).getPositionX();
				 double newY = pipes2.get(index+1).getPositionY()-70;
				 whiteEgg.setPositionXY(newX, newY);   
				 checkWhiteEgg = false;
    		 }
    		 //render white egg
    		 whiteEgg.update(DEF.SCENE_SHIFT_TIME);
    		 whiteEgg.render(gc);
    	 }
    	 
    	 /**
    	  * step5: update gold egg
    	  */
    	 private void moveGoldEgg() {
    		 int[] numbers = {2,4,6};
    		 goldEgg.setVelocity(DEF.PIPE_MOVE_VEL, 0);
    		 if(goldEgg.getPositionX() <= -DEF.EGG_WIDTH) {
    			 //randomly generate gold eggs
    			 int i = rand.nextInt(0,2);
    			 int index = numbers[i];
    			 //update positions of gold egg
    			 double newX = pipes1.get(index+1).getPositionX();
				 double newY = pipes2.get(index+1).getPositionY()-70;
				 goldEgg.setPositionXY(newX, newY);
				 checkGoldEgg = false;
    			 
    		 }
    		 //render gold egg
    		 goldEgg.update(DEF.SCENE_SHIFT_TIME);
    		 goldEgg.render(gc);
         }


    	 /**
    	  *  step6: update pig
    	  */
    	 private void movePig() {
     		pig.setVelocity(DEF.PIPE_MOVE_VEL, DEF.PIG_DROP_VEL);
 	    		if (pig.getPositionX() <= -DEF.PIG_WIDTH) {
 	    			//randomly generate pigs
 	    			int i = rand.nextInt(3);
 	    			//update pig positions
 	        	double newX = pipes1.get((DEF.pipePass + i)%DEF.PIPE_COUNT).getPositionX()+DEF.PIPE_X;
 	        	double newY = DEF.PIPE1_Y + 20;
 	        	pig.setPositionXY(newX, newY);
 	    		}  		

 	    	//renger pigs
         	pig.update(DEF.SCENE_SHIFT_TIME);
         	pig.render(gc);
     	 }
    	 
    	 /**
    	  * step7: update bird
    	  * @param time
    	  */
    	 private void moveBird(long time) {
    		 snoozeBird.setPositionXY(DEF.BIRD_POS_X, DEF.BIRD_POS_Y);
    		 snoozeBird.setVelocity(0, DEF.BIRD_DROP_VEL); 
    		 snoozeBird.update(time * DEF.NANOSEC_TO_SEC);
 			 snoozeBird.render(gc);
    	 }
    	 /**
    	  * function for checking collision
    	  */
    	 public void checkCollision() {

    		int counter = 0;
    		// check collision  
			for (Sprite floor: floors) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor) || life == 0;
			} 
			for (Sprite pipe1: pipes1) {
				if(blob.intersectsSprite(pipe1) && LOSE_LIFE == false) {
					//if hit pipes, lose one life and bounce back and drop
					life -= 1;
					LOSE_LIFE = true;
					HIT_PIPE = true;
					DROPPED = true;
					showDropEffect();
					
				}
				//enable user to click after 0.5 seconds
				if((System.nanoTime() - startDropping)*DEF.NANOSEC_TO_SEC > 0.5 && DROPPED == true) {
					 DEF.startButton.setDisable(false);
					 HIT_PIPE = false;
					 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
					 DROPPED = false;
				}
				
			}
			
			//if bird hits white egg, play sound effect and add score, and make white egg disappear
			if(blob.intersectsSprite(whiteEgg) && checkWhiteEgg == false) {
				collectEgg.play();
				System.out.println("white egg");
				DEF.addScore(5);
				checkWhiteEgg = true;
				whiteEgg.setPositionXY(700, -100);
				whiteEgg.render(gc);
			}	
			
			for (Sprite pipe2 : pipes2) {
				if(!blob.intersectsSprite(pipe2) && 
						blob.getPositionX() >= (pipe2.getPositionX()+pipe2.getWidth()) 
						&& checkPass.get(counter) == false) {
					DEF.addScore(2);
					//if pass a pipe, play sound effect and add 2 scores
					DEF.addPipePass();
					passPipe.seek(Duration.ZERO);
					passPipe.play();

					checkPass.set(counter, true);
					//System.out.println(DEF.pipePass);
					LOSE_LIFE = false;
					
				}
				//if hit lower pipes, bounce back and drop, lose one life
				if(blob.intersectsSprite(pipe2) && LOSE_LIFE == false) {
					life -= 1;
					LOSE_LIFE = true;
					HIT_PIPE = true;
					DROPPED = true;
					showDropEffect();
				}
				//enable user to click after 0.5 second
				if((System.nanoTime() - startDropping)*DEF.NANOSEC_TO_SEC > 0.5 && DROPPED == true) {
					 DEF.startButton.setDisable(false);
					 HIT_PIPE = false;
					 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
					 DROPPED = false;
				}
				
				counter += 1;
			} 
			

			//if hits pig, game will over
			if(blob.intersectsSprite(pig)) {
				GAME_OVER = true;
			}
			
			//if pig hits white egg, egg will disappear and 2 score will be took off
			if(pig.intersectsSprite(whiteEgg) && checkWhiteEgg == false) { // not sure
				DEF.loseScore(2);
				checkWhiteEgg = true;
				whiteEgg.setPositionXY(700, -100);
				whiteEgg.render(gc);
			}
			
			//if pig hits gold egg, the egg will disappear
			if(pig.intersectsSprite(goldEgg) && checkGoldEgg == false) { // not sure
				
				checkGoldEgg = true;
				goldEgg.setPositionXY(700, -100);
				goldEgg.render(gc);
			}
			
			// end the game when blob hit stuff
			if (GAME_OVER) {
				die.seek(Duration.ZERO);
				die.play();
				DEF.setScore(0);
				DEF.setPipePass(0);
				life = 3;
				Sprite gameOver = new Sprite(70,150,DEF.IMAGE.get("game_over"));
				gameOver.render(gc);

				showHitEffect(); 

				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
				mediaPlayer.stop();
			}
			
    	 }
    	 
    	 /**
    	  * showing hit effect
    	  */
	     private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);
	        parallelTransition.getChildren().add(fadeTransition);
	        parallelTransition.play();
	     }
	     
	     /**
	      * show the effect of bird bounce back and drop
	      */
	     private void showDropEffect() {
	    	 startDropping = System.nanoTime();
	    	 DEF.startButton.setDisable(true);
	    	 blob.setVelocity(-100, 100);
	    	 blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			 blob.render(gc);
			 
			 
	     }
    	 
    } // End of MyTimer class

} // End of AngryFlappyBird Class