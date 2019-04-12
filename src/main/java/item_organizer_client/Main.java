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
    private ItemOrganizer itemOrganizer;

    @Override
    public void init() throws Exception {
        SpringFXMLLoader.setApplicationContext(SpringApplication.run(Main.class));
    }

    @Override
    public void start(Stage stage) throws Exception {
        itemOrganizer = new ItemOrganizer(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
