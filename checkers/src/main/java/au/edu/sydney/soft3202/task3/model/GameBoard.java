package au.edu.sydney.soft3202.task3.model;

import java.util.*;

/**
 * This is the core logic engine for the Model, storing both the state of the game, and all the rules of the game
 * It should definitely be decomposed into smaller elements, but is retained here as an overly complex class to make
 * the borders and appropriate responsibilities between the View and the Model more obvious.
 */
public class GameBoard {
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;

    private TileState[][] tiles;
    private Player currentTurn = null;
    private boolean continuing = false;

    // This is not the View leaking in to the Model. It is a way for the View to get inside the Model, but the Model
    // doesn't know or care what sort of observer it is, just that it can be notified. This is likely the best way for
    // the View to find out if the Model has changed
    private final Set<GameBoardObserver> observers;

    public GameBoard() {
        tiles = new TileState[WIDTH][HEIGHT];
        observers = new HashSet<>();

        initialise();

    }

    private void initialise() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                tiles[x][y] = TileState.EMPTY;
            }
        }
    }

    public void newGame() {
        int[][] starter_map = {
                {0,1,0,0,0,2,0,2},
                {1,0,1,0,0,0,2,0},
                {0,1,0,0,0,2,0,2},
                {1,0,1,0,0,0,2,0},
                {0,1,0,0,0,2,0,2},
                {1,0,1,0,0,0,2,0},
                {0,1,0,0,0,2,0,2},
                {1,0,1,0,0,0,2,0},
        };

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                switch (starter_map[x][y]) {
                    case 0 -> tiles[x][y] = TileState.EMPTY;
                    case 1 -> tiles[x][y] = TileState.BLACK;
                    case 2 -> tiles[x][y] = TileState.WHITE;
                }
            }
        }

        Random rand = new Random();
        int nextPlayer = rand.nextInt(2);
        if (nextPlayer == 0) {
            currentTurn = Player.BLACK;
        } else {
            currentTurn = Player.WHITE;
        }

        updateObservers();
    }

    public int getWidth() {
        return tiles.length;
    }

    public int getHeight() {
        return tiles[0].length;
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public TileState getTileState(Point point) {
        if (!withinDimensions(point)) throw new IllegalArgumentException("Point out of board: " + point);
        return tiles[point.x()][point.y()];
    }

    public List<Move> getValidMoves(Point origin) {
        if (!withinDimensions(origin)) throw new IllegalArgumentException("Point out of board");
        TileState tile = getTileState(origin);
        List<Move> results = new ArrayList<>();

        Player owner = getOwner(tile);
        if (owner != currentTurn) return results;


        if (tile == TileState.EMPTY) return results;

        List<Move> takeMoves = anyCanTake(owner);

        if (takeMoves.size() > 0) {
            for (Move move: takeMoves) {
                if (move.origin().equals(origin)) {
                    results.add(move);
                }
            }

            return results;
        }

        // No take moves available - just single diagonals possible

        List<Direction> directions = getValidDirections(owner, tile);

        for (Direction direction: directions) {
            Point target = origin.getPoint(direction);
            if (!withinDimensions(target)) continue;
            TileState targetState = getTileState(target);
            if (targetState == TileState.EMPTY) results.add(new Move(origin, target));
        }

        return results;
    }

    private List<Direction> getValidDirections(Player owner, TileState tile) {
        Direction forward_right;
        Direction forward_left;
        Direction backward_right;
        Direction backward_left;

        if (owner == Player.WHITE) {
            forward_right = Direction.UP_RIGHT;
            forward_left = Direction.UP_LEFT;
            backward_right = Direction.DOWN_RIGHT;
            backward_left = Direction.DOWN_LEFT;
        } else {
            forward_right = Direction.DOWN_RIGHT;
            forward_left = Direction.DOWN_LEFT;
            backward_right = Direction.UP_RIGHT;
            backward_left = Direction.UP_LEFT;
        }

        List<Direction> directions = new ArrayList<>();
        directions.add(forward_right);
        directions.add(forward_left);
        if (isKing(tile)) {
            directions.add(backward_right);
            directions.add(backward_left);
        }
        return directions;
    }

    public void move(Move move) {
        boolean tookAPiece = false;

        Player owner = getOwner(getTileState(move.origin()));

        if (owner != currentTurn) throw new IllegalStateException("Wrong player's tile");

        if (!getValidMoves(move.origin()).contains(move)) throw new IllegalStateException("Illegal move");

        if (move.isJump()) {
            Point taken = move.getIntermediate();
            tiles[taken.x()][taken.y()] = TileState.EMPTY;
            tookAPiece = true;
        }

        tiles[move.target().x()][move.target().y()] = tiles[move.origin().x()][move.origin().y()];
        tiles[move.origin().x()][move.origin().y()] = TileState.EMPTY;
        if (owner == Player.WHITE && move.target().y() == 0) {
            tiles[move.target().x()][move.target().y()] = TileState.WHITE_KING;
        }
        if (owner == Player.BLACK && move.target().y() == 7) {
            tiles[move.target().x()][move.target().y()] = TileState.BLACK_KING;
        }

        if (tookAPiece && canTake(move.target()).size() > 0) { // took one and can take another - multi-jump
            continuing = true;
        } else {
            currentTurn = currentTurn.getOpposite();
            continuing = false;
        }

        updateObservers();
    }

    private Player getOwner(TileState tile) {
        return switch (tile) {
            case EMPTY -> null;
            case WHITE, WHITE_KING -> Player.WHITE;
            case BLACK, BLACK_KING -> Player.BLACK;
        };
    }

    private boolean isKing(TileState tile) {
        return switch (tile) {
            case WHITE_KING, BLACK_KING -> true;
            case WHITE, BLACK, EMPTY -> false;
        };
    }

    private boolean withinDimensions(Point point) {
        return point.x() >= 0 &&
                point.x() < tiles.length &&
                point.y() >= 0 &&
                point.y() < tiles[0].length;
    }

    private List<Move> canTake(Point origin) {
        TileState originState = getTileState(origin);
        Player owner = getOwner(originState);
        List<Move> results = new ArrayList<>();
        if (null == owner || owner != currentTurn) return results;

        Player opponent = owner.getOpposite();

        List<Direction> directions = getValidDirections(owner, originState);

        for (Direction direction: directions) {
            Point intermediate = origin.getPoint(direction);
            Point jump = origin.getJumpPoint(direction);

            if (withinDimensions(intermediate) && withinDimensions(jump) &&
                    opponent == getOwner(getTileState(intermediate)) &&
                    getTileState(jump) == TileState.EMPTY) {
                results.add(new Move(origin, jump));
            }
        }

        return results;
    }

    public boolean mustTake() {
        if (null == currentTurn) return false;

        return anyCanTake(currentTurn).size() > 0;
    }

    private List<Move> anyCanTake(Player player) {
        List<Move> results = new ArrayList<>();

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (player == getOwner(tiles[x][y]))
                results.addAll(canTake(new Point(x, y)));
            }
        }

        return results;
    }

    public boolean isContinuing() {
        return continuing;
    }

    public void registerObserver(GameBoardObserver gameBoardObserver) {
        this.observers.add(gameBoardObserver);
    }

    private void updateObservers() {
        // Notice that this does not carry state with it - the Model doesn't know what the View needs and shouldn't.
        // It just says 'I changed' and it is the View's responsibility to ask for the state it needs.
        for (GameBoardObserver observer: observers) {
            observer.update();
        }
    }

    public Player getWinState() {
        List<Move> results = new ArrayList<>();

        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[0].length; y++) {
                if (currentTurn == getOwner(tiles[x][y]))
                    results.addAll(getValidMoves(new Point(x, y)));
            }
        }

        if (results.size() > 0) return null;

        if (null == currentTurn) return null;

        return currentTurn.getOpposite();
    }

    public String serialise() {
        StringBuilder sb = new StringBuilder();

        sb.append(currentTurn)
                .append("|")
                .append(continuing)
                .append("|");

        for (int x = 0; x < WIDTH; x++) {
            if (x != 0) sb.append(";");
            for (int y = 0; y < HEIGHT; y++) {
                if (y != 0) sb.append(",");
                switch (tiles[x][y]) {
                    case EMPTY -> sb.append(".");
                    case WHITE -> sb.append("w");
                    case WHITE_KING -> sb.append("W");
                    case BLACK -> sb.append("b");
                    case BLACK_KING -> sb.append("B");
                }
            }
        }

        return sb.toString();
    }

    public void deserialise(String state) {
        TileState[][] newTiles = new TileState[WIDTH][HEIGHT];
        Player newCurrentTurn;
        boolean newContinuing;

        String[] tokens = state.split("\\|");
        if (tokens.length != 3) throw new IllegalArgumentException("Invalid serialisation string");

        switch(tokens[0]) {
            case "Black" -> newCurrentTurn = Player.BLACK;
            case "White" -> newCurrentTurn = Player.WHITE;
            default -> throw new IllegalArgumentException("Invalid serialisation string");
        }
        switch (tokens[1]) {
            case "true" -> newContinuing = true;
            case "false" -> newContinuing = false;
            default -> throw new IllegalArgumentException("Invalid serialisation string");
        }
        String[] cols = tokens[2].split(";");
        if (cols.length != WIDTH) throw new IllegalArgumentException("Invalid serialisation string");

        int x = 0;
        for (String col: cols) {
            String[] rows = col.split(",");
            if (rows.length != HEIGHT) throw new IllegalArgumentException("Invalid serialisation string");

            int y = 0;

            for (String cell: rows) {
                switch (cell) {
                    case "." -> newTiles[x][y] = TileState.EMPTY;
                    case "w" -> newTiles[x][y] = TileState.WHITE;
                    case "W" -> newTiles[x][y] = TileState.WHITE_KING;
                    case "b" -> newTiles[x][y] = TileState.BLACK;
                    case "B" -> newTiles[x][y] = TileState.BLACK_KING;
                    default -> throw new IllegalArgumentException("Invalid serialisation string");
                }
                y++;
            }
            x++;
        }

        this.tiles = newTiles;
        this.continuing = newContinuing;
        this.currentTurn = newCurrentTurn;

        // This is only useful if you call it any time your Model state changes
        // More sophisticated applications will instead mutate state through dedicated mutator/setter methods, even
        // internally, and update observers in there, so you can be more certain it has been remembered.
        updateObservers();
    }
}
