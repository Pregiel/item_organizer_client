package item_organizer_client.database.service;

import item_organizer_client.model.TransactionItem;

import java.util.List;

public interface TransactionItemService {
    TransactionItem add(TransactionItem transactionItem);

    List<TransactionItem> addAll(TransactionItem... transactionItems);

    List<TransactionItem> addAll(List<TransactionItem> transactionItemList);

    TransactionItem findById(int id);

    List<TransactionItem> getAll();
}
