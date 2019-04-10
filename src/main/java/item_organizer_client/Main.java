package item_organizer_client;


import item_organizer_client.utils.SpringFXMLLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.prefs.Preferences;

@SpringBootApplication
public class Main extends Application {
    private Preferences preferences;
    private Parent root;

    @Override
    public void init() throws Exception {
        SpringFXMLLoader.setApplicationContext(SpringApplication.run(Main.class));
        root = new SpringFXMLLoader(getClass().getResource("/layout/MainLayout.fxml")).load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        primaryStage.setTitle("Item organizer");
        primaryStage.setScene(new Scene(
                root,
                preferences.getDouble("width", ((Pane) root).getPrefWidth()),
                preferences.getDouble("height", ((Pane) root).getPrefHeight())));

        primaryStage.setMinWidth(((Pane) root).getMinWidth());
        primaryStage.setMinHeight(((Pane) root).getMinHeight());
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
