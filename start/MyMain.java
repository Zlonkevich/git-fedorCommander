package start;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Fedor on 26.10.2016.
 */
public class MyMain extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent myParent = FXMLLoader.load(getClass().getResource("../fxml/commander.fxml"));
        primaryStage.setTitle("Fedor Commander");
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);
        primaryStage.setScene(new Scene(myParent));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
