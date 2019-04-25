package item_organizer_client.model.list;

import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.element.TransactionTableElement;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionList {
    @Autowired
    private TransactionService transactionService;

    private ObservableList<Transaction> transactionList;
    private ListChangeListener<Transaction> listChangeListener;

    public List<Transaction> getTransactionList() {
        return transactionList;
    }

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

    public List<TransactionTableElement> getItemListAsTableItems() {
        return transactionList
                .stream()
                .map(TransactionTableElement::new)
                .sorted()
                .collect(Collectors.toList());
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
}
