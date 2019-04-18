package item_organizer_client.controller.item_list;

import item_organizer_client.controller.MenuView;
import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.Item;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.table_item.ItemTableElement;
import item_organizer_client.utils.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class ItemListController extends SideBarController implements Initializable {
    public TableView<ItemTableElement> itemTableView;
    public AnchorPane itemMainPane;
    public Button homeButton, searchButton, addButton, buyButton, sellButton, infoButton;
    public SplitPane splitPane;
    public TableColumn<ItemTableElement, String> idColumn;

    private ItemList itemList;

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

        itemList = ItemList.getInstance();
        itemList.init();

        setTableItems();

        itemList.addListener(this::setTableItems);

        itemTableView.setRowFactory(param -> {
            TableRow<ItemTableElement> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Item item = new Item(row.getItem());
                    switch (getCurrentView()) {
                        case NONE:
                            showInfoAbout(item.getId());
                            break;
                        case INFO_ITEM:
                            ((InfoAboutItemController) getCurrentController()).showInfoAbout(item.getId());
                            break;
                        case SEARCH_ITEM:
                            break;
                        case ADD_ITEM:
                            break;
                        case BUY_ITEM:
                            ((BuyItemController) getCurrentController()).addNewItem(item);
                            break;
                        case SELL_ITEM:
                            ((SellItemController) getCurrentController()).addNewItem(item);
                            break;
                    }
                }
            });
            return row;
        });

        idColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(Utils.fillWithZeros(param.getValue().getId(), Item.ID_DIGITS)));

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
        FilteredList<ItemTableElement> filteredItemList
                = new FilteredList<>(FXCollections.observableList(itemList.getItemList().stream()
                .map(ItemTableElement::new).sorted().collect(Collectors.toList())), itemTableItem -> true);

        SortedList<ItemTableElement> sortedItemList = new SortedList<>(filteredItemList);
        sortedItemList.comparatorProperty().bind(itemTableView.comparatorProperty());

        itemTableView.setItems(sortedItemList);
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
        hideView();
        InfoAboutItemController infoController = showView(MenuView.INFO_ITEM);

        infoController.showInfoAbout(id);
    }

}
