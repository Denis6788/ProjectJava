package com.example;

import java.util.Collection;
import java.util.Scanner;

public class RushHourCLI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RushHour game = null;
        String filename = null;

        while (game == null) {
            System.out.print("Enter board filename (e.g. rush_hour_board.csv): ");
            filename = scanner.nextLine().trim();
            try {
                game = new RushHour("data/" + filename);
            } catch (Exception e) {
                System.out.println("Could not load board: " + e.getMessage());
            }
        }

        System.out.println("Welcome to Rush Hour! Type 'help' for available commands.");

        while (true) {
            System.out.println("\n" + game);
            System.out.println("Moves made: " + game.getMoveCount());

            System.out.print("Enter command: ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (parts.length == 0 || parts[0].isEmpty()) {
                continue;
            }

            String command = parts[0].toLowerCase();

            switch (command) {
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("  <symbol> <direction>  - move a vehicle (e.g., R RIGHT)");
                    System.out.println("  hint                  - suggest a possible move");
                    System.out.println("  solve                 - automatically solve the puzzle");
                    System.out.println("  reset                 - restart the game");
                    System.out.println("  quit                  - exit the game");
                    break;

                case "hint":
                    if (game.isGameOver()) {
                        System.out.println("Game is over. Use 'reset' to play again.");
                    } else {
                        Collection<Move> moves = game.getPossibleMoves();
                        if (moves.isEmpty()) {
                            System.out.println("No valid moves available.");
                        } else {
                            System.out.println("Hint: Try " + moves.iterator().next());
                        }
                    }
                    break;

                case "solve":
                    if (game.isGameOver()) {
                        System.out.println("Game is already solved!");
                    } else {
                        RushHourConfig solution = RushHourConfig.solve(game);
                        if (solution == null) {
                            System.out.println("No solution found.");
                        } else {
                            System.out.println("Solution found:");
                            for (String move : solution.getMoves()) {
                                System.out.println("Move: " + move);
                            }
                            System.out.println("\nFinal board:");
                            System.out.println(solution);
                        }
                    }
                    break;

                case "reset":
                    try {
                        game = new RushHour("data/" + filename);
                        System.out.println("Game reset.");
                    } catch (Exception e) {
                        System.out.println("Failed to reset game: " + e.getMessage());
                    }
                    break;

                case "quit":
                    System.out.println("Exiting Rush Hour. Goodbye!");
                    scanner.close();
                    return;

                default:
                    if (parts.length == 2) {
                        if (game.isGameOver()) {
                            System.out.println("Game is already complete. Use 'reset' to play again.");
                        } else {
                            try {
                                char symbol = parts[0].charAt(0);
                                Direction dir = Direction.valueOf(parts[1].toUpperCase());
                                Move move = new Move(symbol, dir);
                                game.moveVehicle(move);
                            } catch (IllegalArgumentException e) {
                                System.out.println("Invalid direction. Use UP, DOWN, LEFT, or RIGHT.");
                            } catch (RushHourException e) {
                                System.out.println("Invalid move: " + e.getMessage());
                            }
                        }
                    } else {
                        System.out.println("Unknown command. Type 'help' for the command list.");
                    }
                    break;
            }
        }
    }
}
