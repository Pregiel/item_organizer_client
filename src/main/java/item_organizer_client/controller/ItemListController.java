package item_organizer_client.controller;

import item_organizer_client.model.list.ItemList;
import item_organizer_client.MenuView;
import item_organizer_client.model.table_item.ItemTableItem;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
public class ItemListController implements Initializable {
    private static final String SVG_RIGHT_ARROW = "M 25.06914,17.008679 14.65855,6.2618267 11.045713,9.8438848 " +
            "18.057804,17.008 11.045713,24.172117 14.65855,27.754175 Z M 16.899,0 C 7.566,0 0,7.566 0,16.9 c 0,9.333 " +
            "7.566,16.899 16.899,16.899 9.333,0 16.9,-7.566 16.9,-16.899 C 33.799,7.566 26.233,0 16.899,0 Z";
    private static final String SVG_LEFT_ARROW = "M 16.899,0 C 7.566,0 0,7.566 0,16.9 c 0,9.333 7.566,16.899 " +
            "16.899,16.899 9.333,0 16.9,-7.566 16.9,-16.899 C 33.799,7.566 26.233,0 16.899,0 Z M 8.4083432,17.008679 " +
            "18.802968,6.2618267 22.410264,9.8438848 15.408927,17.008 l 7.001337,7.164117 -3.607296,3.582058 z";

    private static final String ADD_FXML = "/layout/AddItemLayout.fxml";
    private static final String BUY_FXML = "/layout/BuyItemLayout.fxml";
    private static final String SELL_FXML = "/layout/SellItemLayout.fxml";
    private static final String SEARCH_FXML = "/layout/SearchItemLayout.fxml";
    private static final String INFO_FXML = "";

    public TableView<ItemTableItem> itemTableView;
    public AnchorPane itemMainPane;
    public VBox menuBar;
    public Button homeButton, searchButton, addButton, buyButton, sellButton, infoButton;
    public SplitPane splitPane;

    private Node currentNode;

    private MenuView currentView;

    private Preferences preferences;

    private ItemList itemList;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        itemList = ItemList.getInstance();

        itemList.init();

        itemTableView.getItems().addAll(itemList.getItemListAsTableItems());

        itemList.addListener(() -> {
            itemTableView.getItems().clear();
            itemTableView.getItems().addAll(itemList.getItemListAsTableItems());
        });

        int i = 0;
        for (TableColumn<ItemTableItem, ?> itemTableItemTableColumn : itemTableView.getColumns()) {
            double width = preferences.getDouble(
                    "column" + i + "_width",
                    itemTableItemTableColumn.getPrefWidth());

            itemTableItemTableColumn.setPrefWidth(width);

            int finalI = i;
            itemTableItemTableColumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                preferences.putDouble("column" + finalI + "_width", (Double) newValue);
            });
            i++;
        }
    }

    public void goHome(ActionEvent event) {
    }

    public void showSearchView(ActionEvent event) {
        toggleView(MenuView.SEARCH);
    }

    public void showAddView(ActionEvent event) {
        toggleView(MenuView.ADD);
    }

    public void showBuyView(ActionEvent event) {
        toggleView(MenuView.BUY);
    }

    public void showSellView(ActionEvent event) {
        toggleView(MenuView.SELL);
    }

    public void showInfoView(ActionEvent event) {
        toggleView(MenuView.INFO);
    }

    private void toggleView(MenuView menuView) {
        if (currentView == menuView) {
            hideView();
        } else {
            showView(menuView);
        }
    }

    private void showView(MenuView menuView) {
        SpringFXMLLoader loader = null;
        hideView();

        currentView = menuView;

        switch (menuView) {
            case SEARCH:
                loader = new SpringFXMLLoader(getClass().getResource(SEARCH_FXML));
                toggleButton(searchButton);
                break;
            case ADD:
                loader = new SpringFXMLLoader(getClass().getResource(ADD_FXML));
                toggleButton(addButton);
                break;
            case BUY:
                loader = new SpringFXMLLoader(getClass().getResource(BUY_FXML));
                toggleButton(buyButton);
                break;
            case SELL:
                loader = new SpringFXMLLoader(getClass().getResource(SELL_FXML));
                toggleButton(sellButton);
                break;
            case INFO:
                loader = new SpringFXMLLoader(getClass().getResource(INFO_FXML));
                toggleButton(infoButton);
                break;
            default:
        }

        if (loader != null) {
            try {
                currentNode = loader.load();
                splitPane.getItems().add(0, currentNode);

                double position = preferences.getDouble(currentView.toString().toLowerCase() + "_divider", 0);
                if (position <= 0) {
                    position = ((Pane) currentNode).getPrefWidth() / splitPane.getWidth();
                }

                splitPane.getDividers().get(0).setPosition(position);

                splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
                    preferences.putDouble(currentView.toString().toLowerCase() + "_divider", (Double) newValue);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void hideView() {
        if (currentNode != null) {
            splitPane.getItems().remove(currentNode);
        }
        currentView = MenuView.NONE;
        untoggleAllButtons();
    }

    private static final String TOGGLED_SIDE_ICON_BUTTON_STYLECLASS = "toggled-side-icon-button";

    private void toggleButton(Button button) {
        button.getStyleClass().add(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
    }

    private void untoggleAllButtons() {
        homeButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
        searchButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
        addButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
        buyButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
        sellButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
        infoButton.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
    }
}
