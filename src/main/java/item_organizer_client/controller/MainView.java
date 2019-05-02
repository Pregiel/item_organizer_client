package item_organizer_client.controller;

public enum MainView {
    ITEM_LIST, TRANSACTION_LIST;

    private static final String ITEM_LIST_FXML = "/layout/ItemListLayout.fxml";
    private static final String TRANSACTION_LIST_FXML = "/layout/TransactionListLayout.fxml";

    public String getView() {
        switch (this) {
            case ITEM_LIST:
                return ITEM_LIST_FXML;
            case TRANSACTION_LIST:
                return TRANSACTION_LIST_FXML;
        }
        return "";
    }
}
