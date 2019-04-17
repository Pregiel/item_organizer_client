package item_organizer_client.database.service;

import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;

import java.util.List;

public interface TransactionItemService {
    TransactionItem add(TransactionItem transactionItem);

    List<TransactionItem> addAll(TransactionItem... transactionItems);

    List<TransactionItem> addAll(List<TransactionItem> transactionItemList);

    TransactionItem findByItemTransactionPrice(Item item, Transaction transaction, Price price);

    List<TransactionItem> getAll();
}
