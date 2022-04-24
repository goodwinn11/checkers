package au.edu.sydney.soft3202.task3.model;

/**
 * This record represents an x and y coordinate in 2d space. These points are discrete.
 *
 * @param x The horizontal position
 * @param y The vertical position
 */
public record Point(int x, int y) {
    public Point getPoint(Direction direction) {
        return new Point(x + direction.getXMod(), y + direction.getYMod());
    }

    public Point getJumpPoint(Direction direction) {
        return new Point(x + (2 * direction.getXMod()), y + (2 * direction.getYMod()));
    }
}
