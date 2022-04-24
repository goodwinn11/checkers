package au.edu.sydney.soft3202.task3.view;

import au.edu.sydney.soft3202.task3.model.GameBoard;
import au.edu.sydney.soft3202.task3.model.GameBoardObserver;
import org.controlsfx.control.StatusBar;

/**
 * This is the status bar at the bottom of the window. It doesn't have direct knowledge of when mutable actions
 * are triggered, and so uses the observer pattern to know when to update itself from the model.
 */
public class StatusBarPane implements GameBoardObserver {
    private final StatusBar statusBar;
    private final GameBoard model;

    public StatusBarPane(GameBoard model) {
        this.model = model;
        this.statusBar = new StatusBar();
        model.registerObserver(this);
        update();
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    @Override
    public void update() {
        StringBuilder sb = new StringBuilder("Current Player: ");
        sb.append(model.getCurrentTurn());
        sb.append(" | ");

        if (model.mustTake()) sb.append("Must Take | ");
        if (model.isContinuing()) sb.append("Double Jump!");
        statusBar.setText(sb.toString());
    }
}
