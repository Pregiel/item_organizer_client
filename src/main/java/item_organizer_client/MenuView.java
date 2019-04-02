package item_organizer_client;

public enum MenuView {
    NONE, SEARCH, ADD, BUY, SELL, INFO;

    @Override
    public String toString() {
        switch (this) {
            case SEARCH:
                return "SEARCH";
            case ADD:
                return "ADD";
            case BUY:
                return "BUY";
            case SELL:
                return "SELL";
            case INFO:
                return "INFO";
        }
        return "NONE";
    }
}
