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
    private MainView currentView;

    public Label notificationCount;

    private static MainController instance;

    public static MainController getInstance() {
        return instance;
    }

    public MainController() {
        instance = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));
        ItemOrganizerDatabase.configureSessionFactory();
        showItemList(null);
        NotificationList.getInstance().setNotificationCount(notificationCount);

        makeBackup();
    }

    public void showItemList(ActionEvent event) {
        setupStage(MainView.ITEM_LIST);
    }

    public void showTransactionList(ActionEvent event) {
        setupStage(MainView.TRANSACTION_LIST);
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

    private void setupStage(MainView mainView) {
        SearchItemController.clearSavedValues();
        SearchTransactionController.clearSavedValues();
        try {
            currentNode = new SpringFXMLLoader(getClass().getResource(mainView.getView())).load();
            currentView = mainView;
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            Files.copy(new File(DATABASE_PATH).toPath(),
                    new File(String.format(DATABASE_BACKUP_PATH,
                            formatter.format(LocalDateTime.now()))).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MainView getCurrentView() {
        return currentView;
    }
}
