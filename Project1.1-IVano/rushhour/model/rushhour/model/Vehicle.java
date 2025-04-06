
public class Vehicle {
    private char symbol;
    private Position back;
    private Position front;

    public Vehicle(char symbol, Position back, Position front) {
        this.symbol = symbol;
        this.back = back;
        this.front = front;
    }
    // Copy constructor
    // This constructor creates a new Vehicle object as a copy of another Vehicle object
    public Vehicle(Vehicle other) {
        this.symbol = other.symbol;
        this.back = new Position(other.back.getRow(), other.back.getCol());
        this.front = new Position(other.front.getRow(), other.front.getCol());
    }

    public char getSymbol() {
        return symbol;
    }

    public Position getBack() {
        return back;
    }

    public Position getFront() {
        return front;
    }

    public void move(Direction dir) throws RushHourException {
        if (isHorizontal() && (dir == Direction.UP || dir == Direction.DOWN)) {
            throw new RushHourException("Invalid move! Horizontal vehicles can only move left or right.");
        }
        if (isVertical() && (dir == Direction.LEFT || dir == Direction.RIGHT)) {
            throw new RushHourException("Invalid move! Vertical vehicles can only move up or down.");
        }

        switch (dir) {
            case LEFT:
                back = new Position(back.getRow(), back.getCol() - 1);
                front = new Position(front.getRow(), front.getCol() - 1);
                break;
            case RIGHT:
                back = new Position(back.getRow(), back.getCol() + 1);
                front = new Position(front.getRow(), front.getCol() + 1);
                break;
            case UP:
                back = new Position(back.getRow() - 1, back.getCol());
                front = new Position(front.getRow() - 1, front.getCol());
                break;
            case DOWN:
                back = new Position(back.getRow() + 1, back.getCol());
                front = new Position(front.getRow() + 1, front.getCol());
                break;
        }
    }
    // These methods are now public to allow access from the RushHour class
    public boolean isHorizontal() {
        return back.getRow() == front.getRow();
    }

    public boolean isVertical() {
        return back.getCol() == front.getCol();
    }
}
