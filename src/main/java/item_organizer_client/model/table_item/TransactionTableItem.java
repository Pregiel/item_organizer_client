package item_organizer_client.model.table_item;

import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class TransactionTableItem extends Transaction {
    private HBox actionButtons;

    private Double price;

    public TransactionTableItem(Transaction transaction) {
        super(transaction);

        price = 0.0;

        for (TransactionItem transactionItem : transaction.getTransactionItems()) {
            price += transactionItem.getPrice().getValue();
        }

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

    public Double getPrice() {
        return price;
    }
}
