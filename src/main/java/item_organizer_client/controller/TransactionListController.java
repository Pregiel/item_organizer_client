package item_organizer_client.controller;

import item_organizer_client.MenuView;
import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.table_item.TransactionTableItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class TransactionListController extends SideBarController implements Initializable {
    public TableView<TransactionTableItem> transactionTableView;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public TableColumn<TransactionTableItem, String> dateColumn;
    public TableColumn priceColumn;
    public Button homeButton, searchButton, infoButton;
    public SplitPane splitPane;

    private Preferences preferences;

    private TransactionList transactionList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        preferences = Preferences.userRoot().node(this.getClass().getName());

        transactionList = TransactionList.getInstance();
        transactionList.init();

        transactionTableView.getItems().addAll(transactionList.getItemListAsTableItems());

        transactionList.addListener(() -> {
            transactionTableView.getItems().clear();
            transactionTableView.getItems().addAll(transactionList.getItemListAsTableItems());
        });

        dateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(dateFormatter)));

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_TRANSACTION, searchButton);
        getButtonMap().put(MenuView.INFO_TRANSACTION, infoButton);

        setSplitPane(splitPane);
        setPreferences(preferences);

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

    public void goHome(ActionEvent event) {
        hideView();
    }

    public void showSearchView(ActionEvent event) {
        toggleView(MenuView.SEARCH_TRANSACTION);
    }

    public void showInfoView(ActionEvent event) {
        toggleView(MenuView.INFO_TRANSACTION);
    }
}
