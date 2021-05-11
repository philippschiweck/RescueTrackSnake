package Snake.GameLogic;

public class SnakeSegment extends Segment {

    private Snake snake;
    private Direction direction;

    public SnakeSegment(Snake snake, BoardPosition boardPosition, char snakeSymbol){
        super(boardPosition, snakeSymbol);
        this.snake = snake;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }
}
