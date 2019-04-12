package item_organizer_client.model.table_item;

import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class ItemTableItem extends Item {

    private HBox actionButtons;
    private Price price;

    public ItemTableItem(Item item) {
        super(item);

        for (Price itemPrice : getPrices()) {
            if (itemPrice.getType() == PriceType.SELL) {
                if (price == null) {
                    price = itemPrice;
                } else if (itemPrice.getDate().after(price.getDate())) {
                    price = itemPrice;
                }
            }
        }

        Button editButton = new Button("E");
        editButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText("You're editing \"" + getId() + "\"");
            alert.showAndWait();
        });

        Button moreInfoButton = new Button("I");
        moreInfoButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText(toString());
            alert.showAndWait();
        });

        Button hideButton = new Button("H");
        hideButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText("Item hidden");
            alert.showAndWait();
        });

        Button qrButton = new Button("QR");
        qrButton.setOnAction((event) -> {
            Image image = new Image(getClass().getResource("/qrcode.jpg").toExternalForm());
            ImageView imageView = new ImageView(image);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setGraphic(imageView);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.showAndWait();
        });

        actionButtons = new HBox(editButton, moreInfoButton, hideButton, qrButton);
        actionButtons.setSpacing(2);
    }

    public HBox getActionButtons() {
        return actionButtons;
    }

    public void setActionButtons(HBox actionButtons) {
        this.actionButtons = actionButtons;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }
}
