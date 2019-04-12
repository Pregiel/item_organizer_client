package item_organizer_client;

import item_organizer_client.utils.SpringFXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.prefs.Preferences;

public class ItemOrganizer {
    private final Scene scene;

    private final Parent root;

    private final Preferences preferences;

    public ItemOrganizer(Stage stage) throws IOException {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        root = new SpringFXMLLoader(getClass().getResource("/layout/MainLayout.fxml")).load();
        scene = new Scene(
                root,
                preferences.getDouble("width", ((Pane) root).getPrefWidth()),
                preferences.getDouble("height", ((Pane) root).getPrefHeight()));

        setUpStage(stage);
    }

    public Scene getScene() {
        return scene;
    }

    public void setUpStage(Stage stage) {
        stage.setTitle("Item organizer");
        stage.setScene(getScene());

        stage.setMinWidth(((Pane) root).getMinWidth());
        stage.setMinHeight(((Pane) root).getMinHeight());
        stage.show();

        stage.widthProperty().addListener(((observable, oldValue, newValue) -> {
            preferences.putDouble("width", (Double) newValue);
        }));

        stage.heightProperty().addListener(((observable, oldValue, newValue) -> {
            preferences.putDouble("height", (Double) newValue);
        }));
    }
}
