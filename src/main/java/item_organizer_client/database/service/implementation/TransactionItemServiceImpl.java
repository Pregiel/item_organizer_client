package item_organizer_client.database.service.implementation;

import item_organizer_client.database.QueryParameter;
import item_organizer_client.database.Repository;
import item_organizer_client.database.service.TransactionItemService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
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
    public List<TransactionItem> addAll(List<TransactionItem> transactionItemList) {
        return transactionItemRepository.addAll(transactionItemList);
    }

    @Override
    public TransactionItem findByItemTransactionPrice(Item item, Transaction transaction, Price price) {
        List<TransactionItem> list = transactionItemRepository.findByQuery(
                "FROM TransactionItem p " +
                        "WHERE p.item = :item_id AND p.transaction = :transaction_id AND p.price = :price_id ",
                new QueryParameter<>("item_id", item),
                new QueryParameter<>("transaction_id", transaction),
                new QueryParameter<>("price_id", price));

        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<TransactionItem> getAll() {
        return transactionItemRepository.getAll(TransactionItem.class);
    }

}
