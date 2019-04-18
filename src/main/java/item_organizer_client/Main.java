package item_organizer_client;


import item_organizer_client.utils.SpringFXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main extends Application {
    private ItemOrganizer itemOrganizer;

    private static String[] savedArgs;

    @Override
    public void init() throws Exception {
        SpringFXMLLoader.setApplicationContext(SpringApplication.run(Main.class), this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        itemOrganizer = new ItemOrganizer(stage);
    }

    public static void main(String[] args) {
        savedArgs = args;
        Application.launch(Main.class, args);
    }
}
