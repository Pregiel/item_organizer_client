package item_organizer_client.model;


import item_organizer_client.utils.Utils;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "item")
public class Item {
    public static final int ID_DIGITS = 4;

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "amount")
    private Integer amount;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private Set<Price> prices;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private Set<TransactionItem> transactionItems;

    public Item() {
    }

    public Item(Integer id, String name, Category category, Integer amount) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.amount = amount;
    }

    public Item(Item item) {
        this.id = item.id;
        this.name = item.name;
        this.category = item.category;
        this.amount = item.amount;
        this.prices = item.prices;
        this.transactionItems = item.transactionItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Set<Price> getPrices() {
        return prices;
    }

    public void setPrices(Set<Price> prices) {
        this.prices = prices;
    }

    public Set<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public void setTransactionItems(Set<TransactionItem> transactionItems) {
        this.transactionItems = transactionItems;
    }

    public String toTitle() {
        return Utils.fillWithZeros(id, 4) + ". " + name;
    }
}
