package item_organizer_client.controller.item_list;

import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.table_item.TransactionItemInfoTableItem;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@Component
public class InfoAboutItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceService priceService;

    public VBox infoItemPane, detailsPane;
    public GridPane searchInputPane;
    public ComboBox<String> searchText;
    public Label idNotExistAlert, nameNotExistAlert, selectedItemId, selectedItemName, selectedItemCategory,
            selectedItemAmount, selectedItemBuyPrice, selectedItemSellPrice;
    public RadioButton idRadioButton, nameRadioButton;
    public ToggleGroup searchGroup, priceHistoryGroup, transactionHistoryPrice;
    public TableView<Price> priceHistoryTable;
    public TableView<TransactionItemInfoTableItem> transactionHistoryTable;
    public TableColumn<Price, String> priceDateColumn, pricePriceColumn;
    public TableColumn<TransactionItemInfoTableItem, String> transactionDateColumn;

    private Item selectedItem;

    private int step = 0;

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
        setItemSearchComboBox(searchText, 4, 250, searchGroup, idRadioButton, nameRadioButton,
                itemService, idNotExistAlert, nameNotExistAlert);

        priceDateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(Utils.getDateFormatter())));

        pricePriceColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(Price.priceFormat(param.getValue().getValue())));

        transactionDateColumn.setCellValueFactory(param ->
                new SimpleObjectProperty<>(param.getValue().getDate().toLocalDateTime().format(Utils.getDateFormatter())));

        refreshSearchTextListeners();
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(idNotExistAlert, nameNotExistAlert);
    }

    /**
     * @param step 0 - search item, 1 - details
     */
    private void goToStep(int step) {
        this.step = step;
        clearAlerts();
        infoItemPane.getChildren().removeAll(searchInputPane, detailsPane);
        switch (step) {
            case 0:
                infoItemPane.getChildren().addAll(searchInputPane);
                break;
            case 1:
                infoItemPane.getChildren().addAll(detailsPane);

                selectedItemId.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                selectedItemName.setText(String.valueOf(selectedItem.getName()));
                selectedItemCategory.setText(selectedItem.getCategory().getName());
                selectedItemAmount.setText(String.valueOf(selectedItem.getAmount()));

                Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
                if (lastedBuyPrice != null) {
                    selectedItemBuyPrice.setText(lastedBuyPrice.toString());
                }

                Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
                if (lastedSellPrice != null) {
                    selectedItemSellPrice.setText(lastedSellPrice.toString());
                }

                priceHistoryTable.getItems().clear();
                priceHistoryTable.getItems().addAll(selectedItem.getPrices());

                transactionHistoryTable.getItems().clear();
                transactionHistoryTable.getItems().addAll(selectedItem.getTransactionItems()
                        .stream().map(TransactionItemInfoTableItem::new).collect(Collectors.toList()));
                break;
        }
    }

    public void nextStepSearch(ActionEvent event) {
        try {
            if (searchGroup.getSelectedToggle().equals(idRadioButton)) {
                selectedItem = itemService.findById(Integer.parseInt(searchText.getEditor().getText()));
            } else {
                selectedItem = itemService.findByName(searchText.getEditor().getText()).get(0);
            }
            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);

        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            if (searchGroup.getSelectedToggle().equals(idRadioButton)) {
                ((Pane) searchText.getParent()).getChildren().add(idNotExistAlert);
            } else {
                ((Pane) searchText.getParent()).getChildren().add(nameNotExistAlert);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            if (searchGroup.getSelectedToggle().equals(idRadioButton)) {
                MyAlerts.showError("Niepoprawne ID", "Wprowadzono nie poprawny numer ID.");
            } else {
                MyAlerts.showError("Niepoprawna nazwa", "Wprowadzono nie poprawną nazwę.");
            }
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
            idRadioButton.fire();
            searchText.getEditor().setText(Utils.fillWithZeros(id, 4));
            MyAlerts.showError("Niepoprawne ID", "Wprowadzono nie poprawny numer ID.");
            ((Pane) searchText.getParent()).getChildren().add(idNotExistAlert);
        }
    }
}
