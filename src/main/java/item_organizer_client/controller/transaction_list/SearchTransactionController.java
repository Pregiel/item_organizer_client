package item_organizer_client.controller.transaction_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import item_organizer_client.model.list.ItemList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

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

    private static HashMap<String, String> savedValues;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        initFields();

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
        setDateDatePickerListeners(dateFromText, false);
        setDateDatePickerListeners(dateToText, false);
        setPriceTextFieldListeners(priceFromText, false);
        setPriceTextFieldListeners(priceToText, false);

//        ItemList.getInstance().setUpSearchViewFilters(ItemListController.getInstance().getHeaderSearchText(), idText,
//                nameText, categoryText, amountFromText, amountToText, buyPriceFromText, buyPriceToText,
//                sellPriceFromText, sellPriceToText);
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
    }

    public void addNewContainItem(ActionEvent event) {
        HBox hBox = new HBox();

        ComboBox<String> searchText = new ComboBox<>();
        searchText.setEditable(true);
        searchText.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(searchText, Priority.ALWAYS);

        searchText.getItems().addAll(itemService.getAll().stream().map(Item::toTitle).sorted().collect(Collectors.toList()));

        TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());

        Button remove = new Button("x");
        remove.setOnAction(e -> containPane.getChildren().remove(hBox));

        hBox.getChildren().addAll(searchText, remove);

        containPane.getChildren().add(containPane.getChildren().size() - 1, hBox);
    }

    public void submit(ActionEvent event) {

    }

    public void saveValues() {
        savedValues.clear();
        savedValues.put("idText", idText.getText());
        savedValues.put("priceFromText", priceFromText.getText());
        savedValues.put("priceToText", priceToText.getText());
    }

    public void backupValues() {
        idText.setText(savedValues.get("idText"));
        priceFromText.setText(savedValues.get("priceFromText"));
        priceToText.setText(savedValues.get("priceToText"));
    }

}
