package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.listeners.TextFieldListener;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Icon;
import item_organizer_client.utils.IconGraphic;
import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
            safeAmountNullAlert, selectedItemTitle, itemNotExistAlert;

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
        setItemSearchComboBox(searchText, itemService.getAllTitles(), itemNotExistAlert);

        setIdTextFieldListeners(idText, Item.ID_DIGITS, idText.getParent(), idNullAlert, idDuplicateAlert, idMaxAlert);
        setNameTextFieldListeners(nameText, nameText.getParent(), nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        setCategoryComboBoxListeners(categoryText, categoryService, categoryText.getParent(),
                categoryNullAlert, categoryMinAlert, categoryMaxAlert);
        setAmountSpinnerListeners(amountText, Item.INITIAL_AMOUNT_VALUE, amountText.getParent(), amountNullAlert);
        setAmountSpinnerListeners(safeAmountText, Item.INITIAL_SAFE_AMOUNT_VALUE, safeAmountText.getParent(), safeAmountNullAlert);
        setPriceTextFieldListeners(buyPriceText, buyPriceText.getParent(), buyNullAlert);
        setPriceTextFieldListeners(sellPriceText, sellPriceText.getParent(), sellNullAlert);

        for (Button button : new Button[]{idReset, nameReset, categoryReset, amountReset, safeAmountReset, buyPriceReset, sellPriceReset}) {
            Icon.setIconButton(button, Icon.createSVGIcon(IconGraphic.RESTORE,
                    "-icon-color",
                    "-icon-hover-color",
                    12));
        }
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(itemNotExistAlert);

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

    private ChangeListener<Boolean> checkIdIfExistListener;

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

                if (checkIdIfExistListener != null) {
                    idText.focusedProperty().removeListener(checkIdIfExistListener);
                }
                checkIdIfExistListener = TextFieldListener.onlyReturn().checkIdIfExistListener(idText,
                        itemService, selectedItem.getId(), idText.getParent(), idDuplicateAlert);
                idText.focusedProperty().addListener(checkIdIfExistListener);

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
            String text = searchText.getEditor().getText();
            if (text.substring(0, 4).matches("\\d{4}")) {
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

    public void setEditItem(Item item) {
        try {
            selectedItem = item;

            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            searchText.getEditor().setText(Utils.fillWithZeros(item.getId(), 4));
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }
}
