package item_organizer_client.model;


import item_organizer_client.utils.Utils;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "item")
public class Item implements Comparable<Item> {
    public static final int ID_DIGITS = 4;
    public static final int INITIAL_AMOUNT_VALUE = 1;
    public static final int INITIAL_SAFE_AMOUNT_VALUE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "id_item")
    @SequenceGenerator(name = "id_item", sequenceName = "ID_ITE")
    @Column(name = "id")
    private Integer id;

    @Column(name = "number", nullable = false, unique = true)
    private Integer number;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "safe_amount")
    private Integer safeAmount;

    @Column(name = "hidden")
    private Boolean hidden;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private Set<Price> prices;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "item")
    private Set<TransactionItem> transactionItems;

    public Item() {
    }

    public Item(Integer number, String name, Category category, Integer amount, Integer safeAmount) {
        this.number = number;
        this.name = name;
        this.category = category;
        this.amount = amount;
        this.safeAmount = safeAmount;
        this.hidden = false;
    }

    public Item(Item item) {
        this.id = item.id;
        this.number = item.number;
        this.name = item.name;
        this.category = item.category;
        this.amount = item.amount;
        this.safeAmount = item.safeAmount;
        this.hidden = item.hidden;
        this.prices = item.prices;
        this.transactionItems = item.transactionItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
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

    public Integer getSafeAmount() {
        return safeAmount;
    }

    public void setSafeAmount(Integer safeAmount) {
        this.safeAmount = safeAmount;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
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
        return Utils.fillWithZeros(number, 4) + ". " + name;
    }

    @Override
    public int compareTo(Item o) {
        if (getNumber() == null || o.getNumber() == null) {
            return 0;
        }
        return getNumber().compareTo(o.getNumber());
    }
}
