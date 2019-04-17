package item_organizer_client.model.table_item;

import item_organizer_client.model.Price;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.Utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionItemInfoItemTableElement extends TransactionItem implements Comparable<TransactionItemInfoItemTableElement> {
    private Integer id;
    private String totalPrice, date;
    private TransactionType type;

    public TransactionItemInfoItemTableElement(TransactionItem transactionItem) {
        super(transactionItem);

        id = transactionItem.getTransaction().getId();
        totalPrice = Price.priceFormat(getPrice().getValue().multiply(BigDecimal.valueOf(getAmount())));
        date = transactionItem.getTransaction().getDate().toLocalDateTime().format(Utils.getDateFormatter());
        type = transactionItem.getTransaction().getType();
    }

    public Integer getId() {
        return id;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }

    @Override
    public int compareTo(TransactionItemInfoItemTableElement o) {
        if (getDate() == null || o.getDate() == null) {
            return 0;
        } else if (getDate().compareTo(o.getDate()) == 0) {
            if (getId() == null || o.getId() == null) {
                return 0;
            }
            return getId().compareTo(o.getId());
        }
        return getDate().compareTo(o.getDate());
    }
}
