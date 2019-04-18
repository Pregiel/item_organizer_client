package item_organizer_client.model;

import item_organizer_client.model.type.TransactionType;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Set;

@Entity
@Table(name = "transaction_table")
public class Transaction implements Comparable<Transaction>{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_transaction")
    @SequenceGenerator(name = "id_transaction", sequenceName = "ID_TRA", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @CreationTimestamp
    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 5)
    private TransactionType type;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "transaction")
    private Set<TransactionItem> transactionItems;

    public Transaction() {
    }

    public Transaction(Timestamp date, TransactionType type) {
        this.date = date;
        this.type = type;
    }

    public Transaction(Transaction transaction) {
        this.id = transaction.getId();
        this.date = transaction.getDate();
        this.type = transaction.getType();
        this.transactionItems = transaction.getTransactionItems();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Set<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(Set<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }

    @Override
    public int compareTo(Transaction o) {
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
