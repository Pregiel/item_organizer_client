package item_organizer_client.model;

import java.io.Serializable;
import java.util.Objects;

public class TransactionItemPK implements Serializable {
    protected Item item;

    protected Transaction transaction;

    protected Price price;

    public TransactionItemPK() {
    }

    public TransactionItemPK(Item item, Transaction transaction, Price price) {
        this.item = item;
        this.transaction = transaction;
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionItemPK that = (TransactionItemPK) o;
        return Objects.equals(item, that.item) &&
                Objects.equals(transaction, that.transaction) &&
                Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, transaction, price);
    }
}
