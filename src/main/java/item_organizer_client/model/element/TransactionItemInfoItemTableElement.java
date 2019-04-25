package item_organizer_client.model.element;

import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.type.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionItemInfoItemTableElement extends TransactionItem {
    private Integer id;
    private BigDecimal pricePerItem, totalPrice;
    private Timestamp date;
    private TransactionType type;

    public TransactionItemInfoItemTableElement(TransactionItem transactionItem) {
        super(transactionItem);

        id = transactionItem.getTransaction().getId();
        pricePerItem = getPrice().getValue();
        totalPrice = getPrice().getValue().multiply(BigDecimal.valueOf(getAmount()));
        date = transactionItem.getTransaction().getDate();
        type = transactionItem.getTransaction().getType();
    }

    public Integer getId() {
        return id;
    }

    public BigDecimal getPricePerItem() {
        return pricePerItem;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public Timestamp getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public int compareTo(TransactionItem o) {
        if (getDate() == null || o.getTransaction().getDate() == null) {
            return 0;
        } else if (getDate().compareTo(o.getTransaction().getDate()) == 0) {
            if (getId() == null || o.getTransaction().getId() == null) {
                return 0;
            } else if (getId().compareTo(o.getTransaction().getId()) == 0) {
                if (getPrice() == null || o.getPrice() == null) {
                    return 0;
                }
                return getPrice().compareTo(o.getPrice());
            }
            return getId().compareTo(o.getTransaction().getId());
        }
        return getDate().compareTo(o.getTransaction().getDate());
    }
}
