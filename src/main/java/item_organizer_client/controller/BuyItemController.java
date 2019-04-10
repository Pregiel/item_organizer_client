package item_organizer_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BuyItemController implements Initializable {
    private static final String BUY_ELEMENT_FXML = "/layout/BuyItemElementLayout.fxml";

    public VBox newItemPane;
    private List<BuyItemElementController> controllerList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controllerList = new ArrayList<>();
        addItem(null);
    }

    public void clearAll(ActionEvent event) {
        newItemPane.getChildren().removeAll(newItemPane.getChildren());
        controllerList.clear();
        addItem(null);
    }

    public void submit(ActionEvent event) {

    }

    public void addItem(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(BUY_ELEMENT_FXML));

        try {
            TitledPane newElement = loader.load();

            BuyItemElementController controller = loader.getController();

            controllerList.add(controller);
            newItemPane.getChildren().add(newElement);

            controller.setElementId(controllerList.size());
            controller.setItemTitle();
            controller.setBuyItemController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BuyItemElementController> getControllerList() {
        return controllerList;
    }

    public VBox getNewItemPane() {
        return newItemPane;
    }

    public void removeItem(BuyItemElementController controller) {
        newItemPane.getChildren().remove(controller.getItemPane());
        controllerList.remove(controller);

        if (controllerList.size() > 0) {
            for (int i = 0; i < controllerList.size(); i++) {
                controllerList.get(i).setElementId(i+1);
            }
        } else {
            addItem(null);
        }
    }
}
