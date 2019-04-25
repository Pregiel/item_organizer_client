package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.element.PriceTableElement;
import item_organizer_client.model.element.TransactionItemInfoItemTableElement;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.TableColumnFormatter;
import item_organizer_client.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class InfoAboutItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceService priceService;

    public VBox infoItemPane, detailsPane, headerPane;
    public HBox selectedItemPane;
    public GridPane searchInputPane;
    public ComboBox<String> searchText;
    public Label selectedItemTitle, itemNotExistAlert, selectedItemId, selectedItemName,
            selectedItemCategory, selectedItemAmount, selectedItemSafeAmount, selectedItemBuyPrice,
            selectedItemSellPrice;
    public ToggleGroup priceHistoryGroup, transactionHistoryGroup;
    public ToggleButton sellTransactionHistoryToggle, buyTransactionHistoryToggle, allTransactionHistoryToggle,
            sellPriceHistoryToggle, buyPriceHistoryToggle, allPriceHistoryToggle;
    public TableView<PriceTableElement> priceHistoryTable;
    public TableView<TransactionItemInfoItemTableElement> transactionHistoryTable;

    public TableColumn<TransactionItemInfoItemTableElement, Timestamp> dateTransactionHistoryTable;
    public TableColumn<TransactionItemInfoItemTableElement, Price> pricePerItemTransactionHistoryTable;
    public TableColumn<TransactionItemInfoItemTableElement, BigDecimal> totalPriceTransactionHistoryTable;
    public TableColumn<PriceTableElement, BigDecimal> pricePriceHistoryTable;
    public TableColumn<PriceTableElement, Timestamp> datePriceHistoryTable;

    private Item selectedItem;
    private FilteredList<PriceTableElement> priceHistoryList;
    private FilteredList<TransactionItemInfoItemTableElement> transactionHistoryList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        Utils.setTableColumnWidthProperty(priceHistoryTable, getPreferences());
        Utils.setTableColumnWidthProperty(transactionHistoryTable, getPreferences());

        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setItemSearchComboBox(searchText, itemService.getAllTitles(), itemNotExistAlert);

        pricePriceHistoryTable.setCellFactory(TableColumnFormatter.priceFormat());
        datePriceHistoryTable.setCellFactory(TableColumnFormatter.dateFormat());

        pricePerItemTransactionHistoryTable.setCellFactory(TableColumnFormatter.priceFormat());
        totalPriceTransactionHistoryTable.setCellFactory(TableColumnFormatter.priceFormat());
        dateTransactionHistoryTable.setCellFactory(TableColumnFormatter.dateFormat());

        priceHistoryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                priceHistoryList.setPredicate(price -> {
                    if (newValue.equals(allPriceHistoryToggle)) {
                        return true;
                    } else if (newValue.equals(buyPriceHistoryToggle)) {
                        return price.getType().equals(PriceType.BUY);
                    } else if (newValue.equals(sellPriceHistoryToggle)) {
                        return price.getType().equals(PriceType.SELL);
                    }
                    return false;
                });
            }
        });

        transactionHistoryGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                oldValue.setSelected(true);
            } else {
                transactionHistoryList.setPredicate(transactionItem -> {
                    if (newValue.equals(allTransactionHistoryToggle)) {
                        return true;
                    } else if (newValue.equals(buyTransactionHistoryToggle)) {
                        return transactionItem.getType().equals(TransactionType.BUY);
                    } else if (newValue.equals(sellTransactionHistoryToggle)) {
                        return transactionItem.getType().equals(TransactionType.SELL);
                    }
                    return false;
                });
            }
        });
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(itemNotExistAlert);
    }

    /**
     * @param step 0 - search item, 1 - details
     */
    private void goToStep(int step) {
        clearAlerts();
        infoItemPane.getChildren().removeAll(searchInputPane, detailsPane);
        headerPane.getChildren().remove(selectedItemPane);
        switch (step) {
            case 0:
                infoItemPane.getChildren().addAll(searchInputPane);
                break;
            case 1:
                infoItemPane.getChildren().addAll(detailsPane);
                headerPane.getChildren().add(selectedItemPane);

                selectedItemTitle.setText(selectedItem.toTitle());
                selectedItemId.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                selectedItemName.setText(String.valueOf(selectedItem.getName()));
                selectedItemCategory.setText(selectedItem.getCategory().getName());
                selectedItemAmount.setText(String.valueOf(selectedItem.getAmount()));
                selectedItemSafeAmount.setText(String.valueOf(selectedItem.getSafeAmount()));

                Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
                if (lastedBuyPrice != null) {
                    selectedItemBuyPrice.setText(lastedBuyPrice.priceFormat());
                }

                Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
                if (lastedSellPrice != null) {
                    selectedItemSellPrice.setText(lastedSellPrice.priceFormat());
                }

                priceHistoryList
                        = new FilteredList<>(FXCollections.observableList(selectedItem.getPrices().stream()
                        .map(PriceTableElement::new).sorted().collect(Collectors.toList())), price -> true);

                SortedList<PriceTableElement> priceHistorySortedList = new SortedList<>(priceHistoryList);
                priceHistorySortedList.comparatorProperty().bind(priceHistoryTable.comparatorProperty());

                priceHistoryTable.setItems(priceHistorySortedList);

                transactionHistoryList
                        = new FilteredList<>(FXCollections.observableList(selectedItem.getTransactionItems()
                        .stream().map(TransactionItemInfoItemTableElement::new).sorted().collect(Collectors.toList())),
                        price -> true);

                SortedList<TransactionItemInfoItemTableElement> transactionHistorySortedList
                        = new SortedList<>(transactionHistoryList);
                transactionHistorySortedList.comparatorProperty().bind(transactionHistoryTable.comparatorProperty());

                transactionHistoryTable.setItems(transactionHistorySortedList);
                break;
        }
    }

    public void nextStepSearch(ActionEvent event) {
        try {
            String text = searchText.getEditor().getText();
            if (text.substring(0,4).matches("\\d{4}")) {
                selectedItem = itemService.findById(Integer.parseInt(text.substring(0, 4)));
            } else {
                selectedItem = itemService.findByName(text);
            }

            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }

    public void changeSelectedItem(ActionEvent event) {
        goToStep(0);
    }

    public void showInfoAbout(int id) {
        try {
            selectedItem = itemService.findById(id);

            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            searchText.getEditor().setText(Utils.fillWithZeros(id, 4));
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }
}
