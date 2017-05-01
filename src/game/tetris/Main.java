/*
 * Title: Tetris Remake 
 * By: Shabaz Badshah 
 * 
 * Features: 
 * Instantly drops down piece to the bottom
 * Pause/Resume function
 * Speed decreases as the user gets more lines cleared
 * 
 * Bugs: 
 * No game breaking bugs in the final version
 * Null pointer when drawing
 * Key pressed and released bug (had to remove keylistener when game is paused)
 */

package game.tetris;

import game.pieces.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

@SuppressWarnings("serial")
public class Main extends JFrame {

	//TIMERS
	private Timer timerXMovement; //The timer for the movement of the shape in the x direction 
	private Timer timerYMovement; //The timer for the movement of the shape in the y direction, this timer also controls the speed of 
								  //the shape (falling down)

	//CLASS OBJECTS
	private TetrisPiece currentShape; //The currentShape object, this is the current shape the user is controlling
	private TetrisPiece nextShape; //The nextShape object, this is the next shape that will spawn
	private Grid playGrid; //Grid object

	private int score;
	private int userAmountMoveShapeXDirection; //Amount to move the shape in the x direction depending on the user's inpu
	private int userAmountMoveShapeYDirection; //Amount to move the shape in the y direction depending on the user's input
	private int gameState; //The gameState, allows the game to be paused, resumed, started, keeps track of the state
	private int linesCleared; //The amount of lines cleared by the user
	/*
	 * The timer delay for the movement of the shape in the y direction (changes the speed of the game as the player gets a higher
	 * score)
	 */
	private int moveTimerDelayYDirection = 1100; 

	//CONSTANTS (INTEGERS) (dont't change values) 
	private final int SCREEN_SIZE_X = 485; //The size of the window in the x direction
	private final int SCREEN_SIZE_Y = 655; //This size of the window in the y direction
	private final int NEXT_SHAPE_GRID_X_SIZE = 4; //The size of the next shape grid in the x direction
	private final int NEXT_SHAPE_GRID_Y_SIZE = 4; //The size of the next shape grid in the y direction
	private final String SCREEN_TITLE = "Tetris"; //The title of the game
	private final int DISTANCE_BETWEEN_SQUARES = 30; //The distance between the squares on the grid
	private final int MOVE_TIMER_DELAY_X_DIRECTION = 150; //The delay in the movement of the shape in the x direction (will always stay the same)
	private final int SPEED_MODIFIER = 100; //Speed modifier (how fast or slow the delay will go)

	//CONSTANTS (COLOURS) 
	private final Color BACKROUND_COLOUR = Color.BLACK; //The colour of the background
	private final Color NEXT_PIECE_GRID_COLOUR = Color.WHITE; //The colour of the next piece grid
	private final Color MAIN_GRID_COLOUR = Color.WHITE; //The colour of the main grid that the user is playing on

	//GRAPHICS
	private  Graphics graphics; //The graphics object used to draw to the screen 
	private Image tempImage; //tempImage that is used for double buffering (resources used in draw method)
	private JPanel gamePanel; //The game panel where all the game is played

	//BOOLEANS
	boolean updateGameGraphics = false; //A boolean used to request an update from the draw method

	public static void main(String[] args) {
		Main tetris = new Main();
	}

	Main() {

		//Initializes main components
		setTitle(SCREEN_TITLE);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(SCREEN_SIZE_X, SCREEN_SIZE_Y);
		setLocationRelativeTo (null);
		setResizable(false);
		gamePanel = new JPanel();
		gamePanel.setFocusable(true);
		add(gamePanel);
		setVisible(true);
		tempImage = createImage(getWidth(), getHeight());
		graphics = tempImage.getGraphics(); //Takes the off screen graphics and sets them to graphics 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		timerXMovement = new Timer(moveTimerDelayYDirection, new ActionListener() { //Movement timer for the shape in the y direction

			public void actionPerformed(ActionEvent ae) {
				//If the currentShape has been placed, create a new shape
				if (currentShape.isShapeMoving(playGrid) == true) {
					playGrid.placePieceAtSpot(currentShape);
					currentShape = nextShape;
					nextShape = generateShape(true, 0); //Generates a new shape
					//If any of the shapes have reached the top, stops the timers and displays game over
					if (currentShape.isShapeMoving(playGrid) == true) { 
						timerXMovement.stop();
						timerYMovement.stop();
						JOptionPane.showMessageDialog(null, "Game Over!");
						System.exit(0); //Exits the game
					}
					int linesRemoved = playGrid.removeRow(); 
					if (linesRemoved > 0) {

						linesCleared += linesRemoved;
						score += linesRemoved * 10;
						//Reduces the delay of the timer as the user gets more lines
						if (moveTimerDelayYDirection <= 100) {
							moveTimerDelayYDirection = 100;
						} else {
							timerXMovement.setDelay(moveTimerDelayYDirection -= SPEED_MODIFIER * linesRemoved);
						}
					}
				}
				//Moves the currentShape down automatically 
				currentShape.moveShape(playGrid, 0, 1);
				System.out.println(timerXMovement.getDelay());
				//Updates the graphics
				updateGameGraphics();
			}
		});

		//Timer for shape movement
		timerYMovement = new Timer(MOVE_TIMER_DELAY_X_DIRECTION, new ActionListener() {

			//If the shape is moving, then updateGraphics
			public void actionPerformed(ActionEvent ae) {
				/*
				 * If the shape is moving left or right, request a graphics update to the screen
				 */
				if (userAmountMoveShapeXDirection != 0 && currentShape.moveShape(playGrid, userAmountMoveShapeXDirection, 0) == true) {
					updateGameGraphics = true;
				}
				if (userAmountMoveShapeYDirection != 0 && currentShape.moveShape(playGrid, 0, userAmountMoveShapeYDirection) == true) {
					updateGameGraphics = true;
				}
				if (updateGameGraphics == true) {
					updateGameGraphics();
				}
			}
		});

		//KeyListener for game play (shape movement and pause)
		/*
		 * https://docs.oracle.com/javase/tutorial/uiswing/events/keylistener.html
		 */
		gamePanel.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent key) {
				//If the pause key (p) has been pressed
				if (key.getKeyCode() == 80) { 
					//If the game is running, pauses it
					if (timerXMovement.isRunning()) {
						pauseGame();
					} else {
						unPauseGame();
					}
				} else if (timerXMovement.isRunning()) {
					//Moves shape to the left if the left arrow key is pressed
					if (key.getKeyCode() == 37) {
						userAmountMoveShapeXDirection = -1;
						//If the shape is rotated with the up arrow key, updates graphics
					} else if (key.getKeyCode() == 38) {
						if (currentShape.rotateShape(playGrid)) {
							updateGameGraphics();
						}
						//If the right arrow key is pressed, moves the shape 1 unit right
					} else if (key.getKeyCode() == 39) {
						userAmountMoveShapeXDirection = 1;
					} else if (key.getKeyCode() == 40) {
						//If the down arrow key is pressed, moves the shape 1 unit down
						userAmountMoveShapeYDirection = 1;
					} else if (key.getKeyCode() == 32) {
						//If the spacebar is pressed, drops the shape to the bottom 
						currentShape.dropShapeToBottom(playGrid);
						updateGameGraphics();
					}
				}
			}

			/*
			 * If the user does not press any buttons, dont move the shape anywhere,
			 * the shape moves automatically down 1 unit 
			 */
			public void keyReleased(KeyEvent key) {
				if (key.getKeyCode() == 40) {
					userAmountMoveShapeYDirection = 0;
				} else if (key.getKeyCode() == 37 || key.getKeyCode() == 39) {
					userAmountMoveShapeXDirection = 0;
				}
			}

			public void keyTyped(KeyEvent key) {
			}
		});

		//New game, all the counters and trackers are reset
		score = 0; 
		linesCleared = 0;
		userAmountMoveShapeXDirection = 0;
		userAmountMoveShapeYDirection = 0;
		gameState = 0;

		timerXMovement.setDelay(moveTimerDelayYDirection);
		playGrid = new Grid(); //Grid object is created
		/*
		 * The currentShape and the next shapes are generateds 
		 */
		currentShape = generateShape(true, 0);
		nextShape = generateShape(true, 0);
		gamePanel.addKeyListener(new KeyListener() { //Adds a keylistener to listen for controls/input from user

			public void keyPressed(KeyEvent key) {
				/*
				 * The key listener is removed so that the game can be paused, and no input is recived
				 */
				gamePanel.removeKeyListener(this); 
				unPauseGame();
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub

			}
		});
		updateGameGraphics();
	}

	//Resumes the game
	private void unPauseGame() {
		if (gameState != 1) {
			gameState = 1;
			timerXMovement.start();
			timerYMovement.start();
			updateGameGraphics();
		}
	}

	/*
	 * Creates a random shape
	 */
	private TetrisPiece generateShape (boolean randomShape, int givenShape) {
		//Array of possible shapes to generate
		TetrisPiece[] shapes = {
				new Square_Shape(), new Line_Shape(), 
				new RightL_Shape(), new LeftL_Shape(), 
				new T_Shape(), new RightS_Shape(), 
				new LeftS_Shape()};
		//Returns one of the random shapes from the list
		if (randomShape == true) {
			return shapes[(int) (Math.random() * 7)]; //Returns a random shape from the list
		} 
		return null;
	}

	//Paints all required components using double buffering

	/*
	 * We decided to implement double buffering because the screen was flickering when the graphics
	 * were being implemented. I took a look at the starter code for ICS3U0 computer science project
	 * to implement double buffering to remove the flickering. I also referred to the link below 
	 * multiple times. In addition to those resources, I also reffered to the example for applets in 
	 * the OUT drive that was created last year to assist me.
	 */

	/*
	 * https://docs.oracle.com/javase/tutorial/extra/fullscreen/doublebuf.html
	 * http://www.realapplets.com/tutorial/doublebuffering.html
	 * http://stackoverflow.com/questions/10508042/how-do-you-double-buffer-in-java-for-a-game
	 * http://old.koalateam.com/jml/java/tricks/double-buffering.html
	 */
	public void updateGameGraphics() {
		//**All graphics are drawn to a temporary image and then drawn to the panel 
		//clears game panel and draws all grid lines
		graphics.setColor(BACKROUND_COLOUR);
		graphics.fillRect(0, 0, gamePanel.getWidth(), gamePanel.getHeight());
		graphics.setColor(NEXT_PIECE_GRID_COLOUR);

		/*
		 * Draws the main grid
		 */
		for (int i = 0; i < playGrid.getGridXAmount(); i++) {
			for (int j = 0; j < playGrid.getGridYAmount(); j++) {
				graphics.setColor(MAIN_GRID_COLOUR);
				graphics.drawRect(i * DISTANCE_BETWEEN_SQUARES, j * DISTANCE_BETWEEN_SQUARES, 30, 30);
				//Draws the shapes if it has been placed down and the spot is available
				if (playGrid.returnSpotAvailability(i, j) == true) {
					graphics.drawImage(new ImageIcon(getClass().getResource("/res/" + 6 + ".png")).getImage(), 1 + i * DISTANCE_BETWEEN_SQUARES, 
							1 + j * DISTANCE_BETWEEN_SQUARES, null);
				}
			}
		}

		/*
		 * Draws the next shape grid
		 */
		for (int i = 0; i <  NEXT_SHAPE_GRID_X_SIZE; i++) {
			for (int j = 0; j <  NEXT_SHAPE_GRID_Y_SIZE; j++) {
				graphics.drawRect(330 + (i * DISTANCE_BETWEEN_SQUARES), 30 + (j * DISTANCE_BETWEEN_SQUARES), 30, 30);
			}
		}

		//Text
		graphics.drawString("Score: " + score, 315, 210); //User's score
		graphics.drawString("Lines Cleared: " + linesCleared, 315, 240); //The amount of lines cleared by the user
		graphics.drawString("Press 'p' to pause/resume", 315, 270); 
		//Draws the currentShape and the nextShape
		currentShape.drawShape(graphics, 1, 1); //Draws the currentShape overtop of the main grid
		nextShape.drawShape(graphics, 241, 91); //Draws the nextShape overtop of the nextShape grid

		//Intro screen with controls
		if (gameState == 0) {
			graphics.drawImage(new ImageIcon(getClass().getResource("/res/mainMenuSplashScreen.jpg")).getImage(), 0, 0, this);	
		} 
		//If the user pauses the game draws the pause image
		if (gameState == 2) {
			graphics.drawImage(new ImageIcon(getClass().getResource("/res/pause.png")).getImage(), 320, 300, this);	
		}

		//Draws the temporary graphics to the panel
		gamePanel.getGraphics().drawImage(tempImage, 0, 0, gamePanel);
	}

	//Pauses the game
	private void pauseGame() {
		if (gameState == 1) {
			gameState = 2;
			updateGameGraphics();
			timerXMovement.stop();
			timerYMovement.stop();
		}
	}

}
