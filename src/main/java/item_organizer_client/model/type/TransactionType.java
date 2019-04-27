package item_organizer_client.model.type;

public enum TransactionType {
    BUY, SELL;

    @Override
    public String toString() {
        switch (this) {
            case BUY:
                return "BUY";
            case SELL:
                return "SELL";
            default:
                return "NONE";
        }
    }
}
