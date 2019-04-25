package item_organizer_client.model.element;

import item_organizer_client.model.list.NotificationList;

public class NotificationElement {
    private NotificationList.NotificationType type;
    private NotificationList.NotificationTag tag;
    private String message;
    private Runnable onClick;

    public NotificationElement(NotificationList.NotificationType type, NotificationList.NotificationTag tag, String message, Runnable onClick) {
        this.type = type;
        this.tag = tag;
        this.message = message;
        this.onClick = onClick;
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
