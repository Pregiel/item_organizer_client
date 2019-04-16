package item_organizer_client.controller.item_list;

import item_organizer_client.controller.SideBarController;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.controller.MenuView;
import item_organizer_client.model.table_item.ItemTableItem;
import item_organizer_client.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class ItemListController extends SideBarController implements Initializable {
    public TableView<ItemTableItem> itemTableView;
    public AnchorPane itemMainPane;
    public Button homeButton, searchButton, addButton, buyButton, sellButton, infoButton;
    public SplitPane splitPane;

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

        itemTableView.getItems().addAll(itemList.getItemListAsTableItems());

        itemList.addListener(() -> {
            itemTableView.getItems().clear();
            itemTableView.getItems().addAll(itemList.getItemListAsTableItems());
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
