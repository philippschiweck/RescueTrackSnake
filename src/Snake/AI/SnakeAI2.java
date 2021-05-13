package Snake.AI;

import Snake.GameLogic.Board;
import Snake.GameLogic.BoardPosition;
import Snake.GameLogic.Direction;
import Snake.GameLogic.Snake;

public class SnakeAI2 {

    public static Direction getNextMove(Board board, Snake snake){
        //Board
        Direction nextMove = snake.getHead().getDirection();

        return Direction.LEFT;
    }
}
