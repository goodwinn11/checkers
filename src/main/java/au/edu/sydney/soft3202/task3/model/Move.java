package au.edu.sydney.soft3202.task3.model;

/**
 * This is a Record object representing a Move, which is an intended movement from an origin point to a target point
 * It embeds no knowledge of the board limits or move rules itself, and so move legality must be handled externally.
 *
 * @param origin The starting point
 * @param target The intended finishing point
 */
public record Move(Point origin, Point target) {
    public boolean isJump() {
        return Math.abs(origin.x() - target.x()) == 2 ||
                Math.abs(origin.y() - target.y()) == 2;
    }

    public Point getIntermediate() {
        if (!isJump()) throw new IllegalStateException("Not a jump move");

        int xDiff = target.x() - origin.x();
        int yDiff = target.y() - origin.y();

        return new Point(origin.x() + xDiff/2, origin.y() + yDiff/2);
    }
}
