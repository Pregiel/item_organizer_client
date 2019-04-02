package item_organizer_client;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

public class Main extends Application {
    private Preferences preferences;

    @Override
    public void start(Stage primaryStage) throws Exception {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        Parent root = FXMLLoader.load(getClass().getResource("/layout/MainLayout.fxml"));
        primaryStage.setTitle("Item organizer");
        primaryStage.setScene(new Scene(
                root,
                preferences.getDouble("width", 800),
                preferences.getDouble("height", 500)));

        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(300);
        primaryStage.show();

        primaryStage.widthProperty().addListener(((observable, oldValue, newValue) -> {
            preferences.putDouble("width", (Double) newValue);
        }));

        primaryStage.heightProperty().addListener(((observable, oldValue, newValue) -> {
            preferences.putDouble("height", (Double) newValue);
        }));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
