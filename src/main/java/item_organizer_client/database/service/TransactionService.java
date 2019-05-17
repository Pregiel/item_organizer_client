package item_organizer_client.database.service;

import item_organizer_client.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Transaction add(Transaction transaction);

    Transaction findById(int id);

    List<Transaction> getAll();

    List<String> getAllIDs();

    List<Transaction> getAllAfterDate(LocalDateTime dateTime);

    List<Transaction> getAllBetweenDates(LocalDateTime dateTo, LocalDateTime dateFrom);
}
