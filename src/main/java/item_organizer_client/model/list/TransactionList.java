package item_organizer_client.model.list;

import item_organizer_client.database.repository.ItemRepository;
import item_organizer_client.database.repository.TransactionRepository;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.table_item.ItemTableItem;
import item_organizer_client.model.table_item.TransactionTableItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionList {
    private static ObservableList<Transaction> transactionList;
    private static ListChangeListener<Transaction> listChangeListener;

    public static List<Transaction> getTransactionList() {
        return transactionList;
    }

    public static List<TransactionTableItem> getItemListAsTableItems() {
        return transactionList
                .stream()
                .map(TransactionTableItem::new)
                .collect(Collectors.toList());
    }

    public static void init() {
        transactionList = FXCollections.observableList(TransactionRepository.getAll());
    }

    public static void refresh() {
        transactionList.clear();
        transactionList.addAll(TransactionRepository.getAll());
    }

    public static void addListener(Runnable runnable) {
        removeListener();
        listChangeListener = c -> runnable.run();
        transactionList.addListener(listChangeListener);
    }

    private static void removeListener() {
        if (listChangeListener != null) {
            transactionList.removeListener(listChangeListener);
        }
        listChangeListener = null;
    }
}
