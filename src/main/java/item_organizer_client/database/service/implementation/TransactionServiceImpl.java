package item_organizer_client.database.service.implementation;

import item_organizer_client.database.Repository;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private Repository<Transaction> transactionRepository;

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
}
