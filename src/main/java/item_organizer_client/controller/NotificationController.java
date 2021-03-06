package item_organizer_client.controller;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.model.element.NotificationElement;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.list.NotificationList;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@FXMLController
public class NotificationController extends Controller {

    public VBox elementsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));
        NotificationList.getInstance().setNotificationController(this);

        refresh();
    }

    public void refresh() {
        elementsPane.getChildren().clear();
        for (NotificationElement notificationElement : NotificationList.getInstance().getNotificationList()) {
            if (ItemListController.getInstance().getShowHiddenProductsCheckBox().isSelected() ||
                    (!ItemListController.getInstance().getShowHiddenProductsCheckBox().isSelected() && !notificationElement.getItem().getHidden())) {
                try {
                    FXMLLoader loader
                            = new SpringFXMLLoader(getClass().getResource("/layout/NotificationElementLayout.fxml"));

                    Node node = loader.load();
                    node.getStyleClass().add(notificationElement.getType().getStyle(elementsPane.getChildren().size()));
                    node.setCursor(Cursor.HAND);
                    node.setOnMouseClicked(event -> notificationElement.getOnClick().run());

                    NotificationElementController controller = loader.getController();

                    controller.getTagText().setText(notificationElement.getTag().toText());
                    controller.getMessageText().setText(notificationElement.getMessage());
                    controller.getCloseButton().setOnAction(event ->
                            NotificationList.getInstance().remove(notificationElement));

                    elementsPane.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeAll(ActionEvent event) {
        List<Button> buttonList = new ArrayList<>();
        for (Node node : elementsPane.getChildren()) {
            buttonList.add((Button) node.lookup("#closeButton"));
        }
        buttonList.forEach(Button::fire);
    }

    public void clearPane() {
        if (NotificationList.getInstance().getNotificationList() != null) {
            NotificationList.getInstance().getNotificationList().clear();
        }
        elementsPane.getChildren().clear();
    }

    public void reset(ActionEvent event) {
        NotificationList.getInstance().resetIgnoredFile();
        NotificationList.getInstance().check();
        refresh();
        ItemListController.getInstance().getItemTableView().refresh();
    }
}
