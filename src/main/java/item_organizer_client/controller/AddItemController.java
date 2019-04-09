package item_organizer_client.controller;

import item_organizer_client.model.list.ItemList;
import item_organizer_client.database.repository.*;
import item_organizer_client.model.*;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.Utils;
import item_organizer_client.utils.listeners.ComboBoxListeners;
import item_organizer_client.utils.listeners.DatePickerListeners;
import item_organizer_client.utils.listeners.SpinnerListeners;
import item_organizer_client.utils.listeners.TextFieldListeners;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.TextFields;
import tornadofx.control.DateTimePicker;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AddItemController implements Initializable {
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

        idText.textProperty().addListener(TextFieldListeners.onlyNumericListener(idText));
        idText.textProperty().addListener(TextFieldListeners.maxCharsAmountListener(idText, 4));
        idText.focusedProperty().addListener(TextFieldListeners.fillWithZerosListener(idText, 4));
        idText.focusedProperty().addListener(TextFieldListeners.isNullListener(idText, idNullAlert));
        idText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(idText));
        idText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(idText.getParent(), idNullAlert, idMaxAlert, idDuplicateAlert));

        nameText.textProperty().addListener(TextFieldListeners.maxCharsAmountListener(nameText, 250));
        nameText.focusedProperty().addListener(TextFieldListeners.minCharsAmountListener(nameText, 3, nameMinAlert));
        nameText.focusedProperty().addListener(TextFieldListeners.isNullListener(nameText, nameNullAlert));
        nameText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(nameText));
        nameText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(nameText.getParent(), nameNullAlert,
                nameMinAlert, nameMaxAlert, nameDuplicateAlert));

        categoryText.getItems().addAll(CategoryRepository.getAll()
                .stream().map(Category::getName).collect(Collectors.toList()));
        TextFields.bindAutoCompletion(categoryText.getEditor(), categoryText.getItems());
        categoryText.valueProperty().addListener(ComboBoxListeners.maxCharsAmountListener(categoryText, 250));
        categoryText.focusedProperty().addListener(ComboBoxListeners.isNullListener(categoryText, categoryNullAlert));
        categoryText.focusedProperty().addListener(ComboBoxListeners.minCharsAmountListener(categoryText, 3, categoryMinAlert));
        categoryText.focusedProperty().addListener(ComboBoxListeners.autoTrimListener(categoryText));
        categoryText.focusedProperty().addListener(ComboBoxListeners.removeAlertsListener(categoryText.getParent(), categoryNullAlert, categoryMinAlert, categoryMaxAlert));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1);
        amountText.setValueFactory(valueFactory);
        amountText.getEditor().textProperty().addListener(SpinnerListeners.onlyNumericListener(amountText));
//        amountText.focusedProperty().addListener(SpinnerListeners.isNullListener(amountText, amountNullAlert));
        amountText.focusedProperty().addListener(SpinnerListeners.autoFillListener(amountText, 1));
        amountText.getEditor().textProperty().addListener(TextFieldListeners.setBuyPriceDependOnTypeListener(buyPriceText,
                buyPricePerItemText, amountText, buyPriceType));
        amountText.focusedProperty().addListener(SpinnerListeners.removeAlertsListener(amountText.getParent(),
                amountNullAlert));

        buyPriceText.textProperty().addListener(TextFieldListeners.priceListener(buyPriceText));
        buyPriceText.textProperty().addListener(TextFieldListeners.setBuyPriceDependOnTypeListener(buyPriceText,
                buyPricePerItemText, amountText, buyPriceType));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.autoFillPriceListener(buyPriceText, "0.00"));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.isNullListener(buyPriceText, buyNullAlert,
                (Pane) buyPriceText.getParent().getParent()));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(buyPriceText));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(
                buyPriceText.getParent().getParent(), buyNullAlert, sellPriceSmallerInfo));

        sellPriceText.textProperty().addListener(TextFieldListeners.priceListener(sellPriceText));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.autoFillPriceListener(sellPriceText, "0.00"));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.isNullListener(sellPriceText, sellNullAlert));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(sellPriceText));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(sellPriceText.getParent(), sellNullAlert, sellPriceSmallerInfo));

        dateText.focusedProperty().addListener(DatePickerListeners.autoFillDateListener(dateText));

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
            int amountValue = amountText.getValue();
            double sellPriceValue = Double.valueOf(sellPriceText.getText());
            double buyPriceValue = Double.valueOf(buyPriceText.getText());
            Timestamp date = Timestamp.valueOf(dateText.getDateTimeValue());

            if (buyPriceType.getSelectionModel().getSelectedIndex() != 0) {
                buyPriceValue = Utils.round(buyPriceValue / amountValue, 2);
            }

            Item item = new Item(Integer.valueOf(idText.getText()), nameText.getText(),
                    CategoryRepository.findOrAdd(new Category(categoryText.getValue())), amountValue);
            Price sellPrice = new Price(sellPriceValue, PriceType.SELL, item, date);
            Price buyPrice = new Price(buyPriceValue, PriceType.BUY, item, date);
            Transaction transaction = new Transaction(date, TransactionType.BUY);
            TransactionItem transactionItem = new TransactionItem(item, transaction, buyPrice, amountValue);

            ItemRepository.add(item);
            PriceRepository.add(sellPrice);
            PriceRepository.add(buyPrice);
            TransactionRepository.add(transaction);
            TransactionItemRepository.add(transactionItem);

            refresh();
            clearAll(null);
        }
    }

    private void refresh() {
        ItemList.refresh();

        categoryText.getItems().clear();
        categoryText.getItems().addAll(CategoryRepository.getAll()
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
        } else if (ItemRepository.findById(Integer.parseInt(idText.getText())) != null) {
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
        } else if (ItemRepository.findByName(nameText.getText()).size() > 0) {
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
