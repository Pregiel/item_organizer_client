package item_organizer_client.model.table_item;

import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;

public class TransactionTableItem extends Transaction {
    private HBox actionButtons;

    private String totalPrice;

    public TransactionTableItem(Transaction transaction) {
        super(transaction);

        BigDecimal price = BigDecimal.ZERO;

        for (TransactionItem transactionItem : transaction.getTransactionItems()) {
            price = price.add(transactionItem.getPrice().getValue().multiply(BigDecimal.valueOf(transactionItem.getAmount())));
        }

        totalPrice = Price.priceFormat(price);

        Button moreInfoButton = new Button("I");
        moreInfoButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText(toString());
            alert.showAndWait();
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
}
