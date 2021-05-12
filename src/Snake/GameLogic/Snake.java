package Snake.GameLogic;

import java.util.ArrayList;

public class Snake {
    public final char HEAD_SYMBOL = '@';
    public final char BODY_SYMBOL = 'o';

    private ArrayList<SnakeSegment> body;
    private SnakeSegment head;
    private SnakeSegment tail;

    public Snake(BoardPosition boardPosition){
        this.body = new ArrayList<>();
        this.head = new SnakeSegment(this, boardPosition, null, HEAD_SYMBOL);
        this.head.setDirection(Direction.LEFT);
        this.tail = this.head;

        extendBody();
        extendBody();
        extendBody();
    }

    public void extendBody(){
        BoardPosition newSegmentPosition;

        newSegmentPosition = new BoardPosition(tail.getPosition().getPosX(), tail.getPosition().getPosY());

        SnakeSegment newSegment = new SnakeSegment(this, newSegmentPosition, this.tail, BODY_SYMBOL);
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
    }
    public void moveDown(){
        BoardPosition nextHeadPosition = new BoardPosition(head.getPosition().getPosX() - 1, head.getPosition().getPosY());
        moveBody();
        head.setPosition(nextHeadPosition);
        head.setDirection(Direction.DOWN);
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
