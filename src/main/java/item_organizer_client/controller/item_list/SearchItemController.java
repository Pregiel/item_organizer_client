package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.model.Item;
import item_organizer_client.model.list.ItemList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@FXMLController
@Component
public class SearchItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private CategoryService categoryService;

    public TextField idText, nameText, buyPriceFromText, buyPriceToText, sellPriceFromText, sellPriceToText;
    public Spinner<Integer> amountFromText, amountToText;
    public ComboBox<String> categoryText;

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
        setNameTextFieldListeners(nameText);
        setCategoryComboBoxListeners(categoryText, categoryService);
        setAmountSpinnerListeners(amountFromText, false);
        setAmountSpinnerListeners(amountToText, false);
        setPriceTextFieldListeners(buyPriceFromText, false);
        setPriceTextFieldListeners(buyPriceToText, false);
        setPriceTextFieldListeners(sellPriceFromText, false);
        setPriceTextFieldListeners(sellPriceToText, false);

        ItemList.getInstance().setUpSearchViewFilters(ItemListController.getInstance().getHeaderSearchText(), idText,
                nameText, categoryText, amountFromText, amountToText, buyPriceFromText, buyPriceToText,
                sellPriceFromText, sellPriceToText);
    }

    @Override
    protected void clearAlerts() {

    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        nameText.setText("");
        categoryText.setValue("");
        amountFromText.getEditor().setText("");
        amountToText.getEditor().setText("");
        buyPriceFromText.setText("");
        buyPriceToText.setText("");
        sellPriceFromText.setText("");
        sellPriceToText.setText("");
    }

    public void submit(ActionEvent event) {

    }

    public void saveValues() {
        savedValues.clear();
        savedValues.put("numberText", idText.getText());
        savedValues.put("nameText", nameText.getText());
        savedValues.put("categoryText", categoryText.getEditor().getText());
        savedValues.put("amountFromText", amountFromText.getEditor().getText());
        savedValues.put("amountToText", amountToText.getEditor().getText());
        savedValues.put("buyPriceFromText", buyPriceFromText.getText());
        savedValues.put("buyPriceToText", buyPriceToText.getText());
        savedValues.put("sellPriceFromText", sellPriceFromText.getText());
        savedValues.put("sellPriceToText", sellPriceToText.getText());
    }

    public void backupValues() {
        idText.setText(savedValues.get("numberText"));
        nameText.setText(savedValues.get("nameText"));
        categoryText.getEditor().setText(savedValues.get("categoryText"));
        amountFromText.getEditor().setText(savedValues.get("amountFromText"));
        amountToText.getEditor().setText(savedValues.get("amountToText"));
        buyPriceFromText.setText(savedValues.get("buyPriceFromText"));
        buyPriceToText.setText(savedValues.get("buyPriceToText"));
        sellPriceFromText.setText(savedValues.get("sellPriceFromText"));
        sellPriceToText.setText(savedValues.get("sellPriceToText"));
    }

    public static void clearSavedValues() {
        if (savedValues != null) {
            savedValues.clear();
            savedValues = null;
        }
    }

}
