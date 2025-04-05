


//Made by Denis

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private char symbol;
    private int frontRow, frontCol;
    private int backRow, backCol;
    private List<RushHourObserver> observers = new ArrayList<>();

    public Vehicle(char symbol, int frontRow, int frontCol, int backRow, int backCol) {
        this.symbol = symbol;
        this.frontRow = frontRow;
        this.frontCol = frontCol;
        this.backRow = backRow;
        this.backCol = backCol;
    }

    // Copy constructor
    public Vehicle(Vehicle other) {
        this.symbol = other.symbol;
        this.frontRow = other.frontRow;
        this.frontCol = other.frontCol;
        this.backRow = other.backRow;
        this.backCol = other.backCol;
    }

    public Vehicle() {
    }

    // Getters and setters
    public char getSymbol() { return symbol; }
    public int getFrontRow() { return frontRow; }
    public int getFrontCol() { return frontCol; }
    public int getBackRow() { return backRow; }
    public int getBackCol() { return backCol; }

    public void move(int dRow, int dCol) throws RushHourException {
        if (isHorizontal() && dRow != 0) {
            throw new RushHourException("Invalid move! Horizontal vehicles can only move left or right.");
        }
        if (isVertical() && dCol != 0) {
            throw new RushHourException("Invalid move! Vertical vehicles can only move up or down.");
        }
        
        frontRow += dRow;
        frontCol += dCol;
        backRow += dRow;
        backCol += dCol;
        notifyObservers();
    }

    private boolean isHorizontal() {
        return backRow == frontRow;
    }

    private boolean isVertical() {
        return backCol == frontCol;
    }

    // Observer pattern methods
    public void addObserver(RushHourObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(RushHourObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (RushHourObserver observer : observers) {
            observer.vehicleMoved(this);
        }
    }

    public void setObservers(List<RushHourObserver> observers) {
        this.observers = observers;
    }

    private static class RushHourException extends Exception {

        public RushHourException(String invalid_move_Horizontal_vehicles_can_only) {
        }
    }
}

// Observer interface for notifying the GUI of changes
interface RushHourObserver {
    void vehicleMoved(Vehicle vehicle);
}
