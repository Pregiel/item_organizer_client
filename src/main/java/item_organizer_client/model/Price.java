package item_organizer_client.model;

import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.Set;

@Entity
@Table(name = "price")
public class Price {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_price")
    @SequenceGenerator(name = "id_price", sequenceName = "ID_PRI")
    @Column(name = "id")
    private Integer id;

    @Column(name = "value", nullable = false)
    private Double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 5)
    private PriceType type;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @OneToMany(mappedBy = "price")
    private Set<TransactionItem> transactionItems;

    @Column(name = "date")
    private Timestamp date;

    public Price() {
    }

    public Price(Double value, PriceType type, Item item, Timestamp date) {
        this.value = value;
        this.type = type;
        this.item = item;
        this.date = date;
    }

    public Price(Price price) {
        this.id = price.getId();
        this.value = price.getValue();
        this.type = price.getType();
        this.item = price.getItem();
        this.transactionItems = price.getTransactionItems();
        this.date = price.getDate();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public PriceType getType() {
        return type;
    }

    public void setType(PriceType type) {
        this.type = type;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Set<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(Set<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return value.toString() + " " + ResourceBundle.getBundle("strings").getString("price.currency");
    }
}
