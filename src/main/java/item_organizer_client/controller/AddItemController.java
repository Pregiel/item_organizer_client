package item_organizer_client.controller;

import item_organizer_client.ItemList;
import item_organizer_client.database.ItemOrganizerDatabase;
import item_organizer_client.database.repository.ItemRepository;
import item_organizer_client.model.ItemTableItem;
import item_organizer_client.utils.TextFieldUtils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import item_organizer_client.model.Item;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddItemController implements Initializable {
    public TextField idText, nameText, categoryText, amountText, buyPriceText, sellPriceText;
    public DatePicker dateText;
    public VBox addItemPane;
    public Label nameMinAlert, categoryMinAlert, idNullAlert, idDuplicateAlert, nameNullAlert, nameDuplicateAlert,
            amountNullAlert, sellPriceSmallerInfo, categoryMaxAlert, nameMaxAlert, buyNullAlert, sellNullAlert,
            categoryNullAlert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idText.textProperty().addListener(TextFieldUtils.onlyNumericListener(idText));
        idText.textProperty().addListener(TextFieldUtils.maxCharsAmountListener(idText, 4));
        idText.focusedProperty().addListener(TextFieldUtils.fillWithZerosListener(idText, 4));
        idText.focusedProperty().addListener(TextFieldUtils.isNullListener(idText, idNullAlert));
        idText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(idText, idNullAlert, idDuplicateAlert));
        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idDuplicateAlert);

        nameText.textProperty().addListener(TextFieldUtils.maxCharsAmountListener(nameText, 250));
        nameText.focusedProperty().addListener(TextFieldUtils.minCharsAmountListener(nameText, 3, nameMinAlert));
        nameText.focusedProperty().addListener(TextFieldUtils.isNullListener(nameText, nameNullAlert));
        nameText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(nameText, nameNullAlert,
                nameMinAlert, nameMaxAlert, nameDuplicateAlert));
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);

        categoryText.textProperty().addListener(TextFieldUtils.maxCharsAmountListener(categoryText, 250));
        categoryText.focusedProperty().addListener(TextFieldUtils.isNullListener(categoryText, categoryNullAlert));
        categoryText.focusedProperty().addListener(TextFieldUtils.minCharsAmountListener(categoryText, 3, categoryMinAlert));
        categoryText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(categoryText, categoryNullAlert, categoryMinAlert, categoryMaxAlert));
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert, categoryMaxAlert);

        amountText.textProperty().addListener(TextFieldUtils.onlyNumericListener(amountText));
        amountText.focusedProperty().addListener(TextFieldUtils.isNullListener(amountText, amountNullAlert));
        amountText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(amountText, amountNullAlert));
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);

        buyPriceText.textProperty().addListener(TextFieldUtils.priceListener(buyPriceText));
        buyPriceText.focusedProperty().addListener(TextFieldUtils.autoFillPriceListener(buyPriceText, "0,00"));
        buyPriceText.focusedProperty().addListener(TextFieldUtils.isNullListener(buyPriceText, buyNullAlert));
        buyPriceText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(sellPriceText, sellPriceSmallerInfo));
        ((Pane) buyPriceText.getParent()).getChildren().removeAll(buyNullAlert);

        sellPriceText.textProperty().addListener(TextFieldUtils.priceListener(sellPriceText));
        sellPriceText.focusedProperty().addListener(TextFieldUtils.autoFillPriceListener(sellPriceText, "0,00"));
        sellPriceText.focusedProperty().addListener(TextFieldUtils.isNullListener(sellPriceText, sellNullAlert));
        sellPriceText.focusedProperty().addListener(TextFieldUtils.removeAlertsListener(sellPriceText, sellPriceSmallerInfo));
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellPriceSmallerInfo, sellNullAlert);

        dateText.setValue(LocalDate.now());
        dateText.focusedProperty().addListener(TextFieldUtils.autoFillDateListener(dateText));
    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        nameText.setText("");
        categoryText.setText("");
        amountText.setText("");
        dateText.setValue(LocalDate.now());
        buyPriceText.setText("0,00");
        sellPriceText.setText("0,00");
    }

    public void submit(ActionEvent event) {
        if (validate()) {
            Item item = new Item(
                    Integer.valueOf(idText.getText()),
                    nameText.getText(),
                    categoryText.getText(),
                    Integer.valueOf(amountText.getText()),
                    sellPriceText.getText());

            ItemRepository.add(item);
            ItemList.refresh();
            clearAll(null);
        }
    }

    private boolean validate() {
        boolean success = true;
        if (idText.getText().length() == 0) {
            showAlert(idText, idNullAlert);
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
        }

        if (ItemRepository.findByName(nameText.getText()).size() > 0) {
            showAlert(nameText, nameDuplicateAlert);
            success = false;
        }

        if (categoryText.getText().length() == 0) {
            showAlert(categoryText, categoryNullAlert);
            success = false;
        } else if (categoryText.getText().length() < 3) {
            showAlert(categoryText, categoryMinAlert);
            success = false;
        } else if (categoryText.getText().length() > 250) {
            showAlert(categoryText, categoryMaxAlert);
            success = false;
        }

        if (amountText.getText().length() == 0) {
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
        ((Pane) control.getParent()).getChildren().add(label);
    }
}
