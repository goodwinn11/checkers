package au.edu.sydney.soft3202.task3.model;

/**
 * This is part of the observer pattern, and allows indirect notification of changes in the GameBoard to View objects
 * that don't know it might have changed.
 */
public interface GameBoardObserver {
    void update();
}
