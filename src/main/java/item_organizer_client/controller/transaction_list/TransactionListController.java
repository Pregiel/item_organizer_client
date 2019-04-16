package item_organizer_client.controller.transaction_list;

import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.table_item.TransactionTableItem;
import item_organizer_client.utils.Utils;
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
    public TableColumn<TransactionTableItem, String> dateColumn;
    public Button homeButton, searchButton, infoButton;
    public SplitPane splitPane;

    private TransactionList transactionList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        transactionList = TransactionList.getInstance();
        transactionList.init();

        transactionTableView.getItems().addAll(transactionList.getItemListAsTableItems());

        transactionList.addListener(() -> {
            transactionTableView.getItems().clear();
            transactionTableView.getItems().addAll(transactionList.getItemListAsTableItems());
        });

        dateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(Utils.getDateFormatter())));

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_TRANSACTION, searchButton);
        getButtonMap().put(MenuView.INFO_TRANSACTION, infoButton);

        setSplitPane(splitPane);


        Utils.setTableColumnWidthProperty(transactionTableView, getPreferences());
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
