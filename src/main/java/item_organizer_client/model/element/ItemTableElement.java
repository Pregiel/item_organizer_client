package item_organizer_client.model.element;

import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Icon;
import item_organizer_client.utils.IconGraphic;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
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

        Button editButton = Icon.createIconButton(Icon.createSVGIcon(IconGraphic.ELEMENT_EDIT));
        editButton.setOnAction((event) -> ItemListController.getInstance().showEditView(new Item(this)));

        Button moreInfoButton = Icon.createIconButton(Icon.createSVGIcon(IconGraphic.ELEMENT_INFO));
        moreInfoButton.setOnAction((event) -> ItemListController.getInstance().showInfoView(new Item(this)));

        Button buyButton = Icon.createIconButton(Icon.createSVGIcon(IconGraphic.ELEMENT_BUY));
        buyButton.setOnAction((event) -> ItemListController.getInstance().showBuyView(new Item(this)));

        Button sellButton = Icon.createIconButton(Icon.createSVGIcon(IconGraphic.ELEMENT_SELL));
        sellButton.setOnAction((event) -> ItemListController.getInstance().showSellView(new Item(this)));

        Button hideButton = Icon.createIconButton(Icon.createSVGIcon(IconGraphic.ELEMENT_HIDE));
        hideButton.setOnAction((event) -> {
            if (getHidden()) {
                setHidden(false);
                ItemList.getInstance().updateItem(new Item(this));
            } else {
                MyAlerts.showConfirmationDialog(
                        Utils.getString("alert.confirm.hide.title"),
                        Utils.getString("alert.confirm.hide", item.toTitle()),
                        () -> {
                            setHidden(true);
                            ItemList.getInstance().updateItem(new Item(this));
                        });
            }
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
