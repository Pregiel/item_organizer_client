package item_organizer_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BuyItemController implements Initializable {
    private static final String BUY_ELEMENT_FXML = "/layout/BuyItemElementLayout.fxml";

    public VBox newItemPane;
    private List<Node> newItemList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newItemList = new ArrayList<>();
        addItem(null);
    }

    public void clearAll(ActionEvent event) {
        newItemPane.getChildren().removeAll(newItemList);
        newItemList.clear();
        addItem(null);
    }

    public void submit(ActionEvent event) {

    }

    public void addItem(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BUY_ELEMENT_FXML));

        try {
            Node newElement = loader.load();

            BuyItemElementController controller = loader.getController();

            newItemList.add(newElement);
            newItemPane.getChildren().add(newElement);

            controller.setElementId(newItemList.size());
            controller.setItemTitle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
