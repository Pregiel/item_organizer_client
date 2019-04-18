package item_organizer_client.model.table_item;

import item_organizer_client.model.Category;
import item_organizer_client.model.Price;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.Utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionItemInfoTransactionTableElement extends TransactionItem {
    private String id, name, pricePerItem, totalPrice, category;

    public TransactionItemInfoTransactionTableElement(TransactionItem transactionItem) {
        super(transactionItem);

        name = transactionItem.getItem().getName();
        id = Utils.fillWithZeros(transactionItem.getItem().getId(), 4);
        pricePerItem = transactionItem.getPrice().toString();
        totalPrice = Price.priceFormat(getPrice().getValue().multiply(BigDecimal.valueOf(getAmount())));
        category = transactionItem.getItem().getCategory().getName();
    }

    public String getName() {
        return name;
    }

    public String getPricePerItem() {
        return pricePerItem;
    }

    public String getId() {
        return id;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public int compareTo(TransactionItem o) {
        if (getItem().getId() == null || o.getItem().getId() == null) {
            return 0;
        } else if (getItem().getId().compareTo(o.getItem().getId()) == 0) {
            if (getAmount() == null || o.getAmount() == null) {
                return 0;
            } else if (getAmount().compareTo(o.getAmount()) == 0) {
                if (getPrice() == null || o.getPrice() == null) {
                    return 0;
                }
                return getPrice().compareTo(o.getPrice());
            }
            return getAmount().compareTo(o.getAmount());
        }
        return getItem().getId().compareTo(o.getItem().getId());
    }
}
