package Snake.AI;

import Snake.GameLogic.*;

import java.util.ArrayList;

/**
 *  AI that has the ability to calculate paths for the snake in the game Snake.
 */
public class SnakeAI {

    private WeightedBoardNode[][] weights;

    /**
     *  Constructor for the SnakeAI object. Upon creation, the {@link SnakeAI#weights} array is filled with the corresponding Nodes according to the board size
     *  with the function {@link SnakeAI#calculateBoardWeights(Board)}.
     *
     * @param board the {@link Board} the game is being played on.
     */
    public SnakeAI(Board board){
        this.weights = calculateBoardWeights(board);
    }

    /**
     *  Calculates the weights for each individual BoardPosition of the corresponding {@link Board}.
     *
     * @param board the {@link Board} the game is being played on.
     * @return An Array of {@link WeightedBoardNode}s containing weights in accordance with the board size.
     */
    private WeightedBoardNode[][] calculateBoardWeights(Board board){

        WeightedBoardNode[][] newWeights = new WeightedBoardNode[board.getBoard().length][board.getBoard()[0].length];

        //half lengths of the board are needed to calculate increase of weights towards the center
        int halfLengthX = (board.getBoard().length) / 2;
        int halfLengthY = (board.getBoard()[0].length) / 2;

        for(int i = 0; i < board.getBoard().length; i++){
            for(int j = 0; j < board.getBoard()[i].length; j++){
                BoardPosition position = board.getBoard()[i][j];

                // Difference to middle of board in X / Y direction of Square board[i][j]
                int xDiff = halfLengthX - Math.abs(i  - halfLengthX) ;
                int yDiff = halfLengthY - Math.abs(j  - halfLengthY) ;

                //2 different weight calculations.
                //weight1 creates a "pyramid" structure of values increasing logarithmically towards the center
                int weight1 = (int)(board.getBoard().length * Math.log(1 + Math.min(xDiff, yDiff)));
                //weight2 creates a circular logarithmic increase towards the center, favouring the edges as well as the corners of the board
                int weight2 = (int) (board.getBoard().length * Math.log(1 + xDiff * yDiff));

                newWeights[i][j] = new WeightedBoardNode(position, weight2);
            }
        }

        //Print out weights when SnakeAI is created, for reference
        System.out.println("Snake AI Board weights:");
        for (WeightedBoardNode[] row : newWeights) {
            for (WeightedBoardNode newWeight: row) {
                System.out.print(newWeight.weight + " ");
            }
            System.out.println();
        }

        return newWeights;
    }

    /**
     * Calculates the next {@link Direction} the snake has to move in order to get to the target.
     *
     * @param board {@link Board} that is being played on.
     * @param snake The {@link Snake} that has to navigate on the board to the target.
     * @param target Target {@link BoardPosition} that the Snake must reach.
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
                if(!seen.contains(node)){
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

    /**
     * Searches for the {@link Node} in the list with the shortest {@link Node#distance} property.
     *
     * @param list List of {@link Node}s.
     * @return The {@link Node} with the shortest {@link Node#distance}.
     */
    private Node getShortestDistanceNode(ArrayList<Node> list){
        Node leastDistance = list.get(0);

        for(Node elem: list){
            if(elem.getDistance() < leastDistance.getDistance()){
                leastDistance = elem;
            }
        }
        return leastDistance;
    }

    /**
     * Searches for the {@link Node} in the list with the longest {@link Node#distance} property.
     *
     * @param list List of {@link Node}s.
     * @return The {@link Node} with the longest {@link Node#distance}.
     */
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
     *  Find the Nodes surrounding the current {@link Node} that movement is possible to..
     *  BoardPositions that are out of bounds are not included.
     *  BoardPositions that are inside the snakes body are also not included, expect for if the snakes body occupying the Position has already moved on
     *  at that point in the path.
     *  Weights for the nodes are calculated, and an ArrayList of the Nodes is returned.
     * @param board The {@link Board} the game of Snake is being played on.
     * @param current The current {@link Node} of which the surrounding Nodes have to be calculated.
     * @param target The target {@link BoardPosition} the snake has to move to. This is needed for distance calculations for the new {@link Node}s.
     * @param body The body of the {@link Snake}.
     * @return An ArrayList of the Nodes that can be moved to by the Snake.
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

    /**
     * Checks if a given {@link BoardPosition} is inside the {@link Board} that is being played on.
     * @param board The {@link Board} the game is being played on.
     * @param position The {@link BoardPosition} that needs to be checked.
     * @return true if the position is on the board, false if it is outside of the board.
     */
    private boolean isInBounds(Board board, BoardPosition position){
        return position.getPosY() >= 0 && position.getPosY() < board.getBoard()[0].length &&
                position.getPosX() >= 0 && position.getPosX() < board.getBoard().length;
    }

    /**
     * Calculates the Manhattan Distance from one BoardPosition to another.
     * @param current The first {@link BoardPosition}.
     * @param target The second {@link BoardPosition}.
     * @return The Manhattan Distance between current and target Position.
     */
    private int calculateDistance(BoardPosition current, BoardPosition target){
        return Math.abs(current.getPosX() - target.getPosX()) + Math.abs(current.getPosY() - target.getPosY());
    }

    /**
     * A Node containing a BoardPosition, a weighted distance, a parent Node, as well as the distance to the root Node from where the pathfinding started.
     * The root Node has null as its parent.
     */
    private class Node{
        private BoardPosition position;
        private double distance;
        private Node parent;
        private int distanceToRoot;

        /**
         * Creates a node object. The distanceToRoot is set as 0 if this is the root Node. Otherwise, 1 is added to the parents distanceToRoot.
         * @param position The {@link BoardPosition} of the Node.
         * @param distance The weighted distance for the snake to move to this Node, according to the target.
         * @param parent The parent Node of this node.
         */
        public Node(BoardPosition position, double distance, Node parent){
            this.position = position;
            this.distance = distance;
            this.parent = parent;
            if(parent != null){
                this.distanceToRoot = this.parent.getDistanceToRoot() + 1;
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
                //If a Node has the same BoardPosition as another Node, it is the same node.
                equals = node.getPosition().equals(this.position);
            }
            return equals;
        }
    }

    /**
     *  Contains a BoardPosition and an associated weight.
     *  Used to set weights for an entire Board of {@link BoardPosition}s.
     */
    private class WeightedBoardNode{

        private BoardPosition position;
        private int weight;

        public WeightedBoardNode(BoardPosition position, int weight){
            this.position = position;
            this.weight = weight;

        }
    }

}
