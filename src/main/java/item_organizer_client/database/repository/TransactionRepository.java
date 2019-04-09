package item_organizer_client.database.repository;

import item_organizer_client.model.Transaction;

import java.util.List;

public class TransactionRepository extends Repository {
    public static Transaction add(Transaction transaction) {
        return Repository.add(transaction);
    }

    public static Transaction findById(int id) {
        return findById(Transaction.class, id);
    }

    public static List<Transaction> getAll() {
        return getAll(Transaction.class);
    }

}
