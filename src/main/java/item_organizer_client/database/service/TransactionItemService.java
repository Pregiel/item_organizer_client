package item_organizer_client.database.service;

import item_organizer_client.model.TransactionItem;

import java.util.List;

public interface TransactionItemService {
    TransactionItem add(TransactionItem transactionItem);

    List<TransactionItem> addAll(TransactionItem... transactionItems);

    TransactionItem findById(int id);

    List<TransactionItem> getAll();
}
