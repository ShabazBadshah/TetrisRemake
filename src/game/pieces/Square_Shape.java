package game.pieces;

import game.tetris.Grid;

import java.awt.Point;

public class Square_Shape extends TetrisPiece {

    public Square_Shape() {
        super(new Point(4, -1), new Point(5, -1), new Point(4, 0), new Point(5, 0), 1);
    }
    
    //A seperate method that does not allow the shape to be rotated
    //This method overides the super class' method to rotate the shape
    public boolean rotateShape(Grid grid) {
        return false;
    }
}
