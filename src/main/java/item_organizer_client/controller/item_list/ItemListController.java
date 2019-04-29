package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.element.ItemTableElement;
import item_organizer_client.model.list.NotificationList;
import item_organizer_client.utils.TableColumnFormatter;
import item_organizer_client.utils.Utils;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import org.controlsfx.control.textfield.CustomTextField;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class ItemListController extends SideBarController implements Initializable {
    public TableView<ItemTableElement> itemTableView;
    public AnchorPane itemMainPane;
    public Button homeButton, searchButton, addButton, buyButton, sellButton, infoButton, editButton;
    public SplitPane splitPane;
    public TableColumn<ItemTableElement, Integer> idColumn, amountColumn;
    public TableColumn<ItemTableElement, Price> buyPriceColumn, sellPriceColumn;
    public Label headerTitleText, headerAmountText;
    public CheckBox showHiddenProductsCheckBox;
    public CustomTextField headerSearchText;

    private static ItemListController instance;

    public static ItemListController getInstance() {
        return instance;
    }

    public ItemListController() {
        instance = this;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        setTableItems();

        itemTableView.setRowFactory(param -> {
            TableRow<ItemTableElement> row = new TableRow<ItemTableElement>() {
                @Override
                protected void updateItem(ItemTableElement item, boolean empty) {
                    super.updateItem(item, empty);
                    getStyleClass().remove("row-hidden");
                    if (!empty) {
                        if (item.getHidden()) {
                            getStyleClass().add("row-hidden");
                        }
                    }
                }
            };

            row.setOnMouseClicked(event -> {
                ItemTableElement itemTableElement = row.getItem();
                if (row.isEmpty()) {
                    itemTableView.getSelectionModel().clearSelection();
                } else if (event.getClickCount() == 2) {
                    Item item = new Item(itemTableElement);
                    switch (ItemListController.this.getCurrentView()) {
                        case NONE:
                        case SEARCH_ITEM:
                        case ADD_ITEM:
                        case INFO_ITEM:
                            ItemListController.this.showInfoView(item);
                            break;
                        case EDIT_ITEM:
                            ItemListController.this.showEditView(item);
                            break;
                        case BUY_ITEM:
                            ItemListController.this.showBuyView(item);
                            break;
                        case SELL_ITEM:
                            ItemListController.this.showSellView(item);
                            break;
                    }
                }
            });

            return row;
        });

        idColumn.setCellFactory(TableColumnFormatter.idFormat());
        buyPriceColumn.setCellFactory(param -> new TableCell<ItemTableElement, Price>() {
            @Override
            protected void updateItem(Price price, boolean empty) {
                super.updateItem(price, empty);
                ItemTableElement item = (ItemTableElement) getTableRow().getItem();
                getStyleClass().removeAll("alert-info");
                if (item == null || price == null || empty) {
                    setText(null);
                } else {
                    setText(price.priceFormat());
                    if (price.compareTo(item.getSellPrice()) > 0) {
                        getStyleClass().add("alert-info");
                    }
                }
            }
        });

        sellPriceColumn.setCellFactory(param -> new TableCell<ItemTableElement, Price>() {
            @Override
            protected void updateItem(Price price, boolean empty) {
                super.updateItem(price, empty);
                ItemTableElement item = (ItemTableElement) getTableRow().getItem();
                getStyleClass().removeAll("alert-info");
                if (item == null || price == null || empty) {
                    setText(null);
                } else {
                    setText(price.priceFormat());
                    if (price.compareTo(item.getBuyPrice()) < 0) {
                        getStyleClass().add("alert-info");
                    }
                }
            }
        });

        amountColumn.setCellFactory(param -> new TableCell<ItemTableElement, Integer>() {
            @Override
            protected void updateItem(Integer amount, boolean empty) {
                super.updateItem(amount, empty);
                ItemTableElement item = (ItemTableElement) getTableRow().getItem();
                getStyleClass().removeAll("alert-danger", "alert-warning");
                if (item == null || amount == null || empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(amount));
                    if (amount <= 0) {
                        getStyleClass().add("alert-danger");
                    } else {
                        if (amount < item.getSafeAmount()) {
                            getStyleClass().add("alert-warning");
                        }
                    }
                }
            }
        });

        Button searchClearButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
        searchClearButton.getStyleClass().add("clear-button");
        searchClearButton.setOnAction(event -> headerSearchText.setText(""));

        Label searchIcon = new Label("", new FontAwesomeIconView(FontAwesomeIcon.SEARCH));
        searchIcon.setPadding(new Insets(0, 0, 0, 4));
        headerSearchText.setLeft(searchIcon);
        headerSearchText.setRight(searchClearButton);
        headerSearchText.getRight().setVisible(false);

        ItemList.getInstance().setUpSearchBoxFilters(headerSearchText, showHiddenProductsCheckBox);

        headerSearchText.textProperty().addListener((observable, oldValue, newValue) ->
                headerSearchText.getRight().setVisible(newValue.length() > 0));

        headerAmountText.setText("(" + itemTableView.getItems().size() + ")");

        itemTableView.getItems().addListener((InvalidationListener) c -> {
            headerAmountText.setText("(" + itemTableView.getItems().size() + ")");
        });

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_ITEM, searchButton);
        getButtonMap().put(MenuView.ADD_ITEM, addButton);
        getButtonMap().put(MenuView.BUY_ITEM, buyButton);
        getButtonMap().put(MenuView.SELL_ITEM, sellButton);
        getButtonMap().put(MenuView.INFO_ITEM, infoButton);
        getButtonMap().put(MenuView.EDIT_ITEM, editButton);

        setSplitPane(splitPane);

        Utils.setTableColumnWidthProperty(itemTableView, getPreferences());
    }

    public void setTableItems() {
        ItemList.getInstance().init();

        ItemList.getInstance().setFilteredItemList(new FilteredList<>(FXCollections.observableList(ItemList.getInstance().getItemList().stream()
                .map(ItemTableElement::new).sorted().collect(Collectors.toList())), itemTableItem -> true));

        SortedList<ItemTableElement> sortedItemList = new SortedList<>(ItemList.getInstance().getFilteredItemList());
        sortedItemList.comparatorProperty().bind(itemTableView.comparatorProperty());

        itemTableView.setItems(sortedItemList);

        NotificationList.getInstance().check();

        ItemList.getInstance().addListener(this::setTableItems);
    }

    public void goHome(ActionEvent event) {
        hideView();
    }

    public void toggleSearchView(ActionEvent event) {
        toggleView(MenuView.SEARCH_ITEM);
    }

    public void toggleAddView(ActionEvent event) {
        toggleView(MenuView.ADD_ITEM);
    }

    public void toggleBuyView(ActionEvent event) {
        toggleView(MenuView.BUY_ITEM);
    }

    public void toggleSellView(ActionEvent event) {
        toggleView(MenuView.SELL_ITEM);
    }

    public void toggleInfoView(ActionEvent event) {
        toggleView(MenuView.INFO_ITEM);
    }

    public void toggleEditView(ActionEvent event) {
        toggleView(MenuView.EDIT_ITEM);
    }

    private void changeView(MenuView menuView) {
        if (!getCurrentView().equals(menuView)) {
            showView(menuView);
        }
    }

    public void showInfoView(Item item) {
        changeView(MenuView.INFO_ITEM);
        ((InfoAboutItemController) getCurrentController()).showInfoAbout(item.getNumber());
    }

    public void showEditView(Item item) {
        changeView(MenuView.EDIT_ITEM);
        ((EditItemController) getCurrentController()).setEditItem(item);
    }

    public void showBuyView(Item item) {
        changeView(MenuView.BUY_ITEM);
        ((BuyItemController) getCurrentController()).addNewItem(item);
    }

    public void showSellView(Item item) {
        changeView(MenuView.SELL_ITEM);
        ((SellItemController) getCurrentController()).addNewItem(item);
    }

    public CustomTextField getHeaderSearchText() {
        return headerSearchText;
    }

    public CheckBox getShowHiddenProductsCheckBox() {
        return showHiddenProductsCheckBox;
    }

    public TableView<ItemTableElement> getItemTableView() {
        return itemTableView;
    }
}
