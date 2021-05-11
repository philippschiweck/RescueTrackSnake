package Snake.GameLogic;

public abstract class Segment {

    private BoardPosition position;
    private char symbol;

    public Segment(BoardPosition boardPosition, char symbol){
        this.position = boardPosition;
        this.symbol = symbol;
    }

    //Get Set
    public BoardPosition getPosition() {
        return position;
    }

    public void setPosition(BoardPosition position) {
        this.position = position;
    }

    public char getSymbol() {
        return symbol;
    }

    public boolean detectCollision(Segment se){
        return this.position.equals(se);
    }
}
