package item_organizer_client.controller;

import item_organizer_client.controller.item_list.SearchItemController;
import item_organizer_client.controller.transaction_list.SearchTransactionController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.ItemOrganizerDatabase;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.element.NotificationElement;
import item_organizer_client.model.list.NotificationList;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class MainController extends Controller implements Initializable {
    @Autowired
    private ItemService itemService;

    public BorderPane mainPane;

    private Node currentNode;

    public Label notificationCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));
        ItemOrganizerDatabase.configureSessionFactory();
        showItemList(null);
        NotificationList.getInstance().setNotificationCount(notificationCount);

        makeBackup();
    }

    public void showItemList(ActionEvent event) {
        setupStage("/layout/ItemListLayout.fxml");
    }

    public void showTransactionList(ActionEvent event) {
        setupStage("/layout/TransactionListLayout.fxml");
    }

    public void showSummary(ActionEvent event) {
        System.out.println(itemService.findByName("Kubek czar"));
    }

    public void showNotification(ActionEvent event) {
        if (mainPane.getRight() == null) {
            try {
                Node node = new SpringFXMLLoader(getClass().getResource("/layout/NotificationLayout.fxml")).load();
                mainPane.setRight(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            mainPane.setRight(null);
        }
    }

    private void setupStage(String fxml) {
        SearchItemController.clearSavedValues();
        SearchTransactionController.clearSavedValues();
        try {
            currentNode = new SpringFXMLLoader(getClass().getResource(fxml)).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.setCenter(currentNode);
    }

    private static final String DATABASE_PATH = "database.db";
    private static final String DATABASE_BACKUP_PATH = "backup/database_%s.db";

    private void makeBackup() {
        File folder = new File("backup/");
        if (!folder.exists()) {
            folder.mkdirs();
        }
//        List<File> listOfFiles = Arrays.stream(Objects.requireNonNull(folder.listFiles())).filter(
//                file -> file.getName().matches("database.*.db")).collect(Collectors.toList());

        try {
            Files.copy(new File(DATABASE_PATH).toPath(),
                    new File(String.format(DATABASE_BACKUP_PATH,
                            Instant.now().toEpochMilli())).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
