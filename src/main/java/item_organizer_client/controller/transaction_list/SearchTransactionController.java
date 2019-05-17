package item_organizer_client.controller.transaction_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class SearchTransactionController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    public TextField idText, priceFromText, priceToText;
    public ToggleGroup transactionGroup;
    public ToggleButton allTransactionToggle, buyTransactionToggle, sellTransactionToggle;
    public DateTimePicker dateFromText, dateToText;
    public VBox containPane;

    private SimpleListProperty<Item> transactionItemList;

    private static HashMap<String, String> savedValues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        transactionItemList = new SimpleListProperty<>(FXCollections.observableArrayList());

        initFields();

        TransactionList.getInstance().setUpSearchViewFilters(idText, priceFromText, priceToText, transactionGroup,
                dateFromText, dateToText, transactionItemList);

        clearAll(null);

        if (savedValues == null) {
            savedValues = new HashMap<>();
        } else {
            backupValues();
        }
    }

    @Override
    protected void initFields() {
        setIdTextFieldListeners(idText, Item.ID_DIGITS, false);
        setDateDatePickerListeners(dateFromText, false, 0, 0, 0);
        setDateDatePickerListeners(dateToText, false, 23, 59, 59);
        setPriceTextFieldListeners(priceFromText, false);
        setPriceTextFieldListeners(priceToText, false);

        allTransactionToggle.setUserData(null);
        buyTransactionToggle.setUserData(TransactionType.BUY);
        sellTransactionToggle.setUserData(TransactionType.SELL);
        transactionGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            }
        });
    }

    @Override
    protected void clearAlerts() {

    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        dateFromText.getEditor().setText("");
        dateToText.getEditor().setText("");
        priceFromText.setText("");
        priceToText.setText("");

        Platform.runLater(() -> idText.requestFocus());
    }

    private static final String SEARCH_TRANSACTIONITEM_ELEMENT_FXML = "/layout/SearchTransactionItemElementLayout.fxml";

    public void addNewElement(ActionEvent event) {
        addNewElement((Item) null);
    }

    public void addNewElement(Item item) {
        SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource(SEARCH_TRANSACTIONITEM_ELEMENT_FXML));

        try {
            HBox newElement = loader.load();

            SearchTransactionItemController controller = loader.getController();

            controller.setAddItemToListRunnable(() -> transactionItemList.add(controller.getSelectedItem()));
            controller.setRemoveItemFromListRunnable(() -> transactionItemList.remove(controller.getSelectedItem()));
            controller.getDeleteButton().setOnAction(event1 -> {
                transactionItemList.remove(controller.getSelectedItem());
                containPane.getChildren().remove(newElement);
            });

            if (item != null) {
                controller.setSelectedItem(item);
            }

            containPane.getChildren().add(containPane.getChildren().size() - 1, newElement);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void submit(ActionEvent event) {
        System.out.println(
                transactionGroup.getSelectedToggle().getUserData());
    }

    public void saveValues() {
        savedValues.clear();
        savedValues.put("numberText", idText.getText());
        savedValues.put("priceFromText", priceFromText.getText());
        savedValues.put("priceToText", priceToText.getText());
        savedValues.put("selectedTransactionToggle", String.valueOf(transactionGroup.getSelectedToggle().getUserData()));
        savedValues.put("dateFromText", dateFromText.getEditor().getText());
        savedValues.put("dateToText", dateToText.getEditor().getText());
        savedValues.put("transactionItemList", transactionItemList.stream().map(item -> item.getId().toString()).collect(Collectors.joining(" ")));
    }

    public void backupValues() {
        idText.setText(savedValues.get("numberText"));
        priceFromText.setText(savedValues.get("priceFromText"));
        priceToText.setText(savedValues.get("priceToText"));
        switch (savedValues.get("selectedTransactionToggle")) {
            case "BUY":
                buyTransactionToggle.fire();
                break;
            case "SELL":
                sellTransactionToggle.fire();
                break;
            default:
                allTransactionToggle.fire();
        }
        dateFromText.getEditor().setText(savedValues.get("dateFromText"));
        dateToText.getEditor().setText(savedValues.get("dateToText"));

        String[] itemIds = savedValues.get("transactionItemList").split(" ");
        for (String itemId : itemIds) {
            if (itemId.matches("\\d+")) {
                Item item = itemService.findById(Integer.valueOf(itemId));
                if (item != null) {
                    transactionItemList.add(item);
                    addNewElement(item);
                }
            }
        }
    }

    public static void clearSavedValues() {
        if (savedValues != null) {
            savedValues.clear();
            savedValues = null;
        }
    }

}
