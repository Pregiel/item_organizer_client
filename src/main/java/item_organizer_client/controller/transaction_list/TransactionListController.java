package item_organizer_client.controller.transaction_list;

import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.controller.item_list.InfoAboutItemController;
import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.table_item.TransactionTableElement;
import item_organizer_client.utils.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class TransactionListController extends SideBarController implements Initializable {
    public TableView<TransactionTableElement> transactionTableView;
    public TableColumn<TransactionTableElement, String> dateColumn;
    public Button homeButton, searchButton, infoButton;
    public SplitPane splitPane;

    private TransactionList transactionList;

    private static TransactionListController instance;

    public static TransactionListController getInstance() {
        return instance;
    }

    public TransactionListController() {
        instance = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        transactionList = TransactionList.getInstance();
        transactionList.init();

        setTableItems();

        transactionList.addListener(this::setTableItems);

        dateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(Utils.getDateFormatter())));

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_TRANSACTION, searchButton);
        getButtonMap().put(MenuView.INFO_TRANSACTION, infoButton);

        setSplitPane(splitPane);

        Utils.setTableColumnWidthProperty(transactionTableView, getPreferences());
    }

    private void setTableItems() {
        FilteredList<TransactionTableElement> filteredItemList
                = new FilteredList<>(FXCollections.observableList(transactionList.getTransactionList().stream()
                .map(TransactionTableElement::new).sorted().collect(Collectors.toList())), transactionTableElement -> true);

        SortedList<TransactionTableElement> sortedItemList = new SortedList<>(filteredItemList);
        sortedItemList.comparatorProperty().bind(transactionTableView.comparatorProperty());

        transactionTableView.setItems(sortedItemList);
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

    public void showInfoAbout(int id) {
        hideView();
        InfoAboutTransactionController infoController = showView(MenuView.INFO_TRANSACTION);

        infoController.showInfoAbout(id);
    }
}
