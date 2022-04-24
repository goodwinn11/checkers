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
        SQLDataBase chess = new SQLDataBase();
        chess.removeDB();
       chess.createDB();
       chess.setupDB();
       chess.addStartingData();
//        addDataFromQuestionableSource("2", "none", "1,2,3,4");
      //  chess.createDB();
    //    SQLDataBase.setupDB();
       // SQLDataBase.addStartingData();
        primaryStage.setTitle("Checkers");
        primaryStage.show();
    }
}
