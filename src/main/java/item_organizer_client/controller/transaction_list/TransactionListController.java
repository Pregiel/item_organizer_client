package item_organizer_client.controller.transaction_list;

import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.list.TransactionList;
import item_organizer_client.model.element.TransactionTableElement;
import item_organizer_client.utils.TableColumnFormatter;
import item_organizer_client.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class TransactionListController extends SideBarController implements Initializable {
    public TableView<TransactionTableElement> transactionTableView;
    public TableColumn<TransactionTableElement, Timestamp> dateColumn;
    public TableColumn<TransactionTableElement, BigDecimal> totalPriceColumn;
    public Button homeButton, searchButton, infoButton;
    public SplitPane splitPane;

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


        setTableItems();

        transactionTableView.setRowFactory(param -> {
            TableRow<TransactionTableElement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Transaction item = new Transaction(row.getItem());
                    switch (getCurrentView()) {
                        case NONE:
                            showInfoAbout(item.getId());
                            break;
                        case INFO_TRANSACTION:
                            ((InfoAboutTransactionController) getCurrentController()).showInfoAbout(item.getId());
                            break;
                    }
                }
            });
            return row;
        });

        totalPriceColumn.setCellFactory(TableColumnFormatter.priceFormat());
        dateColumn.setCellFactory(TableColumnFormatter.dateFormat());

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_TRANSACTION, searchButton);
        getButtonMap().put(MenuView.INFO_TRANSACTION, infoButton);

        setSplitPane(splitPane);

        Utils.setTableColumnWidthProperty(transactionTableView, getPreferences());
    }

    private void setTableItems() {
        TransactionList.getInstance().init();

        TransactionList.getInstance().setFilteredTransactionList(new FilteredList<>(FXCollections.observableList(
                TransactionList.getInstance().getTransactionList().stream().map(TransactionTableElement::new)
                        .sorted().collect(Collectors.toList())), transactionTableElement -> true));

        SortedList<TransactionTableElement> sortedList
                = new SortedList<>(TransactionList.getInstance().getFilteredTransactionList());
        sortedList.comparatorProperty().bind(transactionTableView.comparatorProperty());

        transactionTableView.setItems(sortedList);

        TransactionList.getInstance().addListener(this::setTableItems);
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
