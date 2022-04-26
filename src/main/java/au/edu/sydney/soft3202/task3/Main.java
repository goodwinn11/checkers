package au.edu.sydney.soft3202.task3;

import au.edu.sydney.soft3202.task3.model.GameBoard;
import au.edu.sydney.soft3202.task3.model.SQLDataBase;
import au.edu.sydney.soft3202.task3.view.GameWindow;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    private final GameBoard model = new GameBoard();
    private final GameWindow view = new GameWindow(model);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(view.getScene());
        primaryStage.setTitle("Checkers");
        primaryStage.show();
    }
}
