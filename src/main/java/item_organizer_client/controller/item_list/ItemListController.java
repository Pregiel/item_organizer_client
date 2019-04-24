package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.table_item.ItemTableElement;
import item_organizer_client.utils.TableColumnFormatter;
import item_organizer_client.utils.Utils;
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
    public Button homeButton, searchButton, addButton, buyButton, sellButton, infoButton;
    public SplitPane splitPane;
    public TableColumn<ItemTableElement, Integer> idColumn;
    public TableColumn<ItemTableElement, Price> buyPriceColumn, sellPriceColumn;
    public Label headerTitleText;
    public Label headerAmountText;
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

        itemTableView.setRowFactory(new Callback<TableView<ItemTableElement>, TableRow<ItemTableElement>>() {
            @Override
            public TableRow<ItemTableElement> call(TableView<ItemTableElement> param) {
                TableRow<ItemTableElement> row = new TableRow<ItemTableElement>() {
                    @Override
                    protected void updateItem(ItemTableElement item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item != null) {
                            getStyleClass().removeAll("alert-danger", "alert-warning");
                            if (item.getAmount() <= 0) {
                                getStyleClass().add("alert-danger");
                            } else {
                                if (item.getAmount() < item.getSafeAmount()) {
                                    getStyleClass().add("alert-warning");
                                }
                            }
                        }
                    }
                };

                row.setOnMouseClicked(event -> {
                    ItemTableElement item = row.getItem();
                    if (row.isEmpty()) {
                        itemTableView.getSelectionModel().clearSelection();
                    } else if (event.getClickCount() == 2) {
                        switch (ItemListController.this.getCurrentView()) {
                            case NONE:
                            case SEARCH_ITEM:
                            case ADD_ITEM:
                            case INFO_ITEM:
                                ItemListController.this.showInfoAbout(item.getId());
                                break;
                            case BUY_ITEM:
                                ((BuyItemController) ItemListController.this.getCurrentController()).addNewItem(new Item(item));
                                break;
                            case SELL_ITEM:
                                ((SellItemController) ItemListController.this.getCurrentController()).addNewItem(new Item(item));
                                break;
                        }
                    }
                });
                return row;
            }
        });

        idColumn.setCellFactory(TableColumnFormatter.idFormat());
        buyPriceColumn.setCellFactory(TableColumnFormatter.priceFormat());
        sellPriceColumn.setCellFactory(TableColumnFormatter.priceFormat());

        Button searchClearButton = new Button("", new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
        searchClearButton.getStyleClass().add("clear-button");
        searchClearButton.setOnAction(event -> headerSearchText.setText(""));

        Label searchIcon = new Label("", new FontAwesomeIconView(FontAwesomeIcon.SEARCH));
        searchIcon.setPadding(new Insets(0, 0, 0, 4));
        headerSearchText.setLeft(searchIcon);
        headerSearchText.setRight(searchClearButton);
        headerSearchText.getRight().setVisible(false);

        ItemList.getInstance().setUpSearchBoxFilters(headerSearchText);

        headerSearchText.textProperty().addListener((observable, oldValue, newValue) ->
                headerSearchText.getRight().setVisible(newValue.length() > 0));

        headerAmountText.setText("(" + itemTableView.getItems().size() + ")");

        itemTableView.getItems().addListener((InvalidationListener) c -> {
            headerAmountText.setText("(" + itemTableView.getItems().size() + ")");
            if (itemTableView.getItems().size() == ItemList.getInstance().getItemList().size()) {
                headerTitleText.setText(ResourceBundle.getBundle("strings").getString("item_list.header.all_items"));
            } else {
                headerTitleText.setText(ResourceBundle.getBundle("strings").getString("item_list.header.founded"));
            }
        });

        getButtonMap().put(MenuView.NONE, homeButton);
        getButtonMap().put(MenuView.SEARCH_ITEM, searchButton);
        getButtonMap().put(MenuView.ADD_ITEM, addButton);
        getButtonMap().put(MenuView.BUY_ITEM, buyButton);
        getButtonMap().put(MenuView.SELL_ITEM, sellButton);
        getButtonMap().put(MenuView.INFO_ITEM, infoButton);

        setSplitPane(splitPane);

        Utils.setTableColumnWidthProperty(itemTableView, getPreferences());
    }

    private void setTableItems() {
        ItemList.getInstance().init();

        ItemList.getInstance().setFilteredItemList(new FilteredList<>(FXCollections.observableList(ItemList.getInstance().getItemList().stream()
                .map(ItemTableElement::new).sorted().collect(Collectors.toList())), itemTableItem -> true));

        SortedList<ItemTableElement> sortedItemList = new SortedList<>(ItemList.getInstance().getFilteredItemList());
        sortedItemList.comparatorProperty().bind(itemTableView.comparatorProperty());

        itemTableView.setItems(sortedItemList);

        ItemList.getInstance().addListener(this::setTableItems);
    }

    public void goHome(ActionEvent event) {
        hideView();
    }

    public void showSearchView(ActionEvent event) {
        toggleView(MenuView.SEARCH_ITEM);
    }

    public void showAddView(ActionEvent event) {
        toggleView(MenuView.ADD_ITEM);
    }

    public void showBuyView(ActionEvent event) {
        toggleView(MenuView.BUY_ITEM);
    }

    public void showSellView(ActionEvent event) {
        toggleView(MenuView.SELL_ITEM);
    }

    public void showInfoView(ActionEvent event) {
        toggleView(MenuView.INFO_ITEM);
    }

    public void showInfoAbout(int id) {
        if (!getCurrentView().equals(MenuView.INFO_ITEM)) {
            hideView();
            showView(MenuView.INFO_ITEM);
        }
        ((InfoAboutItemController) getCurrentController()).showInfoAbout(id);
    }

    public CustomTextField getHeaderSearchText() {
        return headerSearchText;
    }
}
