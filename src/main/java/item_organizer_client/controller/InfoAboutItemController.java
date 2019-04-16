package item_organizer_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class InfoAboutItemController implements Initializable {

    public VBox infoItemPane, detailsPane;
    public GridPane searchInputPane;
    public ComboBox searchText;
    public Label idNotExistAlert, nameNotExistAlert, selectedItemId, selectedItemName, selectedItemCategory,
            selectedItemAmount, selectedItemBuyPrice, selectedItemSellPrice;
    public RadioButton idRadioButton, nameRadioButton;
    public ToggleGroup searchGroup, priceHistoryGroup, transactionHistoryPrice;
    public TableView priceHistoryTable, transactionHistoryTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void nextStepSearch(ActionEvent event) {

    }

    public void changeSelectedItem(ActionEvent event) {

    }
}
