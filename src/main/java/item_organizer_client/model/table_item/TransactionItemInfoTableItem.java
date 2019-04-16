package item_organizer_client.model.table_item;

import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.type.TransactionType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionItemInfoTableItem extends TransactionItem {
    private Integer id;
    private String totalPrice;
    private Timestamp date;
    private TransactionType type;

    public TransactionItemInfoTableItem(TransactionItem transactionItem) {
        super(transactionItem);

        id = transactionItem.getTransaction().getId();

        totalPrice = Price.priceFormat(getPrice().getValue().multiply(BigDecimal.valueOf(getAmount())));
        date = transactionItem.getTransaction().getDate();
        type = transactionItem.getTransaction().getType();
    }

    public Integer getId() {
        return id;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public Timestamp getDate() {
        return date;
    }

    public TransactionType getType() {
        return type;
    }
}
