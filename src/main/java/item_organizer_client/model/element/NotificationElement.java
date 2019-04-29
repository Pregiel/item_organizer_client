package item_organizer_client.model.element;

import item_organizer_client.model.Item;
import item_organizer_client.model.list.NotificationList;

public class NotificationElement {
    private NotificationList.NotificationType type;
    private NotificationList.NotificationTag tag;
    private String message;
    private Runnable onClick;
    private Item item;

    public NotificationElement(Item item, NotificationList.NotificationType type, NotificationList.NotificationTag tag, String message, Runnable onClick) {
        this.item = item;
        this.type = type;
        this.tag = tag;
        this.message = message;
        this.onClick = onClick;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public NotificationList.NotificationType getType() {
        return type;
    }

    public void setType(NotificationList.NotificationType type) {
        this.type = type;
    }

    public NotificationList.NotificationTag getTag() {
        return tag;
    }

    public void setTag(NotificationList.NotificationTag tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }
}
