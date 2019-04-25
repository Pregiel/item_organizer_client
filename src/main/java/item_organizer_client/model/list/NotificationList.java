package item_organizer_client.model.list;

import item_organizer_client.controller.NotificationController;
import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.element.NotificationElement;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Component
public class NotificationList {
    @Autowired
    private PriceService priceService;

    public enum NotificationType {
        WARNING, DANGER, INFO;

        public String getStyle() {
            return getStyle(0);
        }

        public String getStyle(int index) {
            switch (this) {
                case WARNING:
                    return (index % 2 == 0) ? "notification-warning" : "notification-warning-odd";
                case DANGER:
                    return (index % 2 == 0) ? "notification-danger" : "notification-danger-odd";
                case INFO:
                    return (index % 2 == 0) ? "notification-info" : "notification-info-odd";
            }
            return "";
        }
    }

    public enum NotificationTag {
        ITEM_AMOUNT, ITEM_PRICE;

        public String toText() {
            switch (this) {
                case ITEM_AMOUNT:
                    return Utils.getString("notification.tag.itemAmount");
                case ITEM_PRICE:
                    return Utils.getString("notification.tag.itemPrice");
            }
            return "";
        }
    }

    private static NotificationList instance;

    public static NotificationList getInstance() {
        if (instance == null) {
            instance = new NotificationList();
            instance.init();
        }
        return instance;
    }

    private ObservableList<NotificationElement> notificationList;

    private NotificationController notificationController;

    private Label notificationCount;

    public void init() {
        notificationList = FXCollections.observableArrayList();
        notificationList.addListener((ListChangeListener<NotificationElement>) c -> {
            if (notificationCount != null) {
                notificationCount.setText(String.valueOf(notificationList.size()));
            }

            if (notificationController != null) {
                notificationController.refresh();
            }
        });
    }

    public void check() {
        if (notificationList == null) {
            init();
        }

        if (notificationController != null) {
            notificationController.clearAll(null);
        } else {
            notificationList.clear();
        }

        for (Item item : ItemList.getInstance().getItemList()) {
            if (item.getAmount().compareTo(item.getSafeAmount()) < 0) {
                if (item.getAmount().compareTo(0) > 0) {
                    notificationList.add(new NotificationElement(
                            NotificationType.WARNING,
                            NotificationTag.ITEM_AMOUNT,
                            Utils.getString("notification.amountLessThanSafe", item.toTitle()),
                            () -> ItemListController.getInstance().showInfoAbout(item.getId())));
                } else {
                    notificationList.add(new NotificationElement(
                            NotificationType.DANGER,
                            NotificationTag.ITEM_AMOUNT,
                            Utils.getString("notification.amountOut", item.toTitle()),
                            () -> ItemListController.getInstance().showInfoAbout(item.getId())));
                }
            }

            BigDecimal buyPrice = item.getPrices().stream().filter(price -> price.getType().equals(PriceType.BUY))
                    .sorted(Comparator.comparing(Price::getDate).reversed()).collect(Collectors.toList()).get(0).getValue();

            BigDecimal sellPrice = item.getPrices().stream().filter(price -> price.getType().equals(PriceType.SELL))
                    .sorted(Comparator.comparing(Price::getDate).reversed()).collect(Collectors.toList()).get(0).getValue();

            if (buyPrice != null && sellPrice != null) {
                if (sellPrice.compareTo(buyPrice) < 0) {
                    notificationList.add(new NotificationElement(
                            NotificationType.INFO,
                            NotificationTag.ITEM_PRICE,
                            Utils.getString("notification.sellSmallerThanBuy", item.toTitle()),
                            () -> ItemListController.getInstance().showInfoAbout(item.getId())));
                }
            }
        }
    }

    public ObservableList<NotificationElement> getNotificationList() {
        return notificationList;
    }

    public void setNotificationController(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    public void setNotificationCount(Label notificationCount) {
        this.notificationCount = notificationCount;
        if (notificationList != null) {
            notificationCount.setText(String.valueOf(notificationList.size()));
        }
    }
}
