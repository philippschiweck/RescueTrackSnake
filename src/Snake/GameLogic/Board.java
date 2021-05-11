package Snake.GameLogic;

public class Board {

    private BoardPosition[][] board;

    public Board(int sizeX, int sizeY){
        initBoard(sizeX, sizeY);
    }

    private void initBoard(int sizeX, int sizeY){
        this.board = new BoardPosition[sizeX][sizeY];
        for(int i = 0; i < board.length; i++){
            for(int j = 0; j < board[i].length; j++){
                board[i][j] = new BoardPosition(i, j);
            }
        }
    }

    public BoardPosition[][] getBoard() {
        return board;
    }

    public BoardPosition getPosition(int posX, int posY){
        return board[posX][posY];
    }
}
