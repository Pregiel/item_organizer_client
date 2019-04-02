package item_organizer_client.model;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class ItemTableItem extends Item {
    private HBox actionButtons;

    public ItemTableItem(Item item) {
        super(item.getId(),
                item.getName(),
                item.getCategory(),
                item.getAmount(),
                item.getPrice());

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
}
