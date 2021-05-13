package Snake.AI;

import Snake.GameLogic.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SnakeAI {

    //Todo Issue: When snake blocks entire length of board, head is on the other side of the food item

    private WeightedBoardNode[][] weights;

    public SnakeAI(Board board){
        this.weights = calculateBoardWeights(board);
    }

    private WeightedBoardNode[][] calculateBoardWeights(Board board){

        WeightedBoardNode[][] newWeights = new WeightedBoardNode[board.getBoard().length][board.getBoard()[0].length];

        int boardCenterX = board.getBoard().length / 2;
        int boardCenterY = board.getBoard()[0].length / 2;

        for(int i = 0; i < board.getBoard().length; i++){
            for(int j = 0; j < board.getBoard()[i].length; j++){
                BoardPosition position = board.getBoard()[i][j];
                int weight = (int) (Math.min(boardCenterX, boardCenterY) * Math.log(1 + (boardCenterX - Math.abs(boardCenterX - i)) * (boardCenterY - Math.abs(boardCenterY - j))));

                newWeights[i][j] = new WeightedBoardNode(position, weight);
            }
        }

        //Print out weights when SnakeAI is created, for reference
        System.out.println("Snake AI Board weights:");
        for(int i = 0; i < newWeights.length; i++){
            for(int j = 0; j < newWeights[i].length; j++){
                System.out.print(newWeights[i][j].weight + " ");
            }
            System.out.println();
        }

        return newWeights;
    }

    /**
     * Calculates the next direction the snake has to move in order to get to the target.
     *
     * @param board Board that is being played on
     * @param snake The Snake that has to navigate on the board to the target.
     * @param target Target Boardposition that the Snake
     * @return The next direction the snake should move on the board in order to get to the target.
     */
    public Direction getNextMove(Board board, Snake snake, BoardPosition target){

        Direction nextMove = snake.getHead().getDirection();

        //get the optimal path to the target
        ArrayList<Node> path = calculatePath(board, snake, target);

        //if the path is not empty, get the next move. If the path is empty, there is no path to the target. The snake will then keep walk forward into its doom.
        if(!path.isEmpty() && path.size() != 1){
            BoardPosition currentPosition = snake.getHead().getPosition();

            BoardPosition left = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() - 1);
            BoardPosition right = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() + 1);
            BoardPosition up = new BoardPosition(currentPosition.getPosX() + 1, currentPosition.getPosY());
            BoardPosition down = new BoardPosition(currentPosition.getPosX() - 1, currentPosition.getPosY());

            //Get next movement Node //TODO What if Path is null?
            Node next = path.get(path.size() - 2);

            BoardPosition nextPosition = next.getPosition();

            if(left.equals(nextPosition)){
                nextMove = Direction.LEFT;
            } else if (right.equals(nextPosition)){
                nextMove = Direction.RIGHT;
            } else if (up.equals(nextPosition)) {
                nextMove = Direction.UP;
            } else if (down.equals(nextPosition)){
                nextMove = Direction.DOWN;
            }
        }
        return nextMove;
    }

    /**
     * Breadth first search path calculation.
     * @return List of Nodes from target to origin.
     */
    private ArrayList<Node> calculatePath(Board board, Snake snake, BoardPosition target){

        //Get weighted board positions

        //Current Elements in the queue
        ArrayList<Node> queue = new ArrayList<>();
        //Elements that have been seen
        ArrayList<Node> seen = new ArrayList<>();

        SnakeSegment start = snake.getHead();

        //Label root as discovered
        Node current = new Node(start.getPosition(), calculateDistance(start.getPosition(), target), null);
        queue.add(current);
        //Breadth-first search
        while(!queue.isEmpty()){
             current = getShortestDistanceNode(queue);
             queue.remove(current);
            if(current.getPosition().equals(target)){
                //TODO If Path does not find target node, but queue is empty
                // then return longest distance Node
                break;
            }
            for(Node node: getPossibleSurroundingNodes(board, current, target, snake.getBody())){
                if(!isBoardPositionInList(node, seen)){
                    seen.add(node);
                    queue.add(node);
                }
            }
        }

        //Retrace Path
        ArrayList<Node> path = new ArrayList<>();
        while(current != null){
            //TODO If Path does not find target node
            path.add(current);
            current = current.getParent();
        }
        return path;
    }

    private boolean isBoardPositionInList(Node node, ArrayList<Node> list){
        boolean isInList = false;
        for(Node element: list){
            if(element.equals(node)){
                isInList = true;
            }
        }
        return isInList;
    }

    private Node getShortestDistanceNode(ArrayList<Node> list){
        Node leastDistance = list.get(0);

        for(Node elem: list){
            if(elem.getDistance() < leastDistance.getDistance()){
                leastDistance = elem;
            }
        }
        return leastDistance;
    }

    private Node getLongestDistanceNode(ArrayList<Node> list){
        Node longestDistance = list.get(0);

        for(Node elem: list){
            if(elem.getDistance() > longestDistance.getDistance()){
                longestDistance = elem;
            }
        }
        return longestDistance;
    }

    /**
     *
     * @param board
     * @param current
     * @param target
     * @param body
     * @return
     */
    private ArrayList<Node> getPossibleSurroundingNodes(Board board, Node current, BoardPosition target, ArrayList<SnakeSegment> body){

        ArrayList<Node> possibleNodes = new ArrayList<>();

        BoardPosition currentPosition = current.getPosition();

        //A move out of bounds can never be shorter to the target than a move inbounds
        BoardPosition left = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() - 1);
        BoardPosition right = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() + 1);
        BoardPosition up = new BoardPosition(currentPosition.getPosX() + 1, currentPosition.getPosY());
        BoardPosition down = new BoardPosition(currentPosition.getPosX() - 1, currentPosition.getPosY());

        //Remove nodes that are out of bounds
        ArrayList<BoardPosition> newPositions = new ArrayList<>();
        //Add left if it is inbounds
        if(isInBounds(board, left)){
            newPositions.add(left);
        }
        //Add right if it is inbounds
        if(isInBounds(board, right)){
            newPositions.add(right);
        }
        //Add up if it is inbounds
        if(isInBounds(board, up)){
            newPositions.add(up);
        }
        //Add right down it is inbounds
        if(isInBounds(board, down)){
            newPositions.add(down);
        }

        //Remove possible nodes that are inside the body of the snake, but only if the snake body is still there at the time of the head reaching this position.
        for(int i = 0; i < body.size() - current.distanceToRoot; i++){
            if(!newPositions.isEmpty()){
                for(BoardPosition position: newPositions){
                    if (body.get(i).getPosition().equals(position)) {
                        newPositions.remove(position);
                        break;
                    }
                }
            } else {
                break;
            }
        }

        for(BoardPosition position: newPositions){
            double distanceWithWeight = calculateDistance(position, target) + weights[position.getPosX()][position.getPosY()].weight;
            possibleNodes.add(new Node(position, distanceWithWeight, current));
        }

        return possibleNodes;
    }

    private boolean isInBounds(Board board, BoardPosition position){
        return position.getPosY() >= 0 && position.getPosY() < board.getBoard()[0].length &&
                position.getPosX() >= 0 && position.getPosX() < board.getBoard().length;
    }

    /**
     * Manhatten Distance calculation.
     *
     * @param current
     * @param target
     * @return
     */
    private int calculateDistance(BoardPosition current, BoardPosition target){
        return Math.abs(current.getPosX() - target.getPosX()) + Math.abs(current.getPosY() - target.getPosY());
    }

    private class Node{
        private BoardPosition position;
        private double distance;
        private Node parent;
        private int distanceToRoot;

        public Node(BoardPosition position, double distance, Node parent){
            this.position = position;
            this.distance = distance;
            this.parent = parent;
            if(parent != null){
                this.distanceToRoot += this.parent.getDistanceToRoot();
            } else { //This is the root if parent == null
                this.distanceToRoot = 0;
            }

        }

        public BoardPosition getPosition() {
            return position;
        }

        public double getDistance() {
            return distance;
        }

        public Node getParent() {
            return parent;
        }

        public int getDistanceToRoot() {
            return distanceToRoot;
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
                Node node = (Node) obj;
                if(node.getPosition().equals(this.position)){
                    equals = true;
                } else {
                    equals = false;
                }
            }
            return equals;
        }
    }

    private class WeightedBoardNode{

        private BoardPosition position;
        private double weight;

        public WeightedBoardNode(BoardPosition position, double weight){
            this.position = position;
            this.weight = weight;

        }
    }

}
