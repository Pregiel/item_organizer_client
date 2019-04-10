package item_organizer_client.database.service.implementation;

import item_organizer_client.database.Repository;
import item_organizer_client.database.service.TransactionItemService;
import item_organizer_client.model.TransactionItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionItemServiceImpl implements TransactionItemService {
    @Autowired
    private Repository<TransactionItem> transactionItemRepository;

    @Override
    public TransactionItem add(TransactionItem transactionItem) {
        return transactionItemRepository.add(transactionItem);
    }

    @Override
    public List<TransactionItem> addAll(TransactionItem... transactionItems) {
        return transactionItemRepository.addAll(transactionItems);
    }

    @Override
    public TransactionItem findById(int id) {
        return transactionItemRepository.findById(TransactionItem.class, id);
    }

    @Override
    public List<TransactionItem> getAll() {
        return transactionItemRepository.getAll(TransactionItem.class);
    }

}
