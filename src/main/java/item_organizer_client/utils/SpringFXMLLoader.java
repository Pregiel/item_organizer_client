package item_organizer_client.utils;

import item_organizer_client.Main;
import javafx.fxml.FXMLLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SpringFXMLLoader extends FXMLLoader {
    private static ConfigurableApplicationContext applicationContext;

    public static void setApplicationContext(ConfigurableApplicationContext applicationContext) {
        SpringFXMLLoader.applicationContext = applicationContext;
    }

    public SpringFXMLLoader(URL url) {
        super(url);
        setControllerFactory(clazz -> applicationContext.getBean(clazz));
    }
}
