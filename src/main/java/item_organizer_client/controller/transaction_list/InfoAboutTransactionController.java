package item_organizer_client.controller.transaction_list;

import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.element.TransactionItemInfoTransactionTableElement;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class InfoAboutTransactionController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private TransactionService transactionService;

    public VBox infoItemPane, detailsPane;
    public GridPane searchInputPane;
    public ComboBox<String> searchText;
    public Label idNotExistAlert, selectedTransactionId, selectedTransactionType, selectedTransactionDate,
            selectedTransactionPrice;
    public TableView<TransactionItemInfoTransactionTableElement> transactionItemsTable;

    private Transaction selectedTransaction;
    private FilteredList<TransactionItemInfoTransactionTableElement> transactionItemList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        Utils.setTableColumnWidthProperty(transactionItemsTable, getPreferences());

        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setIdComboBoxListeners(searchText, transactionService, searchText.getParent(), idNotExistAlert);
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(idNotExistAlert);
    }

    private void goToStep(int step) {
        clearAlerts();
        infoItemPane.getChildren().removeAll(searchInputPane, detailsPane);
        switch (step) {
            case 0:
                infoItemPane.getChildren().addAll(searchInputPane);
                break;
            case 1:
                infoItemPane.getChildren().addAll(detailsPane);

                selectedTransactionId.setText(String.valueOf(selectedTransaction.getId()));
                selectedTransactionType.setText(selectedTransaction.getType().toString());
                selectedTransactionDate.setText(selectedTransaction.getDate().toLocalDateTime().format(Utils.getDateFormatter()));

                BigDecimal price = BigDecimal.ZERO;
                for (TransactionItem transactionItem : selectedTransaction.getTransactionItems()) {
                    price = price.add(transactionItem.getPrice().getValue().multiply(
                            BigDecimal.valueOf(transactionItem.getAmount())));
                }
                selectedTransactionPrice.setText(Price.priceFormat(price));

                transactionItemList = new FilteredList<>(FXCollections.observableList(selectedTransaction
                        .getTransactionItems().stream().map(TransactionItemInfoTransactionTableElement::new).sorted()
                        .collect(Collectors.toList())), transactionItemInfoTransactionTableElement -> true);

                SortedList<TransactionItemInfoTransactionTableElement> transactionItemSortedList
                        = new SortedList<>(transactionItemList);
                transactionItemSortedList.comparatorProperty().bind(transactionItemsTable.comparatorProperty());

                transactionItemsTable.setItems(transactionItemSortedList);
                break;
        }
    }

    public void nextStepSearch(ActionEvent event) {
        try {
            selectedTransaction = transactionService.findById(Integer.parseInt(searchText.getEditor().getText()));

            if (selectedTransaction == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            ((Pane) searchText.getParent()).getChildren().add(idNotExistAlert);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            MyAlerts.showError("Niepoprawne ID", "Wprowadzono nie poprawny numer ID.");

        }
    }

    public void changeSelectedTransaction(ActionEvent event) {
        goToStep(0);
    }


    public void showInfoAbout(int id) {
        try {
            selectedTransaction = transactionService.findById(id);

            if (selectedTransaction == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            searchText.getEditor().setText(String.valueOf(id));
            MyAlerts.showError("Niepoprawne ID", "Wprowadzono nie poprawny numer ID.");
            ((Pane) searchText.getParent()).getChildren().add(idNotExistAlert);
        }
    }
}
