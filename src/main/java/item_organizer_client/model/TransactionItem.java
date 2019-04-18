package item_organizer_client.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "transaction_item")
@IdClass(TransactionItemPK.class)
public class TransactionItem implements Serializable, Comparable<TransactionItem> {
    @Id
    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Id
    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Id
    @ManyToOne
    @JoinColumn(name = "price_id", nullable = false)
    private Price price;

    @Column(name = "amount")
    private Integer amount;

    public TransactionItem() {
    }

    public TransactionItem(Item item, Transaction transaction, Price price, Integer amount) {
        this.item = item;
        this.transaction = transaction;
        this.price = price;
        this.amount = amount;
    }

    public TransactionItem(TransactionItem transactionItem) {
        this.item = transactionItem.getItem();
        this.transaction = transactionItem.getTransaction();
        this.price = transactionItem.getPrice();
        this.amount = transactionItem.getAmount();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(TransactionItem o) {
        if (getTransaction().getDate() == null || o.getTransaction().getDate() == null) {
            return 0;
        } else if (getTransaction().getDate().compareTo(o.getTransaction().getDate()) == 0) {
            if (getTransaction().getId() == null || o.getTransaction().getId() == null) {
                return 0;
            }
            return getTransaction().getId().compareTo(o.getTransaction().getId());
        }
        return getTransaction().getDate().compareTo(o.getTransaction().getDate());
    }
}
