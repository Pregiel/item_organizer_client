package item_organizer_client.model.element;

import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Icon;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ItemTableElement extends Item {
    private HBox actionButtons;
    private Price sellPrice, buyPrice;

    public ItemTableElement(Item item) {
        super(item);

        for (Price itemPrice : getPrices()) {
            if (itemPrice.getType() == PriceType.SELL) {
                if (sellPrice == null) {
                    sellPrice = itemPrice;
                } else if (itemPrice.getId() > sellPrice.getId()) {
                    sellPrice = itemPrice;
                }
            } else {
                if (buyPrice == null) {
                    buyPrice = itemPrice;
                } else if (itemPrice.getId() > buyPrice.getId()) {
                    buyPrice = itemPrice;
                }
            }
        }

        Button editButton = Icon.createIconButton(Icon.createSVGIcon(Icon.IconPath.ELEMENT_EDIT, "#ffae00", "#e29a00"));
        editButton.setOnAction((event) -> ItemListController.getInstance().showEditView(new Item(this)));

        Button moreInfoButton = Icon.createIconButton(Icon.createSVGIcon(Icon.IconPath.ELEMENT_INFO, "#3f59ea", "#26368e"));
        moreInfoButton.setOnAction((event) -> ItemListController.getInstance().showInfoView(new Item(this)));

        Button buyButton = Icon.createIconButton(Icon.createSVGIcon(Icon.IconPath.ELEMENT_BUY, "#00c3e5", "#00a5c1"));
        buyButton.setOnAction((event) -> ItemListController.getInstance().showBuyView(new Item(this)));

        Button sellButton = Icon.createIconButton(Icon.createSVGIcon(Icon.IconPath.ELEMENT_SELL, "#00cc03", "#00aa02"));
        sellButton.setOnAction((event) -> ItemListController.getInstance().showSellView(new Item(this)));

        Button hideButton = Icon.createIconButton(Icon.createSVGIcon(Icon.IconPath.ELEMENT_HIDE, "#5b5b5b", "#303030"));
        hideButton.setOnAction((event) -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Hey!");
            alert.setHeaderText(null);
            alert.setContentText("Item hidden");
            alert.showAndWait();
        });

        actionButtons = new HBox(editButton, moreInfoButton, buyButton, sellButton, hideButton);
        actionButtons.setSpacing(2);
    }

    public HBox getActionButtons() {
        return actionButtons;
    }

    public void setActionButtons(HBox actionButtons) {
        this.actionButtons = actionButtons;
    }

    public Price getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Price sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Price getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Price buyPrice) {
        this.buyPrice = buyPrice;
    }
}
