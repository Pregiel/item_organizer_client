package item_organizer_client.database.service.implementation;

import item_organizer_client.database.QueryParameter;
import item_organizer_client.database.Repository;
import item_organizer_client.database.repository.TransactionRepository;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Transaction add(Transaction transaction) {
        return transactionRepository.add(transaction);
    }

    @Override
    public Transaction findById(int id) {
        return transactionRepository.findById(Transaction.class, id);
    }

    @Override
    public List<Transaction> getAll() {
        return transactionRepository.getAll(Transaction.class);
    }

    @Override
    public List<String> getAllIDs() {
        return transactionRepository.getAllColumn(Transaction.class, "id");
    }

    @Override
    public List<Transaction> getAllAfterDate(LocalDateTime dateTime) {
        return transactionRepository.findByQuery(
                "FROM Transaction t " +
                        "WHERE t.date > :date ",
                new QueryParameter<>("date", Timestamp.valueOf(dateTime)));
    }

    @Override
    public List<Transaction> getAllBetweenDates(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return transactionRepository.findByQuery(
                "FROM Transaction t " +
                        "WHERE t.date > :dateFrom AND t.date < :dateTo",
                new QueryParameter<>("dateFrom", Timestamp.valueOf(dateFrom)),
                new QueryParameter<>("dateTo", Timestamp.valueOf(dateTo)));
    }
}
