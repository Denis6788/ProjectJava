package gabriel.com;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RushHour {
    public static final int BOARD_DIM = 6;
    public static final char RED_SYMBOL = 'R';
    public static final char EMPTY_SYMBOL = '.';
    public static final Position EXIT_POS = new Position(2, 5);

    private Map<Character, Vehicle> vehicles;
    private char[][] board;
    private int moveCount;

    public RushHour(String filename) throws IOException {
        vehicles = new HashMap<>();
        board = new char[BOARD_DIM][BOARD_DIM];
        initializeBoard();

        List<String> lines = Files.readAllLines(Paths.get(filename));
        for (String line : lines) {
            String[] parts = line.split(",");
            char symbol = parts[0].charAt(0);
            Position back = new Position(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
            Position front = new Position(Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
            Vehicle vehicle = new Vehicle(symbol, back, front);
            addVehicle(vehicle);
            updateBoard(vehicle);
        }
    }

    private void initializeBoard() {
        for (int row = 0; row < BOARD_DIM; row++) {
            Arrays.fill(board[row], EMPTY_SYMBOL);
        }
    }

    private void updateBoard(Vehicle vehicle) {
        // Clear old positions
        for (int row = 0; row < BOARD_DIM; row++) {
            for (int col = 0; col < BOARD_DIM; col++) {
                if (board[row][col] == vehicle.getSymbol()) {
                    board[row][col] = EMPTY_SYMBOL;
                }
            }
        }

        // Mark new positions
        Position back = vehicle.getBack();
        Position front = vehicle.getFront();

        if (back.getRow() == front.getRow()) {
            for (int col = back.getCol(); col <= front.getCol(); col++) {
                board[back.getRow()][col] = vehicle.getSymbol();
            }
        } else {
            for (int row = back.getRow(); row <= front.getRow(); row++) {
                board[row][back.getCol()] = vehicle.getSymbol();
            }
        }
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.put(vehicle.getSymbol(), vehicle);
    }

    public void moveVehicle(Move move) throws RushHourException {
        Vehicle vehicle = vehicles.get(move.getSymbol());
        if (vehicle == null) {
            throw new RushHourException("Vehicle '" + move.getSymbol() + "' not found!");
        }

        Direction dir = move.getDirection();
        if ((vehicle.isHorizontal() && (dir == Direction.UP || dir == Direction.DOWN)) ||
            (vehicle.isVertical() && (dir == Direction.LEFT || dir == Direction.RIGHT))) {
            throw new RushHourException("Invalid direction for vehicle!");
        }

        Position oldBack = vehicle.getBack();
        Position oldFront = vehicle.getFront();
        Position newBack = calculateNewPosition(oldBack, dir);
        Position newFront = calculateNewPosition(oldFront, dir);

        if (isOutOfBounds(newBack) || isOutOfBounds(newFront)) {
            throw new RushHourException("Move would go off the board!");
        }

        if (isCollision(newBack, vehicle) || isCollision(newFront, vehicle)) {
            throw new RushHourException("Collision detected!");
        }

        vehicle.move(dir);
        updateBoard(vehicle);
        moveCount++;
    }

    private Position calculateNewPosition(Position pos, Direction dir) {
        switch (dir) {
            case LEFT: return new Position(pos.getRow(), pos.getCol() - 1);
            case RIGHT: return new Position(pos.getRow(), pos.getCol() + 1);
            case UP: return new Position(pos.getRow() - 1, pos.getCol());
            case DOWN: return new Position(pos.getRow() + 1, pos.getCol());
            default: return pos;
        }
    }

    private boolean isOutOfBounds(Position pos) {
        return pos.getRow() < 0 || pos.getRow() >= BOARD_DIM ||
               pos.getCol() < 0 || pos.getCol() >= BOARD_DIM;
    }

    private boolean isCollision(Position pos, Vehicle currentVehicle) {
        return board[pos.getRow()][pos.getCol()] != EMPTY_SYMBOL &&
               board[pos.getRow()][pos.getCol()] != currentVehicle.getSymbol();
    }

    public boolean isGameOver() {
        Vehicle redCar = vehicles.get(RED_SYMBOL);
        return redCar != null && redCar.getFront().equals(EXIT_POS);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public Collection<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        for (Vehicle vehicle : vehicles.values()) {
            for (Direction dir : Direction.values()) {
                try {
                    Vehicle clone = new Vehicle(vehicle.getSymbol(), vehicle.getBack(), vehicle.getFront());
                    clone.move(dir);
                    if (!isOutOfBounds(clone.getBack()) && !isOutOfBounds(clone.getFront()) &&
                        !isCollision(clone.getBack(), vehicle) && !isCollision(clone.getFront(), vehicle)) {
                        moves.add(new Move(vehicle.getSymbol(), dir));
                    }
                } catch (RushHourException ignored) {}
            }
        }
        return moves;
    }

    public Vehicle getVehicleBySymbol(char symbol) {
        return vehicles.get(symbol);
    }

    public Collection<Vehicle> getVehicles() {
        return vehicles.values();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < BOARD_DIM; row++) {
            for (int col = 0; col < BOARD_DIM; col++) {
                sb.append(board[row][col]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
