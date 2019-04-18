package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@FXMLController
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BuyItemElementController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceService priceService;

    private int elementId;

    public TitledPane itemPane;
    public GridPane searchInputPane, detailsInputPane;
    public VBox buyItemPane, searchPane, detailsPane;
    public HBox buyPricePerItemPane;
    public Spinner<Integer> amountText;
    public RadioButton idRadioButton, nameRadioButton;
    public ToggleGroup searchGroup;
    public TextField buyPriceText, sellPriceText;
    public ComboBox<String> buyPriceType, searchText;
    public Label idNotExistAlert, nameNotExistAlert, buyPricePerItemText, buyNullAlert,
            amountNullAlert, sellNullAlert, selectedItemId, selectedItemName, selectedItemAmount, selectedItemBuy,
            selectedItemBuyPerItem, selectedItemSell, itemTitle;
    public Button removeItem;
    public BorderPane titlePane;

    private Item selectedItem;

    private BuyItemController buyItemController;

    private int step = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        titlePane.prefWidthProperty().bind(itemPane.widthProperty());
        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setItemSearchComboBox(searchText, 4, 250, searchGroup, idRadioButton, nameRadioButton,
                itemService, idNotExistAlert, nameNotExistAlert);
        setAmountSpinnerListeners(amountText, amountText.getParent(), amountNullAlert);
        setPriceTypeListeners(buyPriceText, buyPriceType, buyPriceText.getParent().getParent(), buyPricePerItemPane,
                buyPricePerItemText, amountText);
        setPriceTextFieldListeners(buyPriceText, buyPriceText.getParent().getParent(), buyNullAlert);
        setPriceTextFieldListeners(sellPriceText, sellPriceText.getParent(), sellNullAlert);

        refreshSearchTextListeners();
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(idNotExistAlert, nameNotExistAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
        ((Pane) buyPriceText.getParent().getParent()).getChildren().removeAll(buyPricePerItemPane, buyNullAlert);
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellNullAlert);
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

    private boolean checkIfItemIsAlreadyAdded(Item item) {
        for (BuyItemElementController controller : buyItemController.getControllerList()) {
            if (getElementId() != controller.getElementId()) {
                if (item.getId() == controller.getSelectedId()) {
                    if (item.getName().equals(controller.getSelectedName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void changeSelectedItem(ActionEvent event) {
        goToStep(0);
    }

    public void nextStepDetails(ActionEvent event) {
        if (amountText.getEditor().getText().equals(""))
            amountText.getEditor().setText("0");

        if (buyPriceText.getText().equals(""))
            amountText.getEditor().setText("0.00");

        if (sellPriceText.getText().equals(""))
            amountText.getEditor().setText("0.00");


        BigDecimal amountValue = new BigDecimal(amountText.getEditor().getText());
        BigDecimal sellPriceValue = new BigDecimal(sellPriceText.getText());
        BigDecimal buyPriceValue = new BigDecimal(buyPriceText.getText());

        if (amountValue.compareTo(BigDecimal.ZERO) == 0) {
            MyAlerts.showError("Liczba sztuk równa zero", "Zakupiona liczba sztuk musi być większa od 0.");
            return;
        }

        if (buyPriceValue.compareTo(BigDecimal.ZERO) == 0)
            if (!MyAlerts.showConfirmationDialog("Cena kupna równa zero", "Cena kupna jest równa 0.00zł. Chcesz kontynuować?"))
                return;

        if (sellPriceValue.compareTo(BigDecimal.ZERO) == 0)
            if (!MyAlerts.showConfirmationDialog("Cena sprzedaży równa zero", "Cena sprzedaży jest równa 0.00zł. Chcesz kontynuować?"))
                return;

        selectedItemAmount.setText(String.valueOf(amountValue));
        if (buyPriceType.getSelectionModel().getSelectedIndex() == 0) {
            selectedItemBuy.setText(Price.priceFormat(buyPriceValue.multiply(amountValue)));
            selectedItemBuyPerItem.setText(Price.priceFormat(buyPriceValue));
        } else {
            selectedItemBuy.setText(Price.priceFormat(buyPriceValue));
            selectedItemBuyPerItem.setText(Price.priceFormat(buyPriceValue.divide(amountValue, 2, RoundingMode.CEILING)));
        }
        selectedItemSell.setText(Price.priceFormat(sellPriceValue));

        goToStep(2);
    }

    public void changeSelectedDetails(ActionEvent event) {
        goToStep(1);
    }

    /**
     * @param step 0 - search item, 1 - insert details, 2 - summary
     */
    public void goToStep(int step) {
        this.step = step;
        clearAlerts();
        buyItemPane.getChildren().removeAll(searchInputPane, searchPane, detailsInputPane, detailsPane);
        switch (step) {
            case 0:
                buyItemPane.getChildren().addAll(searchInputPane);
                selectedItemId.setText("");
                selectedItemName.setText("");
                break;
            case 1:
                buyItemPane.getChildren().addAll(searchPane, detailsInputPane);
                selectedItemId.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                selectedItemName.setText(String.valueOf(selectedItem.getName()));

                if (buyPriceType.getSelectionModel().getSelectedIndex() != 0) {
                    buyPriceType.getSelectionModel().select(0);
                }

                Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
                if (lastedBuyPrice != null) {
                    buyPriceText.setText(lastedBuyPrice.getValue().toString());
                }

                Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
                if (lastedSellPrice != null) {
                    sellPriceText.setText(lastedSellPrice.getValue().toString());
                }

                if (checkIfItemIsAlreadyAdded(selectedItem)) {
                    if (!MyAlerts.showConfirmationDialog("Powtórzenie produktu", "Wybrany produkt znajduję się już na liście. Chcesz kontynuować?")) {
                        removeItem(null);
                        return;
                    }
                }
                break;

            case 2:
                buyItemPane.getChildren().addAll(searchPane, detailsPane);
                break;
        }
        setItemTitle();
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
        if (step == 0) {
            setItemTitle();
        }
    }

    public void setItemTitle() {
        if (step == 0) {
            itemTitle.setText("Produkt " + elementId);
        } else {
            itemTitle.setText(selectedItemId.getText() + ". " + selectedItemName.getText());
        }
    }

    public void setBuyItemController(BuyItemController buyItemController) {
        this.buyItemController = buyItemController;
    }

    public void removeItem(ActionEvent event) {
        buyItemController.removeItem(this);
    }

    public int getStep() {
        return step;
    }

    public int getSelectedId() {
        return Integer.valueOf((selectedItemId.getText().equals("") ? "0" : selectedItemId.getText()));
    }

    public String getSelectedName() {
        return selectedItemName.getText();
    }

    public int getSelectedAmount() {
        return Integer.valueOf(selectedItemAmount.getText());
    }

    public BigDecimal getSelectedBuyPrice() {
        return new BigDecimal(selectedItemBuyPerItem.getText().substring(0, selectedItemBuyPerItem.getText().length() - 3));
    }

    public BigDecimal getSelectedSellPrice() {
        return new BigDecimal(selectedItemSell.getText().substring(0, selectedItemSell.getText().length() - 3));
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
    }

    public int getElementId() {
        return elementId;
    }

    public TitledPane getItemPane() {
        return itemPane;
    }
}
