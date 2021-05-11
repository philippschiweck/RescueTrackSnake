package Snake.GameLogic;

public enum Direction {
    UP {
        Direction opposite() {
            return DOWN;
        }
    },
    DOWN{
            Direction opposite() {
                return UP;
            }
    },
    RIGHT{
        Direction opposite() {
            return LEFT;
        }
    },
    LEFT{
        Direction opposite() {
            return RIGHT;
        }
    }
}
