package au.edu.sydney.soft3202.task3.model;

/**
 * This class represents the directions a piece can move along the board, allowing for more easily understandable maths
 *
 * The mod values are used to modify the position of a Point
 */
public enum Direction {
    UP_LEFT(-1, -1), UP_RIGHT(1, -1), DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1);

    private final int xMod;
    private final int yMod;

    Direction(int xMod, int yMod) {

        this.xMod = xMod;
        this.yMod = yMod;
    }

    public int getYMod() {
        return yMod;
    }

    public int getXMod() {
        return xMod;
    }
}
