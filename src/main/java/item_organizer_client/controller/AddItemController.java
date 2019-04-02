package item_organizer_client.controller;

import item_organizer_client.ItemList;
import item_organizer_client.model.ItemTableItem;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import item_organizer_client.model.Item;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddItemController implements Initializable {
    public TextField idText;
    public TextField nameText;
    public TextField categoryText;
    public TextField amountText;
    public DatePicker dateText;
    public TextField buyPriceText;
    public TextField sellPriceText;
    public VBox addItemPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dateText.setValue(LocalDate.now());
    }

    public void clearAll(ActionEvent event) {
        idText.setText("");
        nameText.setText("");
        categoryText.setText("");
        amountText.setText("");
        dateText.setValue(LocalDate.now());
        buyPriceText.setText("");
        sellPriceText.setText("");
    }

    public void submit(ActionEvent event) {
        Item item = new Item(
                idText.getText(),
                nameText.getText(),
                categoryText.getText(),
                amountText.getText(),
                sellPriceText.getText());

        ItemList.add(new ItemTableItem(item));

        clearAll(null);
    }
}
