package item_organizer_client.model.list;

import item_organizer_client.controller.transaction_list.SearchTransactionItemController;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.element.TransactionTableElement;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.Utils;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

@Component
public class TransactionList {
    @Autowired
    private TransactionService transactionService;

    private ObservableList<Transaction> transactionList;
    private FilteredList<TransactionTableElement> filteredTransactionList;
    private ListChangeListener<Transaction> listChangeListener;

    private static TransactionList instance;

    public static TransactionList getInstance() {
        if (instance == null) {
            instance = new TransactionList();
            instance.init();
        }
        return instance;
    }

    public TransactionList() {
        instance = this;
    }

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

    public void init() {
        transactionList = FXCollections.observableList(transactionService.getAll());
    }

    public void refresh() {
        transactionList.clear();
        transactionList.addAll(transactionService.getAll());
    }

    public void addListener(Runnable runnable) {
        removeListener();
        listChangeListener = c -> runnable.run();
        transactionList.addListener(listChangeListener);
    }

    private void removeListener() {
        if (listChangeListener != null) {
            transactionList.removeListener(listChangeListener);
        }
        listChangeListener = null;
    }

    public FilteredList<TransactionTableElement> getFilteredTransactionList() {
        return filteredTransactionList;
    }

    public void setFilteredTransactionList(FilteredList<TransactionTableElement> filteredTransactionList) {
        this.filteredTransactionList = filteredTransactionList;
    }

    public void setUpSearchViewFilters(TextField idText, TextField priceFromText, TextField priceToText,
                                       ToggleGroup transactionGroup, DateTimePicker dateFromText, DateTimePicker dateToText,
                                       SimpleListProperty<Item> transactionItemList) {

        ObjectProperty<Predicate<TransactionTableElement>> idFilter = new SimpleObjectProperty<>();
        idFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            if (idText.getText().equals("")) {
                return true;
            } else if (idText.getText().matches("\\d*"))
                return transaction.getId().toString().contains(idText.getText());
            return false;
        }, idText.textProperty()));

        ObjectProperty<Predicate<TransactionTableElement>> priceFromFilter = new SimpleObjectProperty<>();
        priceFromFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            if (priceFromText.getText().equals("")) {
                return true;
            } else if (priceFromText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return transaction.getTotalPrice().compareTo(new BigDecimal(priceFromText.getText())) >= 0;
            return false;
        }, priceFromText.textProperty()));

        ObjectProperty<Predicate<TransactionTableElement>> transactionGroupFilter = new SimpleObjectProperty<>();
        transactionGroupFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            if (transactionGroup.getSelectedToggle() != null) {
                if (transactionGroup.getSelectedToggle().getUserData() != null) {
                    return transaction.getType().equals(transactionGroup.getSelectedToggle().getUserData());
                }
            }
            return true;
        }, transactionGroup.selectedToggleProperty()));

        ObjectProperty<Predicate<TransactionTableElement>> priceToFilter = new SimpleObjectProperty<>();
        priceToFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            if (priceToText.getText().equals("")) {
                return true;
            } else if (priceToText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return transaction.getTotalPrice().compareTo(new BigDecimal(priceToText.getText())) <= 0;
            return false;
        }, priceToText.textProperty()));


        ObjectProperty<Predicate<TransactionTableElement>> dateFromFilter = new SimpleObjectProperty<>();
        dateFromFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            try {
                if (dateFromText.getEditor().getText().equals("")) {
                    return true;
                } else if (dateFromText.getEditor().getText().matches(Utils.dateTimeRegex + ".*")) {
                    Timestamp date = Timestamp.valueOf(LocalDateTime.parse
                            (dateFromText.getEditor().getText().substring(0, 16), Utils.getDateTimeFormatter()));
                    return transaction.getDate().compareTo(date) >= 0;
                } else if (dateFromText.getEditor().getText().matches(Utils.dateRegex + ".*")) {
                    Timestamp date = Timestamp.valueOf(LocalDateTime.parse
                            (dateFromText.getEditor().getText().substring(0, 10).concat(" 00:00"), Utils.getDateTimeFormatter()));
                    return transaction.getDate().compareTo(date) >= 0;
                }
            } catch (Exception ex) {
                return true;
            }
            return true;
        }, dateFromText.getEditor().textProperty()));

        ObjectProperty<Predicate<TransactionTableElement>> dateToFilter = new SimpleObjectProperty<>();
        dateToFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            try {
                if (dateToText.getEditor().getText().equals("")) {
                    return true;
                } else if (dateToText.getEditor().getText().matches(Utils.dateTimeRegex + ".*")) {
                    Timestamp date = Timestamp.valueOf(LocalDateTime.parse
                            (dateToText.getEditor().getText().substring(0, 16), Utils.getDateTimeFormatter()));
                    return transaction.getDate().compareTo(date) <= 0;
                } else if (dateToText.getEditor().getText().matches(Utils.dateRegex + ".*")) {
                    Timestamp date = Timestamp.valueOf(LocalDateTime.parse
                            (dateToText.getEditor().getText().substring(0, 10).concat(" 23:59"), Utils.getDateTimeFormatter()));
                    return transaction.getDate().compareTo(date) <= 0;
                }
            } catch (Exception ex) {
                return true;
            }
            return true;
        }, dateToText.getEditor().textProperty()));

        ObjectProperty<Predicate<TransactionTableElement>> transactionItemListFilter = new SimpleObjectProperty<>();
        transactionItemListFilter.bind(Bindings.createObjectBinding(() -> transaction -> {
            if (transactionItemList.size() > 0) {
                for (Item item : transactionItemList) {
                    for (TransactionItem transactionItem : transaction.getTransactionItems()) {
                        if (transactionItem.getItem().getId().equals(item.getId())) {
                            return true;
                        }
                    }
                }
                return false;
            }
            return true;
        }, transactionItemList.sizeProperty()));

        filteredTransactionList.predicateProperty().unbind();
        filteredTransactionList.predicateProperty().bind(Bindings.createObjectBinding(() -> idFilter.get()
                        .and(priceFromFilter.get()).and(priceToFilter.get()).and(transactionGroupFilter.get())
                        .and(dateFromFilter.get()).and(dateToFilter.get()).and(transactionItemListFilter.get()),
                idFilter, priceFromFilter, priceToFilter, transactionGroupFilter, dateFromFilter, dateToFilter, transactionItemListFilter));
    }
}
