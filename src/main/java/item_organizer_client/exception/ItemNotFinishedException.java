package item_organizer_client.exception;

public class ItemNotFinishedException extends Exception {
    public ItemNotFinishedException(String message) {
        super(message);
    }

    public ItemNotFinishedException() {
        super();
    }
}
