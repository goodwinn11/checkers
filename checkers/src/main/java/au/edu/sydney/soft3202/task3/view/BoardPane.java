package au.edu.sydney.soft3202.task3.view;

import au.edu.sydney.soft3202.task3.model.*;
import javafx.geometry.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * This is the main subcomponent of the view, and represents the image of the game board with all the pieces.
 *
 * Note the responsibilities it has, and those it leaves to the model:
 *  - it handles visual representations of state, meaning there are no image files in the model
 *  - it handles user interaction, so mouse hovering and clicking (along with the GameWindow for
 *    buttons/menu/keyboard shortcuts
 *
 *  - it does not embed any knowledge it can get from the model instead: size, movement rules, whose turn is next,
 *    when somebody has won. It instead asks the model for that information whenever it needs it
 */
public class BoardPane {
    private final StackPane pane;
    private final TileView[][] tiles;
    private final GameBoard model;
    
    private TileView pendingMoveOrigin = null;

    public BoardPane(GameBoard model) {
        this.model = model;
        this.tiles = new TileView[model.getWidth()][model.getHeight()];
        GridPane tilesPane = new GridPane();
        Label boardLabel = getBoardLabel();
        this.pane = new StackPane(boardLabel, tilesPane);

        for (int x = 0; x < model.getWidth(); x++) {
            ColumnConstraints constraints = new ColumnConstraints(62);
            constraints.setHalignment(HPos.CENTER);
            tilesPane.getColumnConstraints().add(constraints);
        }
        for (int y = 0; y < model.getHeight(); y++) {
            RowConstraints constraints = new RowConstraints(62);
            constraints.setValignment(VPos.CENTER);
            tilesPane.getRowConstraints().add(constraints);
        }

        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                Point point = new Point(x, y);
                TileView tile = getTile(point);
                tiles[x][y] = tile;
                tilesPane.add(tiles[x][y].label(), x, y);
            }
        }

        pane.setAlignment(Pos.TOP_LEFT);

        updateBoard();
    }

    public Pane getPane() {
        return pane;
    }

    private TileView getTile(Point point) {
        // Using the TileView record here means we can refer to the different components
        // as needed - using the knowledge of the Point state to correctly modify the ImageView sprite
        // and Label shading. The alternative would be very messy mouse coordinate checking.

        Image img = new Image("checkers.png");
        ImageView view = new ImageView(img);
        view.setFitHeight(48);
        view.setPreserveRatio(true);

        Label label = new Label();
        label.setGraphic(view);

        TileView result = new TileView(label, view, point);

        label.setOnMouseEntered((event) -> checkHighlight(result));
        label.setOnMouseExited((event) -> endHighlight(result));
        label.setOnMouseClicked((event) -> handleClick(result));

        return result;
    }

    private void handleClick(TileView tile) {
        if (null == pendingMoveOrigin) {
            if (model.getValidMoves(tile.point()).size() > 0) {
                tile.label().setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
                pendingMoveOrigin = tile;
            }
        } else {
            if (pendingMoveOrigin == tile) {
                pendingMoveOrigin = null;
                tile.label().setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                Move potentialMove = new Move(pendingMoveOrigin.point(), tile.point());
                if (model.getValidMoves(pendingMoveOrigin.point()).contains(potentialMove)) {
                    tile.label().setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
                    pendingMoveOrigin.label().setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
                    pendingMoveOrigin = null;
                    model.move(potentialMove);
                    updateBoard();
                }
            }
        }
    }

    private void endHighlight(TileView tile) {
        if (pendingMoveOrigin != tile) {
            tile.label().setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
        }
    }

    private void checkHighlight(TileView tile) {
        if (null == pendingMoveOrigin) {
            if (model.getValidMoves(tile.point()).size() > 0) {
                tile.label().setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        } else {
            Move potentialMove = new Move(pendingMoveOrigin.point(), tile.point());
            if (model.getValidMoves(pendingMoveOrigin.point()).contains(potentialMove)) {
                tile.label().setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
    }

    private Rectangle2D getTileSpriteViewport(TileState state) {
        // Using a sprite based image allows for some very efficient graphics - because images never need to
        // be loaded from disk after the first time, and image data can be cached. We don't leverage much of that
        // in this application as its performance needs are near-zero (e.g. we have an Image for every tile, where
        // we actually only need one, but it's a very interesting technique all the same.

        return switch (state) {
            case EMPTY -> new Rectangle2D(0, 0, 1, 1);
            case BLACK_KING -> new Rectangle2D(0,0, 32, 32);
            case WHITE_KING -> new Rectangle2D(32,0, 32, 32);
            case BLACK -> new Rectangle2D(64,0, 32, 32);
            case WHITE -> new Rectangle2D(96,0, 32, 32);
        };
    }

    private Label getBoardLabel() {
        Label label = new Label();
        Image img = new Image("chessboard.png");
        ImageView view = new ImageView(img);
        label.setGraphic(view);

        return label;
    }

    public void updateBoard() {
        // Notice when this gets called - it is tied to every update action on the Model, both here and in the
        // owning GameWindow.

        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                Point point = new Point(x, y);
                TileView tile = tiles[x][y];
                tile.imageView().setViewport(getTileSpriteViewport(model.getTileState(point)));
                tile.label().setBackground(new Background(new BackgroundFill(null, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }

        pendingMoveOrigin = null;

        Player winner = model.getWinState();
        if (null != winner) announceWinner();
    }

    private void announceWinner() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Winner!");
        alert.setHeaderText(model.getWinState() + " just won the game!");

        alert.showAndWait();
    }
}
