

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.Collection;
import javafx.application.Application;
import javafx.stage.Stage;

public class RushHourGUI extends Application implements RushHourObserver {

    private RushHour game;
    private GridPane boardPane;
    private Label statusLabel;
    private Label movesLabel;
    private Vehicle selectedVehicle;
    private Move hintMove;

    @Override
    public void start(Stage stage) {
        try {
            game = new RushHour("data/03_01.csv");
            game.registerObserver(this);
        } catch (Exception e) {
            showError("Couldn't load game board");
            return;
        }

        boardPane = createBoard();
        statusLabel = new Label("Game started!");
        movesLabel = new Label("Moves: 0");

        Button resetButton = new Button("Reset");
        Button hintButton = new Button("Hint");
        Button quitButton = new Button("Quit");

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(
            new HBox(10, movesLabel, statusLabel),
            boardPane,
            new HBox(10, resetButton, hintButton, quitButton)
        );

        resetButton.setOnAction(e -> resetGame());
        hintButton.setOnAction(e -> showHint());
        quitButton.setOnAction(e -> Platform.exit());

        Scene scene = new Scene(root);
        stage.setTitle("Rush Hour");
        stage.setScene(scene);
        stage.show();

        updateBoardDisplay();
    }

    private GridPane createBoard() {
        GridPane grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(5));

        for (int row = 0; row < RushHour.BOARD_DIM; row++) {
            for (int col = 0; col < RushHour.BOARD_DIM; col++) {
                Rectangle cell = new Rectangle(50, 50);
                cell.setFill(Color.LIGHTGRAY);
                cell.setStroke(Color.BLACK);
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    private void updateBoardDisplay() {
        Platform.runLater(() -> {
            boardPane.getChildren().removeIf(node -> node instanceof Label);

            for (Vehicle vehicle : game.getVehicles()) {
                Color color = vehicle.getSymbol() == RushHour.RED_SYMBOL
                        ? Color.RED : Color.CORNFLOWERBLUE;

                if (vehicle.isHorizontal()) {
                    for (int col = vehicle.getBack().getCol(); col <= vehicle.getFront().getCol(); col++) {
                        addVehicleToCell(vehicle, color, vehicle.getBack().getRow(), col);
                    }
                } else {
                    for (int row = vehicle.getBack().getRow(); row <= vehicle.getFront().getRow(); row++) {
                        addVehicleToCell(vehicle, color, row, vehicle.getBack().getCol());
                    }
                }
            }

            movesLabel.setText("Moves: " + game.getMoveCount());
        });
    }

    private void addVehicleToCell(Vehicle vehicle, Color color, int row, int col) {
        Label cell = new Label(String.valueOf(vehicle.getSymbol()));
        cell.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY)));
        cell.setMinSize(50, 50);
        cell.setAlignment(javafx.geometry.Pos.CENTER);

        cell.setOnMouseClicked(e -> {
            selectedVehicle = vehicle;
            showMoveButtons(row, col);
        });

        boardPane.add(cell, col, row);
    }

    private void showMoveButtons(int row, int col) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Move Vehicle");
        alert.setHeaderText("Select direction for " + selectedVehicle.getSymbol());

        ButtonType left = new ButtonType("Left");
        ButtonType right = new ButtonType("Right");
        ButtonType up = new ButtonType("Up");
        ButtonType down = new ButtonType("Down");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(left, right, up, down, cancel);

        alert.showAndWait().ifPresent(buttonType -> {
            try {
                Direction dir = null;
                if (buttonType == left) dir = Direction.LEFT;
                else if (buttonType == right) dir = Direction.RIGHT;
                else if (buttonType == up) dir = Direction.UP;
                else if (buttonType == down) dir = Direction.DOWN;

                if (dir != null) {
                    game.moveVehicle(new Move(selectedVehicle.getSymbol(), dir));
                }
            } catch (RushHourException ex) {
                statusLabel.setText("Invalid move: " + ex.getMessage());
            }
        });
    }

    @Override
    public void vehicleMoved(Vehicle vehicle) {
        updateBoardDisplay();
        if (game.isGameOver()) {
            statusLabel.setText("🎉 Congratulations! You won!");
        }
    }

    private void resetGame() {
        try {
            game = new RushHour("data/03_01.csv");
            game.registerObserver(this);
            updateBoardDisplay();
            statusLabel.setText("Game reset!");
        } catch (Exception e) {
            showError("Failed to reset game");
        }
    }

    private void showHint() {
        Collection<Move> moves = game.getPossibleMoves();
        if (!moves.isEmpty()) {
            hintMove = moves.iterator().next();
            statusLabel.setText("Hint: Move " + hintMove.getSymbol() + " " + hintMove.getDirection());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    
    public class RushHourLauncher extends Application {
    @Override
    public void start(Stage primaryStage) {
        new RushHourGUI(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
}






