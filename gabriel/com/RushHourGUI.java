package gabriel.com;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// implementing Rush Hour GUI using JavaFX
public class RushHourGUI extends Application {
    private static final int TILE_SIZE = 80;
    private GridPane grid;
    private Label statusLabel;
    private Label moveLabel;
    private Button hintButton;
    private Button resetButton;
    private RushHour game;
    private final String filename = "06_01.csv";
    private Map<Character, List<Button>> vehicleButtons = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    // JavaFX application entry point
    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        grid = new GridPane();
        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #444444;");

        HBox bottomBar = new HBox(20);
        bottomBar.setAlignment(Pos.CENTER);
        statusLabel = new Label("New Game");
        statusLabel.setTextFill(Color.GREEN);
        moveLabel = new Label("Moves: 0");
        hintButton = new Button("Hint");
        resetButton = new Button("Reset");

        hintButton.setOnAction(e -> showHint());
        resetButton.setOnAction(e -> resetGame());

        bottomBar.getChildren().addAll(statusLabel, moveLabel, hintButton, resetButton);
        root.setCenter(grid);
        root.setBottom(bottomBar);

        loadGame();
        // Load the game from the file
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setTitle("Rush Hour");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadGame() {
        try {
            game = new RushHour(filename);
            drawBoard();
        } catch (IOException e) {
            statusLabel.setText("Failed to load: " + filename);
            statusLabel.setTextFill(Color.RED);
            e.printStackTrace(); // Showing with details the error that i am getting
        }
    }
    
    // Reset the game
    private void resetGame() {
        loadGame();
        statusLabel.setText("New Game");
        statusLabel.setTextFill(Color.GREEN);
        moveLabel.setText("Moves: 0");
        hintButton.setDisable(false);
    }
    // drawing the board 
    private void drawBoard() {
        grid.getChildren().clear();
        vehicleButtons.clear();

        String[] lines = game.toString().split("\\R");
        char[][] boardArray = new char[lines.length][];
        for (int i = 0; i < lines.length; i++) {
            boardArray[i] = lines[i].toCharArray();
        }

        Map<Character, Boolean> drawn = new HashMap<>();
        // Loop through the board array and alsoo creating the UI elements
        for (int row = 0; row < RushHour.BOARD_DIM; row++) {
            for (int col = 0; col < RushHour.BOARD_DIM; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(TILE_SIZE, TILE_SIZE);

                Rectangle bg = new Rectangle(TILE_SIZE, TILE_SIZE);
                bg.setFill(Color.DARKGRAY);
                bg.setStroke(Color.BLACK);
                cell.getChildren().add(bg);

                char symbol = boardArray[row][col];
                if (symbol != RushHour.EMPTY_SYMBOL && !drawn.containsKey(symbol)) {
                    VBox box = new VBox(4);
                    box.setAlignment(Pos.CENTER);

                    Label label = new Label(String.valueOf(symbol));
                    label.setFont(new Font(16));
                    label.setTextFill(Color.WHITE);

                    Rectangle block = new Rectangle(TILE_SIZE - 10, TILE_SIZE - 10);
                    block.setFill(Color.web(getColor(symbol)));
                    block.setArcWidth(20);
                    block.setArcHeight(20);

                    StackPane vehiclePane = new StackPane(block, label);
                    box.getChildren().add(vehiclePane);

                    Direction[] dirs = getAllowedDirections(symbol);
                    List<Button> moveBtns = new ArrayList<>();

                    for (Direction dir : dirs) {
                        Button btn = new Button(getArrow(dir));
                        btn.setOnAction(e -> handleMove(symbol, dir));
                        moveBtns.add(btn);
                        box.getChildren().add(btn);
                    }

                    vehicleButtons.put(symbol, moveBtns);
                    cell.getChildren().add(box);
                    drawn.put(symbol, true);
                }

                grid.add(cell, col, row);
            }
        }
    }
    // getting the allowed directions for the vehicle
    private Direction[] getAllowedDirections(char symbol) {
        Vehicle v = game.getVehicleBySymbol(symbol);
        if (v == null) return new Direction[0];
        if (v.getBack().getRow() == v.getFront().getRow()) {
            return new Direction[]{Direction.LEFT, Direction.RIGHT};
        } else {
            return new Direction[]{Direction.UP, Direction.DOWN};
        }
    }
    // handling the move of the vehicle
    private void handleMove(char symbol, Direction dir) {
        if (game.isGameOver()) return;

        try {
            game.moveVehicle(new Move(symbol, dir));
            drawBoard();
            moveLabel.setText("Moves: " + game.getMoveCount());
            statusLabel.setText("Valid move.");
            statusLabel.setTextFill(Color.BLACK);

            if (game.isGameOver()) {
                statusLabel.setText("🎉 Congratulations!");
                statusLabel.setTextFill(Color.LIMEGREEN);
                hintButton.setDisable(true);
                disableAllMoveButtons();
            }
        } catch (RushHourException e) {
            statusLabel.setText("Invalid move: " + e.getMessage());
            statusLabel.setTextFill(Color.RED);
        }
    }
    // showing the hint for the vehicle
    private void showHint() {
        for (Move move : game.getPossibleMoves()) {
            char symbol = move.getSymbol();
            Direction dir = move.getDirection();
            statusLabel.setText("Hint: Try " + symbol + " " + dir);
            return;
        }
        statusLabel.setText("No hints available.");
    }
    // disabling all the move buttons
    private void disableAllMoveButtons() {
        for (List<Button> buttons : vehicleButtons.values()) {
            for (Button b : buttons) {
                b.setDisable(true);
            }
        }
    }
    // getting the arrow for the direction
    private String getArrow(Direction dir) {
        switch (dir) {
            case UP: return "↑";
            case DOWN: return "↓";
            case LEFT: return "←";
            case RIGHT: return "→";
            default: return "?";
        }
    }
    // getting the color for the vehicle
    private String getColor(char symbol) {
        switch (symbol) {
            case 'R': return "red";
            case 'O': return "orange";
            case 'Y': return "yellow";
            case 'G': return "green";
            case 'B': return "blue";
            default: return "lightgray";
        }
    }
}
