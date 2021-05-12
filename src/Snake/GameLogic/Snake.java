package Snake.GameLogic;

import java.util.ArrayList;

public class Snake {
    public final char HEAD_SYMBOL = '@';
    public final char BODY_SYMBOL = 'o';

    // The head is the first segment of the Snake. It consumes the food and dictates the movement direction.
    private SnakeSegment head;
    // The body is a list of all the segments of the snake except the head. The tail is part of this list.
    private ArrayList<SnakeSegment> body;
    // Tail is the last segment of the snake body. This is important, as this is where new segments are added and movement starts
    private SnakeSegment tail;

    public Snake(BoardPosition boardPosition){
        this.body = new ArrayList<>();
        this.head = new SnakeSegment(this, boardPosition, null, HEAD_SYMBOL);
        this.head.setDirection(Direction.LEFT);
        this.tail = this.head;

        //TODO Manually add first 3 Snake segments, so they don't appear out of thin air
        extendBody();
        extendBody();
        extendBody();
    }

    /**
     * Adds a new SnakeSegment to the body
     */
    public void extendBody(){
        BoardPosition newSegmentPosition;

        newSegmentPosition = new BoardPosition(tail.getPosition().getPosX(), tail.getPosition().getPosY());

        SnakeSegment newSegment = new SnakeSegment(this, newSegmentPosition, this.tail, BODY_SYMBOL);
        newSegment.setDirection(tail.getDirection());

        this.body.add(newSegment);
        this.tail = newSegment;
    }

    //Movement - first moves the head to the next position according to the direction.
    public void moveSnake(Direction dir){
        //Validation if movement is possible is missing. SnakeAI currently handles this, but this opens up the possibility of illegal movement by the AI.
        //It currently does not seem to be the case that the AI moves illegally, but validation should still be implemented.
        // TODO move validation in GameLogic instead of AI
        BoardPosition nextHeadPosition;
        switch(dir){
            case LEFT:
                nextHeadPosition = new BoardPosition(head.getPosition().getPosX(), head.getPosition().getPosY() - 1);
                moveBody();
                break;
            case RIGHT:
                nextHeadPosition = new BoardPosition(head.getPosition().getPosX(), head.getPosition().getPosY()  + 1);
                moveBody();
                break;
            case UP:
                nextHeadPosition = new BoardPosition(head.getPosition().getPosX() + 1, head.getPosition().getPosY());
                moveBody();
                break;
            default: //(DOWN, which is the only direction possible at this point. Otherwise nextHeadPosition might not be set.)
                nextHeadPosition = new BoardPosition(head.getPosition().getPosX() - 1, head.getPosition().getPosY());
                moveBody();
                break;
        }
        head.setDirection(dir);
        head.setPosition(nextHeadPosition);

    }


    private void moveBody(){
        if(body.size() > 0){
            tail.moveSegment();
        }
    }

    public ArrayList<SnakeSegment> getBody() {
        return body;
    }

    public SnakeSegment getHead() {
        return head;
    }
}
