package com.example;


public class Move {
    private char symbol;
    private Direction direction;

    public Move(char symbol, Direction direction) {
        this.symbol = symbol;
        this.direction = direction;
    }

    public char getSymbol() {
        return symbol;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return symbol + " " + direction;
    }
}
