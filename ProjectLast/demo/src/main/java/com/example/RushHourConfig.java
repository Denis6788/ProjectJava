
package com.example;

import backtracker.Configuration;
import backtracker.Backtracker;

import java.util.*;

public class RushHourConfig implements Configuration<RushHour> {
    private RushHour game;
    private List<String> moves;

    public RushHourConfig(RushHour game) {
        this.game = new RushHour(game);  // Deep copy to avoid mutating input
        this.moves = new ArrayList<>();
    }

    private RushHourConfig(RushHour game, List<String> moves) {
        this.game = game;
        this.moves = new ArrayList<>(moves);
    }

    @Override
    public boolean isGoal() {
        return game.isSolved();
    }

    @Override
    public List<Configuration<RushHour>> getSuccessors() {
        List<Configuration<RushHour>> successors = new ArrayList<>();
        for (String move : game.getPossibleMoves()) {
            RushHour copy = new RushHour(game);
            copy.applyMove(move);
            List<String> newMoves = new ArrayList<>(moves);
            newMoves.add(move);
            successors.add(new RushHourConfig(copy, newMoves));
        }
        return successors;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public String toString() {
        return "Moves: " + String.join(", ", moves) + "\n" + game.toString();
    }


    public List<String> getMoves() {
        return moves;
    }

    
    public static RushHourConfig solve(RushHour start) {
        RushHourConfig startConfig = new RushHourConfig(start);
        Backtracker<RushHour> backtracker = new Backtracker<>(true);
        Optional<Configuration<RushHour>> solution = backtracker.solve(startConfig);

        return solution.map(config -> (RushHourConfig) config).orElse(null);
    }
}
