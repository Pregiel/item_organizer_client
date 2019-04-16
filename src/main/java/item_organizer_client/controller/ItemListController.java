package item_organizer_client.controller;

import item_organizer_client.model.list.ItemList;
import item_organizer_client.MenuView;
import item_organizer_client.model.table_item.ItemTableItem;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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

    private Preferences preferences;
    private ItemList itemList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        preferences = Preferences.userRoot().node(this.getClass().getName());
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
        setPreferences(preferences);

        int i = 0;
        for (TableColumn<ItemTableItem, ?> column : itemTableView.getColumns()) {
            int finalI = i;
            if (column.getColumns().size() > 0) {
                int ii = 0;
                for (TableColumn<ItemTableItem, ?> subcolumn : column.getColumns()) {
                    double width = preferences.getDouble(
                            "column" + i + "_" + ii + "_width",
                            subcolumn.getPrefWidth());

                    subcolumn.setPrefWidth(width);

                    int finalII = ii;
                    subcolumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                        preferences.putDouble("column" + finalI + "_" + finalII + "_width", (Double) newValue);
                    });
                    ii++;
                }
            } else {
                double width = preferences.getDouble(
                        "column" + i + "_width",
                        column.getPrefWidth());

                column.setPrefWidth(width);

                column.widthProperty().addListener((observable, oldValue, newValue) -> {
                    preferences.putDouble("column" + finalI + "_width", (Double) newValue);
                });
                i++;
            }
        }
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

}
