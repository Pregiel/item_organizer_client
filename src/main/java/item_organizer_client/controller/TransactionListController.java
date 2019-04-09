package item_organizer_client.controller;

import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.table_item.TransactionTableItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class TransactionListController implements Initializable {
    public TableView<TransactionTableItem> transactionTableView;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");
    public TableColumn<TransactionTableItem, String> dateColumn;
    public TableColumn priceColumn;

    private Preferences preferences;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        TransactionList.init();

        transactionTableView.getItems().addAll(TransactionList.getItemListAsTableItems());

        TransactionList.addListener(() -> {
            transactionTableView.getItems().clear();
            transactionTableView.getItems().addAll(TransactionList.getItemListAsTableItems());
        });

        dateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(dateFormatter)));

        int i = 0;
        for (TableColumn<TransactionTableItem, ?> transactionTableItemTableColumn : transactionTableView.getColumns()) {
            double width = preferences.getDouble(
                    "column" + i + "_width",
                    transactionTableItemTableColumn.getPrefWidth());

            transactionTableItemTableColumn.setPrefWidth(width);

            int finalI = i;
            transactionTableItemTableColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                preferences.putDouble("column" + finalI + "_width", (Double) newValue);
            });
            i++;
        }
    }
}
