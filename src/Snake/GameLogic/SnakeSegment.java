package Snake.GameLogic;

public class SnakeSegment extends Segment {

    private Snake snake;
    private Direction direction;
    private SnakeSegment parent;
    private int distanceToHead;

    public SnakeSegment(Snake snake, BoardPosition boardPosition, SnakeSegment parent, char snakeSymbol){
        super(boardPosition, snakeSymbol);
        this.snake = snake;
        this.parent = parent;

        if(parent != null){
            this.distanceToHead += this.parent.getDistanceToHead();
        } else { //This is the root if parent == null
            this.distanceToHead = 0;
        }
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public SnakeSegment getParent() {
        return parent;
    }

    public int getDistanceToHead() {
        return distanceToHead;
    }

    public void moveSegment(){
        if(parent != null){
            this.setPosition(parent.getPosition());
            this.setDirection(parent.getDirection());
            parent.moveSegment();
        }
    }
}
