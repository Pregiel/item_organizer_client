package item_organizer_client.model.table_item;

import item_organizer_client.controller.transaction_list.TransactionListController;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;

public class TransactionTableElement extends Transaction implements Comparable<TransactionTableElement> {
    private HBox actionButtons;

    private String totalPrice;

    public TransactionTableElement(Transaction transaction) {
        super(transaction);

        BigDecimal price = BigDecimal.ZERO;

        for (TransactionItem transactionItem : transaction.getTransactionItems()) {
            price = price.add(transactionItem.getPrice().getValue().multiply(BigDecimal.valueOf(transactionItem.getAmount())));
        }

        totalPrice = Price.priceFormat(price);

        Button moreInfoButton = new Button("I");
        moreInfoButton.setOnAction((event) -> {
            TransactionListController.getInstance().showInfoAbout(getId());
        });

        actionButtons = new HBox(moreInfoButton);
        actionButtons.setSpacing(2);
    }

    public HBox getActionButtons() {
        return actionButtons;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    @Override
    public int compareTo(TransactionTableElement o) {
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
