package item_organizer_client.controller;

public enum MenuView {
    NONE, SEARCH_ITEM, ADD_ITEM, BUY_ITEM, SELL_ITEM, INFO_ITEM, EDIT_ITEM, SEARCH_TRANSACTION, INFO_TRANSACTION;

    private static final String ADD_ITEM_FXML = "/layout/AddItemLayout.fxml";
    private static final String BUY_ITEM_FXML = "/layout/BuyItemLayout.fxml";
    private static final String SELL_ITEM_FXML = "/layout/SellItemLayout.fxml";
    private static final String SEARCH_FXML = "/layout/SearchItemLayout.fxml";
    private static final String INFO_ITEM_FXML = "/layout/InfoAboutItemLayout.fxml";
    private static final String EDIT_ITEM_FXML = "/layout/EditItemLayout.fxml";
    private static final String SEARCH_TRANSACTION_FXML = "";
    private static final String INFO_TRANSACTION_ITEM_FXML = "/layout/InfoAboutTransactionLayout.fxml";

    public String getView() {
        switch (this) {
            case SEARCH_ITEM:
                return SEARCH_FXML;
            case ADD_ITEM:
                return ADD_ITEM_FXML;
            case BUY_ITEM:
                return BUY_ITEM_FXML;
            case SELL_ITEM:
                return SELL_ITEM_FXML;
            case INFO_ITEM:
                return INFO_ITEM_FXML;
            case EDIT_ITEM:
                return EDIT_ITEM_FXML;
            case SEARCH_TRANSACTION:
                return SEARCH_TRANSACTION_FXML;
            case INFO_TRANSACTION:
                return INFO_TRANSACTION_ITEM_FXML;
        }
        return "";
    }

    @Override
    public String toString() {
        switch (this) {
            case SEARCH_ITEM:
                return "SEARCH_ITEM";
            case ADD_ITEM:
                return "ADD_ITEM";
            case BUY_ITEM:
                return "BUY_ITEM";
            case SELL_ITEM:
                return "SELL_ITEM";
            case INFO_ITEM:
                return "INFO_ITEM";
            case EDIT_ITEM:
                return "EDIT_ITEM";
            case SEARCH_TRANSACTION:
                return "SEARCH_TRANSACTION";
            case INFO_TRANSACTION:
                return "INFO_TRANSACTION";
        }
        return "NONE";
    }
}
