package Snake.AI;

import Snake.GameLogic.*;

import java.lang.reflect.Array;
import java.util.ArrayList;

public abstract class SnakeAI {

    //Todo Issue: When snake blocks entire length of board, head is on the other side of the food item

    public static Direction getNextMove(Board board, Snake snake, BoardPosition target){

        Direction nextMove = snake.getHead().getDirection();

        ArrayList<Node> path = calculatePath(board, snake, target);

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

        return nextMove;
    }

    /**
     * Breadth first search path calculation.
     * @return List of Nodes from target to origin.
     */
    private static ArrayList<Node> calculatePath(Board board, Snake snake, BoardPosition target){
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
            for(Node node: getPossibleSurroundingNodes(current, target, snake.getBody())){
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

    private static boolean isBoardPositionInList(Node node, ArrayList<Node> list){
        boolean isInList = false;
        for(Node element: list){
            if(element.equals(node)){
                isInList = true;
            }
        }
        return isInList;
    }

    private static Node getShortestDistanceNode(ArrayList<Node> list){
        Node leastDistance = list.get(0);

        for(Node elem: list){
            if(elem.getDistance() < leastDistance.getDistance()){
                leastDistance = elem;
            }
        }
        return leastDistance;
    }

    private static ArrayList<Node> getPossibleSurroundingNodes(Node current, BoardPosition target, ArrayList<SnakeSegment> body){
        ArrayList<Node> fields = new ArrayList<>();

        BoardPosition currentPosition = current.getPosition();

        //A move out of bounds can never be shorter to the target than a move inbounds
        BoardPosition left = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() - 1);
        BoardPosition right = new BoardPosition(currentPosition.getPosX(), currentPosition.getPosY() + 1);
        BoardPosition up = new BoardPosition(currentPosition.getPosX() + 1, currentPosition.getPosY());
        BoardPosition down = new BoardPosition(currentPosition.getPosX() - 1, currentPosition.getPosY());

        ArrayList<BoardPosition> newPositions = new ArrayList<>();
        newPositions.add(left);
        newPositions.add(right);
        newPositions.add(up);
        newPositions.add(down);

        for(SnakeSegment segment: body){
            if(segment.getPosition().equals(left)){
                newPositions.remove(left);
            }
            if(segment.getPosition().equals(right)){
                newPositions.remove(right);
            }
            if(segment.getPosition().equals(up)){
                newPositions.remove(up);
            }
            if(segment.getPosition().equals(down)){
                newPositions.remove(down);
            }
        }

        for(BoardPosition position: newPositions){
            fields.add(new Node(position, calculateDistance(position, target), current));
        }

        return fields;
    }

    /**
     * Manhatten Distance calculation.
     *
     * @param current
     * @param target
     * @return
     */
    private static int calculateDistance(BoardPosition current, BoardPosition target){
        return Math.abs(current.getPosX() - target.getPosX()) + Math.abs(current.getPosY() - target.getPosY());
    }

    private static class Node{
        private BoardPosition position;
        private int distance;
        private Node parent;

        public Node(BoardPosition position, int distance, Node parent){
            this.position = position;
            this.distance = distance;
            this.parent = parent;
        }

        public BoardPosition getPosition() {
            return position;
        }

        public int getDistance() {
            return distance;
        }

        public Node getParent() {
            return parent;
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
}
