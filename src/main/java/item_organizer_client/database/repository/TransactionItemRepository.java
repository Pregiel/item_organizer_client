package item_organizer_client.database.repository;


import item_organizer_client.model.TransactionItem;

import java.util.List;

public class TransactionItemRepository extends Repository {
    public static TransactionItem add(TransactionItem transactionItem) {
        return Repository.add(transactionItem);
    }

    public static TransactionItem findById(int id) {
        return findById(TransactionItem.class, id);
    }

    public static List<TransactionItem> getAll() {
        return getAll(TransactionItem.class);
    }
}
