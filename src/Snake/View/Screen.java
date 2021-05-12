package Snake.View;

import Snake.GameLogic.*;

import java.sql.SQLOutput;
import java.util.ArrayList;

public final class Screen {

    public static void drawScreen(Board board, ArrayList<Food> food, Snake snake){
        //Clear previous output
        clearConsole();

        char[][] screen = buildScreen(board, food, snake);
        //Print top line
        for(int i = 0; i < screen[0].length; i++){
            System.out.print('_');
        }
        System.out.println();
        for(int i = 0; i < screen.length; i++){
            System.out.print('|');
            for(int j = 0; j < screen[i].length; j++){
                System.out.print(screen[i][j]);
            }
            System.out.print('|');
            System.out.println();
        }
        //Print bottom line
        for(int i = 0; i < screen[0].length; i++){
            System.out.print('_');
        }
        System.out.println();
    }

    private static char[][] buildScreen(Board board, ArrayList<Food> food, Snake snake){

        BoardPosition[][] boardArray = board.getBoard();

        //Draw Empty Board
        char[][] screen = new char[boardArray.length][boardArray[0].length];

        for(int i = 0; i <boardArray.length; i++){
            for(int j = 0; j < boardArray[i].length; j++){
                screen[i][j] = ' ';
            }
        }

        //Draw Apples
        for(Food item: food){
            int posX = item.getPosition().getPosX();
            int posY = item.getPosition().getPosY();
            screen[posX][posY] = item.getSymbol();
        }

        //Draw Snake
        SnakeSegment head = snake.getHead();
        screen[head.getPosition().getPosX()][head.getPosition().getPosY()] = head.getSymbol();


        for(SnakeSegment segment: snake.getBody()){
            int posX = segment.getPosition().getPosX();
            int posY = segment.getPosition().getPosY();
            screen[posX][posY] = segment.getSymbol();
            //TODO If last Segment is at the boundary, and a new Segment is added, this Segment is out of Bounds when the Snake turns away from the
            // boundary - this causes ArrayIndexOutOfBoundsException
        }

        return screen;
    }

    /**
     * Clear console function found online. Does not work in intellij, but in regular console.
     */
    public final static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }
}
