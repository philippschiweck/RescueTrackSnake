package Snake;

import Snake.AI.SnakeAI;
import Snake.GameLogic.*;
import Snake.View.Screen;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class SnakeGame{

    private final int SIZE_X = 7;
    private final int SIZE_Y = 15;

    public final char FOOD_SYMBOL = 'a';

    public final int WAIT_TIME = 150;

    public final Direction STARTING_DIRECTION = Direction.LEFT;

    private Snake snake;
    private ArrayList<Food> food;
    private Board board;
    private Direction direction;
    private boolean gameRunning;
    private int score;

    public static void main(String[] args){
        new SnakeGame();
    }

    public SnakeGame(){
        initGame();
        startGame();
    }

    private void initGame(){
        board = new Board(SIZE_X, SIZE_Y);
        food = new ArrayList<>();
        //Snake starts in the middle of the board
        snake = new Snake(new BoardPosition(SIZE_X / 2, SIZE_Y / 2));
        score = 0;

        //Add first food item
        food.add(new Food(new BoardPosition(SIZE_X / 2, SIZE_Y / 2 - 4), '1'));

    }

    private void startGame(){
        gameRunning = true;
        this.direction = STARTING_DIRECTION;
        gameLoop();
    }

    private void gameLoop(){

        while(gameRunning){
            Screen.drawScreen(board, food, snake);
            direction = SnakeAI.getNextMove(board, snake, food.get(0).getPosition());
            //TODO validate movement as legitimate
            snake.getHead().setDirection(direction);
            snake.moveSnake(direction);

            for(Food item: food){
                if(checkCollision(item.getPosition(), snake.getHead().getPosition())){
                    food.remove(item);
                    snake.extendBody();
                    score++;
                    addFood();
                }
            }

            gameRunning = !gameOver();
            try{
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }

        System.out.println(String.format("Game over! Score: %d", score));

    }


    private boolean gameOver(){
        boolean gameOver = false;
        SnakeSegment head = snake.getHead();

        if(head.getPosition().getPosX() < 0 ||
            head.getPosition().getPosX() >= SIZE_X ||
            head.getPosition().getPosY() < 0 ||
            head.getPosition().getPosY() >= SIZE_Y){
            gameOver = true;
        } else {
            for(SnakeSegment segment: snake.getBody()){
                if(checkCollision(segment.getPosition(), head.getPosition())){
                    gameOver = true;
                    break;
                }
            }
        }

        return gameOver;
    }

    private boolean checkCollision(BoardPosition pos1, BoardPosition pos2){
        return pos1.equals(pos2);
    }

    private BoardPosition generateRandomPos(){
        Random rand = new Random();

        int x = rand.nextInt(SIZE_X);
        int y = rand.nextInt(SIZE_Y);

        return new BoardPosition(x, y);
    }

    private void addFood(){
        BoardPosition newFoodPosition = generateRandomPos();

        //Get list of all body positions + head to compare to new Food item Position
        ArrayList<BoardPosition> snakeBodyPosition = new ArrayList<>();
        for(SnakeSegment segment: snake.getBody()){
            snakeBodyPosition.add(segment.getPosition());
        }
        snakeBodyPosition.add(snake.getHead().getPosition());

        while(snakeBodyPosition.contains(newFoodPosition)){
            newFoodPosition = generateRandomPos();
        }

        this.food.add(new Food(newFoodPosition, FOOD_SYMBOL));
    }

}
