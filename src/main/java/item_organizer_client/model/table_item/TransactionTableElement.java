package item_organizer_client.model.table_item;

import item_organizer_client.controller.transaction_list.TransactionListController;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;

public class TransactionTableElement extends Transaction {
    private HBox actionButtons;

    private BigDecimal totalPrice;

    public TransactionTableElement(Transaction transaction) {
        super(transaction);

        totalPrice = BigDecimal.ZERO;

        for (TransactionItem transactionItem : transaction.getTransactionItems()) {
            totalPrice = totalPrice.add(transactionItem.getPrice().getValue().multiply(BigDecimal.valueOf(transactionItem.getAmount())));
        }

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

}
