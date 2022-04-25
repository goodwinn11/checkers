package au.edu.sydney.soft3202.task3.view;

import au.edu.sydney.soft3202.task3.model.Point;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * This is a Record that is essentially just a way of keeping multiple references together so they can be
 * mutated based on shared state.
 *
 * @param label The surrounding label for the tile - used for selection colouring
 * @param imageView The image of the piece, if any - uses sprite techniques on a multiple sprite image file
 * @param point The point on the board where this tile is located
 */
public record TileView(Label label, ImageView imageView, Point point) {
}
