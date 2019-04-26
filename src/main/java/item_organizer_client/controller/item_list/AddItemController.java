package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.*;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.*;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.MyAlerts;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class AddItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionItemService transactionItemService;

    public TextField idText, nameText, buyPriceText, sellPriceText;
    public Spinner<Integer> amountText, safeAmountText;
    public DateTimePicker dateText;
    public ComboBox<String> categoryText;
    public VBox addItemPane;
    public Label nameMinAlert, categoryMinAlert, idNullAlert, idDuplicateAlert, nameNullAlert, nameDuplicateAlert,
            amountNullAlert, sellPriceSmallerInfo, categoryMaxAlert, nameMaxAlert, buyNullAlert, sellNullAlert,
            categoryNullAlert, idMaxAlert, buyPricePerItemText, safeAmountNullAlert;
    public ComboBox<String> buyPriceType;
    public HBox buyPricePerItemPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        initFields();
        clearAll(null);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setIdTextFieldListeners(idText, Item.ID_DIGITS, itemService, idText.getParent(), idNullAlert, idDuplicateAlert, idMaxAlert);
        setNameTextFieldListeners(nameText, nameText.getParent(), nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        setCategoryComboBoxListeners(categoryText, categoryService, categoryText.getParent(),
                categoryNullAlert, categoryMinAlert, categoryMaxAlert);
        setAmountSpinnerListeners(amountText, Item.INITIAL_AMOUNT_VALUE, amountText.getParent(), amountNullAlert);
        setAmountSpinnerListeners(safeAmountText, Item.INITIAL_SAFE_AMOUNT_VALUE, safeAmountText.getParent(), safeAmountNullAlert);
        setPriceTextFieldListeners(buyPriceText, buyPriceText.getParent().getParent(), buyNullAlert, sellPriceSmallerInfo);
        setPriceTypeListeners(buyPriceText, buyPriceType, buyPriceText.getParent().getParent(), buyPricePerItemPane,
                buyPricePerItemText, amountText);
        setPriceTextFieldListeners(sellPriceText, sellPriceText.getParent(), sellNullAlert, sellPriceSmallerInfo);
        setDateDatePickerListeners(dateText);
    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        nameText.setText("");
        categoryText.setValue("");
        amountText.getEditor().setText(String.valueOf(Item.INITIAL_AMOUNT_VALUE));
        safeAmountText.getEditor().setText(String.valueOf(Item.INITIAL_SAFE_AMOUNT_VALUE));
        dateText.setValue(LocalDate.now());
        buyPriceText.setText("0.00");
        sellPriceText.setText("0.00");
    }

    @Override
    protected void clearAlerts() {
        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
        ((Pane) safeAmountText.getParent()).getChildren().removeAll(safeAmountNullAlert);
        ((Pane) buyPriceText.getParent().getParent()).getChildren().removeAll(buyNullAlert);
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellPriceSmallerInfo, sellNullAlert);
    }


    public void submit(ActionEvent event) {
        idText.setText(idText.getText().trim());
        nameText.setText(nameText.getText().trim());
        categoryText.setValue(categoryText.getValue().trim());
        buyPriceText.setText(buyPriceText.getText().trim());
        sellPriceText.setText(sellPriceText.getText().trim());

        if (validate()) {
            int amountValue = Integer.parseInt(amountText.getEditor().getText());
            int safeAmountValue = Integer.parseInt(safeAmountText.getEditor().getText());
            BigDecimal sellPriceValue = new BigDecimal(sellPriceText.getText());
            BigDecimal buyPriceValue = new BigDecimal(buyPriceText.getText());
            Timestamp date = Timestamp.valueOf(dateText.getDateTimeValue());

            if (buyPriceType.getSelectionModel().getSelectedIndex() != 0) {
                buyPriceValue = buyPriceValue.divide(BigDecimal.valueOf(amountValue), 2, RoundingMode.CEILING);
            }

            Item item = new Item(Integer.valueOf(idText.getText()), nameText.getText(),
                    categoryService.findOrAdd(new Category(categoryText.getValue())), amountValue, safeAmountValue);
            Price sellPrice = new Price(sellPriceValue, PriceType.SELL, item, date);
            Price buyPrice = new Price(buyPriceValue, PriceType.BUY, item, date);
            Transaction transaction = new Transaction(date, TransactionType.BUY);
            TransactionItem transactionItem = new TransactionItem(item, transaction, buyPrice, amountValue);

            itemService.add(item);
            priceService.add(sellPrice);
            priceService.add(buyPrice);
            transactionService.add(transaction);
            transactionItemService.add(transactionItem);

            clearAll(null);
            MyAlerts.showInfo("Sukces", "Operacja zakończona sukcesem.");
            refresh();
        }
    }

    private void refresh() {
        ItemList.getInstance().refresh();

        categoryText.getItems().clear();
        categoryText.getItems().addAll(categoryService.getAll()
                .stream().map(Category::getName).collect(Collectors.toList()));
        TextFields.bindAutoCompletion(categoryText.getEditor(), categoryText.getItems());
    }

    private boolean validate() {
        boolean success = true;
        if (idText.getText().length() == 0) {
            showAlert(idText, idNullAlert);
            success = false;
        } else if (idText.getText().length() > 4) {
            showAlert(idText, idMaxAlert);
            success = false;
        } else if (itemService.findById(Integer.parseInt(idText.getText())) != null) {
            showAlert(idText, idDuplicateAlert);
            success = false;
        }

        if (nameText.getText().length() == 0) {
            showAlert(nameText, nameNullAlert);
            success = false;
        } else if (nameText.getText().length() < 3) {
            showAlert(nameText, nameMinAlert);
            success = false;
        } else if (nameText.getText().length() > 250) {
            showAlert(nameText, nameMaxAlert);
            success = false;
        } else if (itemService.findByName(nameText.getText()) != null) {
            showAlert(nameText, nameDuplicateAlert);
            success = false;
        }

        if (categoryText.getValue() == null) {
            categoryText.setValue("");
            showAlert(categoryText, categoryNullAlert);
        } else if (categoryText.getValue().length() == 0) {
            showAlert(categoryText, categoryNullAlert);
            success = false;
        } else if (categoryText.getValue().length() < 3) {
            showAlert(categoryText, categoryMinAlert);
            success = false;
        } else if (categoryText.getValue().length() > 250) {
            showAlert(categoryText, categoryMaxAlert);
            success = false;
        }

        if (amountText.getValue().toString().length() == 0) {
            showAlert(amountText, amountNullAlert);
            success = false;
        }

        if (buyPriceText.getText().length() == 0) {
            showAlert(buyPriceText, buyNullAlert);
            success = false;
        }

        if (sellPriceText.getText().length() == 0) {
            showAlert(sellPriceText, sellNullAlert);
            success = false;
        }

        return success;
    }

    private void showAlert(Control control, Label label) {
        if (!((Pane) control.getParent()).getChildren().contains(label)) {
            ((Pane) control.getParent()).getChildren().add(label);
        }
    }
}
