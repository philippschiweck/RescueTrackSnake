package Snake.GameLogic;

import java.util.ArrayList;

public class Snake {
    public final char HEAD_SYMBOL = '@';
    public final char BODY_SYMBOL = 'o';

    private ArrayList<SnakeSegment> body;
    private SnakeSegment head;
    SnakeSegment tail;

    public Snake(BoardPosition boardPosition){
        this.body = new ArrayList<>();
        this.head = new SnakeSegment(this, boardPosition, HEAD_SYMBOL);
        this.head.setDirection(Direction.LEFT);
        this.tail = this.head;

        extendBody();
        extendBody();
        extendBody();
    }

    public void extendBody(){
        BoardPosition newSegmentPosition;

        //TODO add last Segment behind Tail
        newSegmentPosition = new BoardPosition(tail.getPosition().getPosX(), tail.getPosition().getPosY());

        SnakeSegment newSegment = new SnakeSegment(this, newSegmentPosition, BODY_SYMBOL);
        newSegment.setDirection(tail.getDirection());

        this.body.add(newSegment);
        this.tail = newSegment;
    }

    //Movement
    public void moveLeft(){
        BoardPosition nextHeadPosition = new BoardPosition(head.getPosition().getPosX(), head.getPosition().getPosY() - 1);
        moveBody();
        head.setPosition(nextHeadPosition);
        head.setDirection(Direction.LEFT);
    }
    public void moveRight(){
        BoardPosition nextHeadPosition = new BoardPosition(head.getPosition().getPosX(), head.getPosition().getPosY()  + 1);
        moveBody();
        head.setPosition(nextHeadPosition);
        head.setDirection(Direction.RIGHT);
    }
    public void moveUp(){
        BoardPosition nextHeadPosition = new BoardPosition(head.getPosition().getPosX() + 1, head.getPosition().getPosY());
        moveBody();
        head.setPosition(nextHeadPosition);
        head.setDirection(Direction.UP);
    }
    public void moveDown(){
        BoardPosition nextHeadPosition = new BoardPosition(head.getPosition().getPosX() - 1, head.getPosition().getPosY());
        moveBody();
        head.setPosition(nextHeadPosition);
        head.setDirection(Direction.DOWN);
    }

    private void moveBody(){
        if(body.size() > 0){
            for(int i = body.size() - 1; i > 0; i--){
                SnakeSegment currentSegment = body.get(i);
                SnakeSegment nextSegment = body.get(i - 1);
                currentSegment.setDirection(nextSegment.getDirection());
                currentSegment.setPosition(nextSegment.getPosition());
            }

            SnakeSegment segment = body.get(0);
            segment.setDirection(head.getDirection());
            segment.setPosition(head.getPosition());
        }
    }

    public ArrayList<SnakeSegment> getBody() {
        return body;
    }

    public SnakeSegment getHead() {
        return head;
    }
}
