package au.edu.sydney.soft3202.task3.model;

/**
 * This represents a Player, used to govern who owns certain pieces and whose turn it is to play
 */
public enum Player {
    WHITE, BLACK;

    public Player getOpposite() {
        if (this == WHITE) return BLACK;
        return WHITE;
    }

    @Override
    public String toString() {
        if (this == WHITE) return "White";
        return "Black";
    }
}
