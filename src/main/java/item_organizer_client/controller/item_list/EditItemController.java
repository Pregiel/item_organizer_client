package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.table_item.PriceTableElement;
import item_organizer_client.model.table_item.TransactionItemInfoItemTableElement;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Icon;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class EditItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PriceService priceService;

    public HBox footerPane, selectedItemPane;
    public VBox editItemPane, headerPane;
    public Button sellPriceReset, buyPriceReset, safeAmountReset, amountReset, categoryReset, nameReset, idReset;
    public GridPane searchInputPane, editValuePane;
    public BorderPane editPane;
    public TextField idText, nameText, buyPriceText, sellPriceText;
    public Spinner<Integer> amountText, safeAmountText;
    public ComboBox<String> searchText, categoryText;
    public Label nameMinAlert, categoryMinAlert, idNullAlert, idDuplicateAlert, nameNullAlert, nameDuplicateAlert,
            amountNullAlert, categoryMaxAlert, nameMaxAlert, buyNullAlert, sellNullAlert, categoryNullAlert, idMaxAlert,
            safeAmountNullAlert, selectedItemTitle, nameNotExistAlert, idNotExistAlert;
    public RadioButton idRadioButton, nameRadioButton;
    public ToggleGroup searchGroup;

    private Item selectedItem;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setItemSearchComboBox(searchText, 4, 250, searchGroup, idRadioButton, nameRadioButton,
                itemService, idNotExistAlert, nameNotExistAlert);

        setIdTextFieldListeners(idText, 4, idText.getParent(), idNullAlert, idMaxAlert, idDuplicateAlert);
        setNameTextFieldListeners(nameText, nameText.getParent(), nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        setCategoryComboBoxListeners(categoryText, categoryService, categoryText.getParent(),
                categoryNullAlert, categoryMinAlert, categoryMaxAlert);
        setAmountSpinnerListeners(amountText, Item.INITIAL_AMOUNT_VALUE, amountText.getParent(), amountNullAlert);
        setAmountSpinnerListeners(safeAmountText, Item.INITIAL_SAFE_AMOUNT_VALUE, safeAmountText.getParent(), safeAmountNullAlert);
        setPriceTextFieldListeners(buyPriceText, buyPriceText.getParent(), buyNullAlert);
        setPriceTextFieldListeners(sellPriceText, sellPriceText.getParent(), sellNullAlert);

        refreshSearchTextListeners();

        for (Button button : new Button[]{idReset, nameReset, categoryReset, amountReset, safeAmountReset, buyPriceReset, sellPriceReset}) {
            Icon.setIconButton(button, Icon.createSVGIcon(Icon.IconPath.RESTORE,
                    "-icon-color",
                    "-icon-hover-color",
                    12));
        }
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(idNotExistAlert, nameNotExistAlert);

        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
        ((Pane) safeAmountText.getParent()).getChildren().removeAll(safeAmountNullAlert);
        ((Pane) buyPriceText.getParent()).getChildren().removeAll(buyNullAlert);
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellNullAlert);
    }

    public void reset(ActionEvent event) {
        clearAlerts();

        selectedItemTitle.setText(selectedItem.toTitle());

        resetId(null);
        resetName(null);
        resetCategory(null);
        resetAmount(null);
        resetSafeAmount(null);
        resetBuyPrice(null);
        resetSellPrice(null);
    }

    public void resetId(ActionEvent event) {
        idText.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
    }

    public void resetName(ActionEvent event) {
        nameText.setText(String.valueOf(selectedItem.getName()));
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
    }

    public void resetCategory(ActionEvent event) {
        categoryText.getEditor().setText(selectedItem.getCategory().getName());
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
    }

    public void resetAmount(ActionEvent event) {
        amountText.getEditor().setText(String.valueOf(selectedItem.getAmount()));
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
    }

    public void resetSafeAmount(ActionEvent event) {
        safeAmountText.getEditor().setText(String.valueOf(selectedItem.getSafeAmount()));
        ((Pane) safeAmountText.getParent()).getChildren().removeAll(safeAmountNullAlert);
    }

    public void resetBuyPrice(ActionEvent event) {
        Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
        if (lastedBuyPrice != null) {
            buyPriceText.setText(lastedBuyPrice.priceFormat());
        }
        ((Pane) buyPriceText.getParent()).getChildren().removeAll(buyNullAlert);
    }

    public void resetSellPrice(ActionEvent event) {
        Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
        if (lastedSellPrice != null) {
            sellPriceText.setText(lastedSellPrice.priceFormat());
        }
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellNullAlert);
    }

    public void submit(ActionEvent event) {

    }

    /**
     * @param step 0 - search item, 1 - details
     */
    private void goToStep(int step) {
        clearAlerts();
        editItemPane.getChildren().removeAll(searchInputPane, editValuePane);
        editPane.setBottom(null);
        headerPane.getChildren().remove(selectedItemPane);
        switch (step) {
            case 0:
                editItemPane.getChildren().addAll(searchInputPane);
                break;
            case 1:
                editItemPane.getChildren().addAll(editValuePane);
                headerPane.getChildren().add(selectedItemPane);
                editPane.setBottom(footerPane);

                selectedItemTitle.setText(selectedItem.toTitle());
                idText.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                nameText.setText(String.valueOf(selectedItem.getName()));
                categoryText.getEditor().setText(selectedItem.getCategory().getName());
                amountText.getEditor().setText(String.valueOf(selectedItem.getAmount()));
                safeAmountText.getEditor().setText(String.valueOf(selectedItem.getSafeAmount()));

                Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
                if (lastedBuyPrice != null) {
                    buyPriceText.setText(lastedBuyPrice.priceFormat());
                }

                Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
                if (lastedSellPrice != null) {
                    sellPriceText.setText(lastedSellPrice.priceFormat());
                }
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

    public void showEditItem(int id) {
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
