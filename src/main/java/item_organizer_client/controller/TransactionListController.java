package item_organizer_client.controller;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import item_organizer_client.model.Transaction;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionListController implements Initializable {
    public TableView<Transaction> transactionTableView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Transaction> items = transactionTableView.getItems();
        items.add(new Transaction("1", "ZAKUP", "21.03.2019", "2.5zl", "5", "12.5zl"));
    }
}
