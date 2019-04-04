package item_organizer_client.controller;

import item_organizer_client.ItemList;
import item_organizer_client.database.ItemOrganizerDatabase;
import item_organizer_client.database.repository.ItemRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import item_organizer_client.model.Item;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public BorderPane mainPane;

    private Node currentNode;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ItemOrganizerDatabase.configureSessionFactory();
        ItemList.init();

        showItemList(null);
    }

    public void showItemList(ActionEvent event) {
        setupStage("/layout/ItemListLayout.fxml");
    }

    public void showTransactionList(ActionEvent event) {
        setupStage("/layout/TransactionListLayout.fxml");
    }

    public void showSummary(ActionEvent event) {
        System.out.println(ItemRepository.findByName("as"));
    }

    private void setupStage(String fxml) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

        try {
            currentNode = loader.load();
            mainPane.setCenter(currentNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
