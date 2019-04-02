package item_organizer_client.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class Transaction {
    private String id;
    private String type;
    private String date;
    private String pricePerItem;
    private String amount;
    private String price;

    private HBox actionButtons;

    public Transaction(String id, String type, String date, String pricePerItem, String amount, String price) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.pricePerItem = pricePerItem;
        this.amount = amount;
        this.price = price;


        Button moreInfoButton = new Button("I");
        moreInfoButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText(toString());
            alert.showAndWait();
        });

        actionButtons = new HBox(moreInfoButton);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPricePerItem() {
        return pricePerItem;
    }

    public void setPricePerItem(String pricePerItem) {
        this.pricePerItem = pricePerItem;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public HBox getActionButtons() {
        return actionButtons;
    }
}
