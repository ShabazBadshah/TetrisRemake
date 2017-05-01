package game.tetris;

import game.pieces.TetrisPiece;

import java.awt.*;

import javax.swing.ImageIcon;

/* 
 * This grid class is responsible for the logic of the game, more specifically it checks for valid moves and places/locks the shapes 
 * It makes sure every shape is placed in the correct spot, and is responsible for the placement of the shapes in their correct
 * positions. The grid is a grid of booleans and everything in the game is based off a spot in the grid being false or true
 */

public class Grid {
	
	private final int GRID_Y_AMOUNT = 20; //The size of the array in the y direction
	private final int GRID_X_AMOUNT = 10; //The size of the array in the x direction
	
    private final boolean [][] mainGrid = new boolean [GRID_X_AMOUNT][GRID_Y_AMOUNT]; //Creates a grid that has each spot be a boolean 

    //Returns the size of the grid in terms of the y amount
    public int getGridYAmount () {
    	return GRID_Y_AMOUNT;
    }
    
   //Returns the size of the grid in terms of the x amount
    public int getGridXAmount () {
    	return GRID_X_AMOUNT;
    }
    
   /*
    * Checks to see if a shape can be put between other shapes, without overlapping or going off the 
    * grid/screen, calls the canPlaceAtSpot method to determine if a shape can be placed
    */
    public boolean checkSpotAvailability(TetrisPiece shape, int xPos, int yPos) {
        try {
        	/*
        	 * Goes through each of the points of the shape and and returns if the spot is available by calling the
        	 * returnSpotAvailability method
        	 */
            for (Point point : shape.getPiece()) {
            	//Checks the avialability of each spot using the x and y coordinates of each point
                if (returnSpotAvailability(point.x + xPos, point.y + yPos) == true) {
                    return false;
                } 
            }
            /*
             * There was a bug where I would get an index out of bounds exception, but the game ran normally and as intended, this 
             * is when I was using an arrayList to for the grid instead of an array.
             * When I switched to an array, the same error occured, so I just caught the exception
             */
        } catch (IndexOutOfBoundsException e) { 
            return false;
        }
        return true;
    }

    /* 
     * Places the shape by calling the lockShapeSpot method
     */
    public void placePieceAtSpot(TetrisPiece s) {
        for (Point point : s.getPiece()) {
            changeAvailabilityOfSpot(point.x, point.y, true);
        }
    }

    /*
     * Locks the spot that the shape has taken, does not allow any other
     * shapes to be places there
     */
    public void changeAvailabilityOfSpot(int xPos, int yPos, boolean fillSpot) {
        mainGrid[xPos][yPos] = fillSpot; //Sets the spot in the array to true or false and allows shapes to be placed or not placed
    }

   /*
    * Checks to see if a shape can be places at the given location
    */
    public boolean returnSpotAvailability(int xPos, int yPos) {
        /*
         * Checks to see if the given x and y coordinate are available (true or false)
         * and returns if it is free or not
         */
        return mainGrid[xPos][yPos]; 
    }
    
    /*
     * Removes the lines that have been filled, and shift the blocks above 1 unit down
     */
      public int removeRow () {
          int linesCleared = 0;
          for (int i = GRID_Y_AMOUNT - 1; i >= 0; i--) {
              int counter = 0;
              /*
               * Goes through the grid (x and y values) and checks to see if any of the spots are open
               */
              for (int j = 0; j < GRID_X_AMOUNT; j++) {
                  if (returnSpotAvailability(j, i) == true) { 
                      counter++;
                  }
              }
              
              //Once it finds all the open spots
              if (counter == GRID_X_AMOUNT) {
                  linesCleared++;
                  //Shift blocks down
                  for (int j = 0; j < GRID_X_AMOUNT; j++) {
                      for (int k = i; k > 0; k--) {
                          changeAvailabilityOfSpot(j, k, returnSpotAvailability(j, k - 1)); //Locks the new shapes from the row above and shifts them down
                      }
                  }
                  //Sets the row to false, and can allow shapes to be places there
                  for (int j = 0; j < GRID_X_AMOUNT; j++) {
                      changeAvailabilityOfSpot(j, 0, false); //Unlocks all the shapes 
                  }
                  i++;
              }
          }
          return linesCleared; //Returns the amount of lines cleared to be added to the score
      }
    
}
