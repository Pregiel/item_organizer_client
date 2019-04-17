package item_organizer_client.controller.item_list;

import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.listeners.CustomListener;
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

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SellItemElementController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private PriceService priceService;

    private int elementId;

    public TitledPane itemPane;
    public GridPane searchInputPane, detailsInputPane;
    public VBox sellItemPane, searchPane, detailsPane;
    public HBox sellPricePerItemPane;
    public Spinner<Integer> amountText;
    public RadioButton idRadioButton, nameRadioButton;
    public ToggleGroup searchGroup;
    public TextField sellPriceText;
    public ComboBox<String> sellPriceType, searchText;
    public Label idNotExistAlert, nameNotExistAlert, sellPricePerItemText, amountNullAlert, sellNullAlert,
            selectedItemId, selectedItemName, selectedItemAmount, selectedItemSellPerItem, selectedItemSell, itemTitle;
    public Button removeItem;
    public BorderPane titlePane;

    private Item selectedItem;

    private SellItemController sellItemController;

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
        setAmountSpinnerListeners(amountText, 1, amountText.getParent(), amountNullAlert);
        setPriceTypeListeners(sellPriceText, sellPriceType, sellPriceText.getParent().getParent(), sellPricePerItemPane,
                sellPricePerItemText, amountText);
        setPriceTextFieldListeners(sellPriceText, "0.00", sellPriceText.getParent().getParent(), sellNullAlert);

        refreshSearchTextListeners();
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(idNotExistAlert, nameNotExistAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
        ((Pane) sellPriceText.getParent().getParent()).getChildren().removeAll(sellPricePerItemPane, sellNullAlert);
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

            Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
            if (lastedSellPrice != null) {
                sellPriceText.setText(lastedSellPrice.getValue().toString());
            }

            if (checkIfItemIsAlreadyAdded(selectedItem)) {
                if (!MyAlerts.showConfirmationDialog("Powtórzenie produktu", "Wybrany produkt znajduję się już na liście. Chcesz kontynuować?"))
                    return;
            }

            goToStep(1);

        } catch (IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            if (searchGroup.getSelectedToggle().equals(idRadioButton)) {
                ((Pane) searchText.getParent()).getChildren().add(idNotExistAlert);
            } else {
                ((Pane) searchText.getParent()).getChildren().add(nameNotExistAlert);
            }
        }
    }

    private boolean checkIfItemIsAlreadyAdded(Item item) {
        for (SellItemElementController controller : sellItemController.getControllerList()) {
            if (item.getId() == controller.getSelectedId()) {
                if (item.getName().equals(controller.getSelectedName())) {
                    return true;
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

        if (sellPriceText.getText().equals(""))
            amountText.getEditor().setText("0.00");

        BigDecimal amountValue = new BigDecimal(amountText.getEditor().getText());
        BigDecimal sellPriceValue = new BigDecimal(sellPriceText.getText());

        if (amountValue.compareTo(BigDecimal.ZERO) == 0) {
            MyAlerts.showError("Liczba sztuk równa zero", "Sprzedana liczba sztuk musi być większa od 0.");
            return;
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SellItemElementController controller : sellItemController.getControllerList()) {
            if (controller.getStep() == 2) {
                totalAmount = totalAmount.add(BigDecimal.valueOf(controller.getSelectedAmount()));
            }
        }
        totalAmount = totalAmount.add(amountValue);

        if (BigDecimal.valueOf(selectedItem.getAmount()).subtract(totalAmount).compareTo(BigDecimal.ZERO) < 0)
            if (!MyAlerts.showConfirmationDialog("Za mało sztuk w bazie", "Liczba sztuk w bazie jest mniejsza od podanej liczby sztuk. Chcesz kontynuować?"))
                return;

        if (sellPriceValue.compareTo(BigDecimal.ZERO) == 0)
            if (!MyAlerts.showConfirmationDialog("Cena sprzedaży równa zero", "Cena sprzedaży jest równa 0.00zł. Chcesz kontynuować?"))
                return;

        selectedItemAmount.setText(String.valueOf(amountValue));
        if (sellPriceType.getSelectionModel().getSelectedIndex() == 0) {
            selectedItemSell.setText(Price.priceFormat(sellPriceValue.multiply(amountValue)));
            selectedItemSellPerItem.setText(Price.priceFormat(sellPriceValue));
        } else {
            selectedItemSell.setText(Price.priceFormat(sellPriceValue));
            selectedItemSellPerItem.setText(Price.priceFormat(sellPriceValue.divide(amountValue, 2, RoundingMode.CEILING)));
        }

        goToStep(2);
    }

    public void changeSelectedDetails(ActionEvent event) {
        goToStep(1);
    }

    /**
     * @param step 0 - search item, 1 - insert details, 2 - summary
     */
    private void goToStep(int step) {
        this.step = step;
        clearAlerts();
        sellItemPane.getChildren().removeAll(searchInputPane, searchPane, detailsInputPane, detailsPane);
        switch (step) {
            case 0:
                sellItemPane.getChildren().addAll(searchInputPane);
                selectedItemId.setText("");
                selectedItemName.setText("");
                break;
            case 1:
                sellItemPane.getChildren().addAll(searchPane, detailsInputPane);
                selectedItemId.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                selectedItemName.setText(String.valueOf(selectedItem.getName()));

                if (sellPriceType.getSelectionModel().getSelectedIndex() != 0) {
                    ((Pane) sellPriceText.getParent().getParent()).getChildren().add(sellPricePerItemPane);
                }
                break;

            case 2:
                sellItemPane.getChildren().addAll(searchPane, detailsPane);
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

    public void setSellItemController(SellItemController sellItemController) {
        this.sellItemController = sellItemController;
    }

    public void removeItem(ActionEvent event) {
        sellItemController.removeItem(this);
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

    public BigDecimal getSelectedSellPrice() {
        return new BigDecimal(selectedItemSellPerItem.getText().substring(0, selectedItemSellPerItem.getText().length() - 3));
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public TitledPane getItemPane() {
        return itemPane;
    }
}
