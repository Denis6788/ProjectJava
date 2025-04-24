package com.example;


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
        // Added a list of observers to notify when a vehicle moves
        private List<RushHourObserver> observers = new ArrayList<>();
        // Constants for the game added
        public static final int BOARD_DIM = 6;
        public static final char RED_SYMBOL = 'R';
        public static final char EMPTY_SYMBOL = '.';
        public static final Position EXIT_POS = new Position(2, 5);
        private Map<Character, Vehicle> vehicles;
        // The board is now a 2D array of characters
        // to represent the vehicles and empty spaces
        private char[][] board;
        private int moveCount;

        // Added a rush hour constructor that takes a filename
        // and initializes the board with vehicles from the file
        public RushHour(String filename) throws IOException {
            vehicles = new HashMap<>();
            board = new char[BOARD_DIM][BOARD_DIM];
            initializeBoard(); // Initialize the board with empty symbols

            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                String[] parts = line.split(",");
                char symbol = parts[0].charAt(0);
                Position back = new Position(
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
                );
                Position front = new Position(
                    Integer.parseInt(parts[3]),
                    Integer.parseInt(parts[4])
                );
                Vehicle vehicle = new Vehicle(symbol, back, front);
                addVehicle(vehicle);
            }
        }
        private void initializeBoard() {
            for (int row = 0; row < BOARD_DIM; row++) {
                Arrays.fill(board[row], EMPTY_SYMBOL);
            }
        }
        // This method is called to update the board after a vehicle has moved
        // It clears the old positions of the vehicle and marks the new positions
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

            // Calculate new positions
            Position oldBack = vehicle.getBack();
            Position oldFront = vehicle.getFront();
            Position newBack = calculateNewPosition(oldBack, dir);
            Position newFront = calculateNewPosition(oldFront, dir);

            // Check boundaries
            // added check for out of bounds
            // Check if the new positions are within the board limits
            if (isOutOfBounds(newBack) || isOutOfBounds(newFront)) {
                throw new RushHourException("Move would go off the board!");
            }

            // Check collisions (excluding the vehicle's own positions)
            if (isCollision(newBack, vehicle) || isCollision(newFront, vehicle)) {
                throw new RushHourException("Collision detected!");
            }

            // Update vehicle and board
            vehicle.move(dir);
            updateBoard(vehicle);
            moveCount++;
            notifyObservers(vehicle); // Notify observers of the move
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
        // Check if the position is out of bounds added
        private boolean isOutOfBounds(Position pos) {
            return pos.getRow() < 0 || pos.getRow() >= BOARD_DIM ||
                pos.getCol() < 0 || pos.getCol() >= BOARD_DIM;
        }
        // Check if the position collides with another vehicle added
        private boolean isCollision(Position pos, Vehicle currentVehicle) {
            return board[pos.getRow()][pos.getCol()] != EMPTY_SYMBOL &&
                board[pos.getRow()][pos.getCol()] != currentVehicle.getSymbol();
        }
        @Override
        // Added a toString method to visualize the board
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
        // Added a method to check if the game is over
        public boolean isGameOver() {
            Vehicle redCar = vehicles.get(RED_SYMBOL);
            return redCar != null && redCar.getFront().equals(EXIT_POS);
        }
        // Added a method to get the number of moves made
        public int getMoveCount() {
            return moveCount;
        }
        // Added a method to get the possible moves for all vehicles
        public Collection<Move> getPossibleMoves() {
            List<Move> moves = new ArrayList<>();
            for (Vehicle vehicle : vehicles.values()) {
                for (Direction dir : Direction.values()) {
                    // Simuliraj potez bez promjene stanja
                    if (isMoveValid(vehicle, dir)) {
                        moves.add(new Move(vehicle.getSymbol(), dir));
                    }
                }
            }
            return moves;
        }
        public void registerObserver(RushHourObserver observer) {
            observers.add(observer);
        }
        private void notifyObservers(Vehicle vehicle) {
            for (RushHourObserver observer : observers) {
                observer.vehicleMoved(new Vehicle(vehicle));
            }
        }
        public Collection<Vehicle> getVehicles() {
            List<Vehicle> copies = new ArrayList<>();
            for (Vehicle v : vehicles.values()) {
                copies.add(new Vehicle(v)); // Koristi copy constructor
            }
            return copies;
        }
        private boolean isMoveValid(Vehicle vehicle, Direction dir) {
            try {
                // Calculate new positions
                Position newBack = calculateNewPosition(vehicle.getBack(), dir);
                Position newFront = calculateNewPosition(vehicle.getFront(), dir);
        
                // Checks boundaries
                if (isOutOfBounds(newBack) || isOutOfBounds(newFront)) {
                    return false;
                }
        
                // Check collisions
                if (isCollision(newBack, vehicle) || isCollision(newFront, vehicle)) {
                    return false;
                }
        
                // Check direction validity
                if ((vehicle.isHorizontal() && (dir == Direction.UP || dir == Direction.DOWN)) ||
                    (vehicle.isVertical() && (dir == Direction.LEFT || dir == Direction.RIGHT))) {
                    return false;
                }
        
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
