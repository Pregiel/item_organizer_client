package item_organizer_client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class InfoAboutTransactionController implements Initializable {
    public VBox infoItemPane, detailsPane;
    public GridPane searchInputPane;
    public ComboBox searchText;
    public Label idNotExistAlert, selectedTransactionId, selectedTransactionType, selectedTransactionDate,
            selectedTransactionPrice;
    public TableView transactionItemsTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void nextStepSearch(ActionEvent event) {
    }

    public void changeSelectedTransaction(ActionEvent event) {
    }
}
