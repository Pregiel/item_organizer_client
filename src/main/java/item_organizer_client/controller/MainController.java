package item_organizer_client.controller;

import item_organizer_client.controller.item_list.SearchItemController;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class MainController implements Initializable {
    @Autowired
    private ItemService itemService;

    public BorderPane mainPane;

    private Node currentNode;

    public Label notificationCount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ItemOrganizerDatabase.configureSessionFactory();
        showItemList(null);
        NotificationList.getInstance().setNotificationCount(notificationCount);
    }

    public void showItemList(ActionEvent event) {
        setupStage("/layout/ItemListLayout.fxml");
    }

    public void showTransactionList(ActionEvent event) {
        setupStage("/layout/TransactionListLayout.fxml");
    }

    public void showSummary(ActionEvent event) {
        System.out.println(itemService.findByName("as"));
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
        try {
            currentNode = new SpringFXMLLoader(getClass().getResource(fxml)).load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mainPane.setCenter(currentNode);
    }
}
