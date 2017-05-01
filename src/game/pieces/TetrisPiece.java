package game.pieces;

import game.tetris.Grid;

import java.awt.*;

import javax.swing.ImageIcon;

/*
 * This class is reponsible for the actual logic of the game, it moves the shapes, and creates them
 * it is also responsible for drawing the shape 
 */

public class TetrisPiece {

	private Point[] piecesOfShape; //An array that stores the 4 points of a shape
	private final int COLOUR_OF_SHAPE; //Colour of the shape

	/* 
	 * This constructor take in 5 parameters, the first 4 are the points that the shapes will be 
	 * drawn on (the individual squares), and the last one is the colour of the the complete shape
	 */
	public TetrisPiece(Point firstSquare, Point secondSquare, Point thirdSquare, Point fourthSquare, int COLOUR_OF_SHAPE) {
		piecesOfShape = new Point[4];
		/*
		 * Each point in the shape is drawn on the next grid, a 3*3 grid
		 */
		piecesOfShape[0] = firstSquare;
		piecesOfShape[1] = secondSquare;
		piecesOfShape[2] = thirdSquare;
		piecesOfShape[3] = fourthSquare;
		this.COLOUR_OF_SHAPE = COLOUR_OF_SHAPE;
	}

	/*
	 * Checks to see if any part of the shape is at the bottom of the board
	 * or if it is ontop of another shape
	 */
	public boolean isShapeMoving(Grid grid) {
		for (Point point : piecesOfShape) {
			if (point.y == grid.getGridYAmount() - 1) { //If the first shape has reached the bottom
				return true;
			} else if (grid.returnSpotAvailability(point.x, point.y + 1)) {
				return true; 
			}
		}
		return false;
	}
	
	/*
	 * Drops the shape to the bottom by calling the moveShape method
	 */
	  public void dropShapeToBottom(Grid grid) {
	        while (!isShapeMoving(grid)) {
	            moveShape(grid, 0, 1); //Places the shape in its current column
	        }
	        //Places the piece at the spot it lands at
	        grid.placePieceAtSpot(this);
	    }

	//Returns the location of all of the points that make up the shape
	public Point[] getPiece() {
		return piecesOfShape;
	}

	/*
	 * Moves the shape according to the x and y value
	 */
	public boolean moveShape(Grid grid, int x, int y) {
		if (x == 0 && y == 0) {
			return false;
		} else if (grid.checkSpotAvailability(this, x, y)) {
			for (Point point : piecesOfShape) {
				point.x += x; //Moves the shape 1 unit over (left or right)
				point.y += y; //Moves the shape 1 unit down
			}
			return true;
		}
		return false;
	}

	/* 
	 * Rotates the shape
	 * 
	 * This part took a very long time to complete, I refered to these bottom two links multiple times and used
	 * part of their solution to assist me, the only thing that really changes when a shape is rotated is the 
	 * x or y position of the individual square that makes up the shape
	 * 
	 * http://www.mathwarehouse.com/transformations/rotations-in-math.php
	 * https://bordiani.wordpress.com/2014/10/20/tetris-in-java-part-iii-rotating-the-figures-and-clearing-full-lines/
	 * http://stackoverflow.com/questions/233850/tetris-piece-rotation-algorithm
	 * http://cslibrary.stanford.edu/112/Tetris-Architecture.html
	 */
	public boolean rotateShape (Grid grid) {
		Point rotatePoint = piecesOfShape[3];//Point to rotate about
		for (int i = 0; i < piecesOfShape.length - 1; i++) {
			if (piecesOfShape[i].x < rotatePoint.x) {//Point is to the left of the rotatePoint
				if (piecesOfShape[i].y > rotatePoint.y) {//Point is above of the rotatePoint
					piecesOfShape[i].y -= 2;
				} else if (piecesOfShape[i].y < rotatePoint.y) {//Point is below the rotatePoint
					piecesOfShape[i].x += 2;
				} else {//Point is to the left (right next to it) of the rotatePoint
					piecesOfShape[i].y -= rotatePoint.x - piecesOfShape[i].x;
					piecesOfShape[i].x = rotatePoint.x;
				}
			} else if (piecesOfShape[i].x > rotatePoint.x) {//Point is to the right of the rotatePoint
				if (piecesOfShape[i].y > rotatePoint.y) {//Point is above the rotatePoint
					piecesOfShape[i].x -= 2;
				} else if (piecesOfShape[i].y < rotatePoint.y) {//Point is below rotatePoint
					piecesOfShape[i].y += 2;
				} else {//Point is to the right (right next to it) of the rotatePoint
					piecesOfShape[i].y += piecesOfShape[i].x - rotatePoint.x;
					piecesOfShape[i].x = rotatePoint.x;
				}
			} else {//Point is above or below the rotatePoint
				if (piecesOfShape[i].y > rotatePoint.y) {//Point is directly above the rotatePoint
					piecesOfShape[i].x -= piecesOfShape[i].y - rotatePoint.y;
					piecesOfShape[i].y = rotatePoint.y;
				} else {//Point is directly below the rotatePoint
					piecesOfShape[i].x += rotatePoint.y - piecesOfShape[i].y;
					piecesOfShape[i].y = rotatePoint.y;
				}
			}
		}
		//If the Shape cannot stay where it is, try moving it by 1 or 2 squares in each direction
		//This part is mainly used to check for the collisions at the side 
		if (!grid.checkSpotAvailability(this, 0, 0) && !moveShape(grid, 1, 0) && !moveShape(grid, -1, 0) && !moveShape(grid, 0, -1) && !moveShape(grid, 0, 1) && !moveShape(grid, 2, 0) && !moveShape(grid, -2, 0) && !moveShape(grid, 0, -2) && !moveShape(grid, 0, 2)) {
			//If the shape cannot be places there rotate, the shape to its previous orientation
			rotateShape(grid);
			return false;
		}
		return true;
	}

	/*
	 * Draws each square shape of the panel using the locations of the points of the shape
	 */
	public void drawShape(Graphics g, int x, int y) {
		for (Point point : getPiece()) {
			//Draws the shape from the squares
			g.drawImage(new ImageIcon(getClass().getResource("/res/" + COLOUR_OF_SHAPE + ".png")).getImage(), x + point.x * 30, y + point.y * 30, null);
		}
	}
}
