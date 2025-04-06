//the code has some errors becuase i am not connecting it with the rest project code yet, when we connect it we will update and make it work together
package gabriel.com;

import java.util.Collection;
import java.util.Scanner;

// leaving out this import statements for later when we connect the code
// import rushhour.model.*;
// import rushhour.model.RushHour;
// import rushhour.model.Move;
// import rushhour.model.Direction;
// import rushhour.model.RushHourException;

public class RushHourCLI {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); 
        RushHour game = null; 
        String filename = null; 

        // Prompt for filename
        while (game == null) {
            System.out.print("Enter board filename: ");
            filename = scanner.nextLine().trim();
            try {
                game = new RushHour("data/" + filename); 
            } catch (Exception e) {
                System.out.println("Could not load board: " + e.getMessage()); 
            }
        }

        // Game loop
        while (true) {
            System.out.println(game.toString()); 
            System.out.println("Moves made: " + game.getMoveCount()); 

            System.out.print("Enter command (type 'help' for options): "); 
            String input = scanner.nextLine().trim(); 
            String[] parts = input.split("\\s+"); 

            if (parts.length == 0 || parts[0].isEmpty()) {
                continue; 
            }

            String command = parts[0].toLowerCase(); // getting main command
            // checking if the command is a valid vehicle move
            switch (command) {
                case "help":
                    System.out.println("Available commands:");
                    System.out.println("  <symbol> <direction> - move a vehicle (e.g., R RIGHT)");
                    System.out.println("  hint - show a possible move");
                    System.out.println("  reset - reset the game to start state");
                    System.out.println("  quit - exit the game");
                    break;
                // checking if the command is a hint
                case "hint":
                    if (game.isGameOver()) {
                        System.out.println("Game is over. Click 'reset' to play again.");
                    } else {
                        Collection<Move> moves = game.getPossibleMoves(); 
                        if (moves.isEmpty()) {
                            System.out.println("No valid moves available.");
                        } else {
                            System.out.println("Hint: Try " + moves.iterator().next()); 
                        }
                    }
                    break;
                    // checking if the command is a reset
                case "reset":
                    try {
                        game = new RushHour("data/" + filename); 
                        System.out.println("Game reset.");
                    } catch (Exception e) {
                        System.out.println("Failed to reset game: " + e.getMessage()); 
                    }
                    break;
                    // checking if the command is a quit
                case "quit":
                    scanner.close(); 
                    return; 
                   // checking if the command is a move 
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
