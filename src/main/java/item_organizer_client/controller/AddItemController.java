package item_organizer_client.controller;

import item_organizer_client.database.service.*;
import item_organizer_client.listeners.*;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.*;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
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

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class AddItemController implements Initializable {
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
    public Spinner<Integer> amountText;
    public DateTimePicker dateText;
    public ComboBox<String> categoryText;
    public VBox addItemPane;
    public Label nameMinAlert, categoryMinAlert, idNullAlert, idDuplicateAlert, nameNullAlert, nameDuplicateAlert,
            amountNullAlert, sellPriceSmallerInfo, categoryMaxAlert, nameMaxAlert, buyNullAlert, sellNullAlert,
            categoryNullAlert, idMaxAlert, buyPricePerItemText;
    public ComboBox<String> buyPriceType;
    public HBox buyPricePerItemPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buyPriceType.getItems().addAll("za sztukę", "za wszystko");
        buyPriceType.getSelectionModel().select(0);
        ((Pane) buyPriceText.getParent().getParent()).getChildren().remove(buyPricePerItemPane);
        buyPriceType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                ((Pane) buyPriceText.getParent().getParent()).getChildren().remove(buyPricePerItemPane);
            } else {
                ((Pane) buyPriceText.getParent().getParent()).getChildren().add(buyPricePerItemPane);
                buyPricePerItemText.setText(Utils.round(
                        Double.valueOf(buyPriceText.getText()) / Integer.valueOf(amountText.getEditor().getText()), 2) + " zł");
            }
        });

        TextFieldListener.selectAllOnFocusListener(idText);
        TextFieldListener.onlyNumericListener(idText);
        TextFieldListener.maxCharsAmountListener(idText, 4);
        TextFieldListener.fillWithZerosListener(idText, 4);
        TextFieldListener.isNullListener(idText, idNullAlert);
        TextFieldListener.autoTrimListener(idText);
        TextFieldListener.removeAlertsListener(idText.getParent(), idNullAlert, idMaxAlert, idDuplicateAlert);

        TextFieldListener.selectAllOnFocusListener(nameText);
        TextFieldListener.maxCharsAmountListener(nameText, 250);
        TextFieldListener.minCharsAmountListener(nameText, 3, nameMinAlert);
        TextFieldListener.isNullListener(nameText, nameNullAlert);
        TextFieldListener.autoTrimListener(nameText);
        TextFieldListener.removeAlertsListener(nameText.getParent(), nameNullAlert, nameMinAlert, nameMaxAlert,
                nameDuplicateAlert);

        categoryText.getItems().addAll(categoryService.getAll()
                .stream().map(Category::getName).collect(Collectors.toList()));
        TextFields.bindAutoCompletion(categoryText.getEditor(), categoryText.getItems());
        ComboBoxListener.selectAllOnFocusListener(categoryText);
        ComboBoxListener.maxCharsAmountListener(categoryText, 250);
        ComboBoxListener.isNullListener(categoryText, categoryNullAlert);
        ComboBoxListener.minCharsAmountListener(categoryText, 3, categoryMinAlert);
        ComboBoxListener.autoTrimListener(categoryText);
        ComboBoxListener.removeAlertsListener(categoryText.getParent(), categoryNullAlert, categoryMinAlert, categoryMaxAlert);

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                0, Integer.MAX_VALUE, 1);
        amountText.setValueFactory(valueFactory);
        SpinnerListener.selectAllOnFocusListener(amountText);
        SpinnerListener.onlyNumericListener(amountText);
        SpinnerListener.autoFillListener(amountText, 1);
        SpinnerListener.removeAlertsListener(amountText.getParent(), amountNullAlert);

        TextFieldListener.selectAllOnFocusListener(buyPriceText);
        TextFieldListener.priceListener(buyPriceText);
        TextFieldListener.autoFillPriceListener(buyPriceText, "0.00");
        TextFieldListener.isNullListener(buyPriceText, buyNullAlert, (Pane) buyPriceText.getParent().getParent());
        TextFieldListener.autoTrimListener(buyPriceText);
        TextFieldListener.removeAlertsListener(buyPriceText.getParent().getParent(), buyNullAlert, sellPriceSmallerInfo);

        TextFieldListener.selectAllOnFocusListener(sellPriceText);
        TextFieldListener.priceListener(sellPriceText);
        TextFieldListener.autoFillPriceListener(sellPriceText, "0.00");
        TextFieldListener.isNullListener(sellPriceText, sellNullAlert);
        TextFieldListener.autoTrimListener(sellPriceText);
        TextFieldListener.removeAlertsListener(sellPriceText.getParent(), sellNullAlert, sellPriceSmallerInfo);

        DatePickerListener.selectAllOnFocusListener(dateText);
        DatePickerListener.autoFillDateListener(dateText);

        CustomListener.updateBuyPerItemLabelListener(buyPricePerItemText, buyPriceText, amountText, buyPriceType);

        clearAll(null);
    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        nameText.setText("");
        categoryText.setValue("");
        amountText.getEditor().setText("1");
        dateText.setValue(LocalDate.now());
        buyPriceText.setText("0.00");
        sellPriceText.setText("0.00");
        clearAllAlerts();
    }

    private void clearAllAlerts() {
        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
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
            double sellPriceValue = Double.valueOf(sellPriceText.getText());
            double buyPriceValue = Double.valueOf(buyPriceText.getText());
            Timestamp date = Timestamp.valueOf(dateText.getDateTimeValue());

            if (buyPriceType.getSelectionModel().getSelectedIndex() != 0) {
                buyPriceValue = Utils.round(buyPriceValue / amountValue, 2);
            }

            Item item = new Item(Integer.valueOf(idText.getText()), nameText.getText(),
                    categoryService.findOrAdd(new Category(categoryText.getValue())), amountValue);
            Price sellPrice = new Price(sellPriceValue, PriceType.SELL, item, date);
            Price buyPrice = new Price(buyPriceValue, PriceType.BUY, item, date);
            Transaction transaction = new Transaction(date, TransactionType.BUY);
            TransactionItem transactionItem = new TransactionItem(item, transaction, buyPrice, amountValue);

            itemService.add(item);
            priceService.add(sellPrice);
            priceService.add(buyPrice);
            transactionService.add(transaction);
            transactionItemService.add(transactionItem);

            refresh();
            clearAll(null);
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
        } else if (itemService.findByName(nameText.getText()).size() > 0) {
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
