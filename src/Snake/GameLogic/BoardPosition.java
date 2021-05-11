package Snake.GameLogic;

public class BoardPosition {

    private int posX;
    private int posY;

    public BoardPosition(int posX, int posY){
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public boolean equals(Object obj){
        boolean equals;
        if(obj == this){
            equals = true;
        }
        else if(obj == null || obj.getClass() != this.getClass()){
            equals = false;
        } else {
            BoardPosition boardPosition = (BoardPosition) obj;
            if(boardPosition.getPosX() == this.posX && boardPosition.getPosY() == this.posY){
                equals = true;
            } else {
                equals = false;
            }
        }
        return equals;
    }
}
