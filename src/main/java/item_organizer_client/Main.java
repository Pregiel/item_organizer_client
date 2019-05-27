package item_organizer_client;


import item_organizer_client.utils.SpringFXMLLoader;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class Main extends Application {
    private static final DateTimeFormatter logDateTime = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

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

        LocalDateTime now = LocalDateTime.now();
        String logName = "logs/log_" + now.format(logDateTime) + ".txt";

        new File("logs").mkdirs();
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(logName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.setOut(out);
        System.setErr(out);

        Application.launch(Main.class, args);
    }
}
