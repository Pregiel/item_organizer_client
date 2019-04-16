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

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

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
        setAmountSpinnerListeners(amountText, 1, amountText.getParent(), amountNullAlert);
        setPriceTypeListeners(buyPriceText, buyPriceType, buyPriceText.getParent().getParent(), buyPricePerItemPane,
                buyPricePerItemText, amountText);
        setPriceTextFieldListeners(buyPriceText, "0.00", buyPriceText.getParent().getParent(), buyNullAlert);
        setPriceTextFieldListeners(sellPriceText, "0.00", sellPriceText.getParent(), sellNullAlert);

        CustomListener.updateBuyPerItemLabelListener(buyPricePerItemText, buyPriceText, amountText, buyPriceType);

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

            Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
            if (lastedBuyPrice != null) {
                buyPriceText.setText(lastedBuyPrice.getValue().toString());
            }

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

        if (buyPriceText.getText().equals(""))
            amountText.getEditor().setText("0.00");

        if (sellPriceText.getText().equals(""))
            amountText.getEditor().setText("0.00");

        if (Integer.valueOf(amountText.getEditor().getText()) == 0) {
            MyAlerts.showError("Liczba sztuk równa zero", "Zakupiona liczba sztuk musi być większa od 0.");
            return;
        }

        if (Double.valueOf(buyPriceText.getText()) == 0.0)
            if (!MyAlerts.showConfirmationDialog("Cena kupna równa zero", "Cena kupna jest równa 0.00zł. Chcesz kontynuować?"))
                return;

        if (Double.valueOf(sellPriceText.getText()) == 0.0)
            if (!MyAlerts.showConfirmationDialog("Cena sprzedaży równa zero", "Cena sprzedaży jest równa 0.00zł. Chcesz kontynuować?"))
                return;


        int amountValue = amountText.getValue();
        double sellPriceValue = Double.valueOf(sellPriceText.getText());
        double buyPriceValue = Double.valueOf(buyPriceText.getText());


        selectedItemAmount.setText(String.valueOf(amountValue));
        if (buyPriceType.getSelectionModel().getSelectedIndex() == 0) {
            selectedItemBuy.setText(Utils.round(buyPriceValue * amountValue, 2) + " zł");
            selectedItemBuyPerItem.setText(Utils.round(buyPriceValue, 2) + " zł");
        } else {
            selectedItemBuy.setText(Utils.round(buyPriceValue, 2) + " zł");
            selectedItemBuyPerItem.setText(Utils.round(buyPriceValue / amountValue, 2) + " zł");
        }
        selectedItemSell.setText(sellPriceValue + " zł");

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
                    ((Pane) buyPriceText.getParent().getParent()).getChildren().add(buyPricePerItemPane);
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

    public double getSelectedBuyPrice() {
        return Double.valueOf(selectedItemBuyPerItem.getText().substring(0, selectedItemBuyPerItem.getText().length() - 3));
    }

    public double getSelectedSellPrice() {
        return Double.valueOf(selectedItemSell.getText().substring(0, selectedItemSell.getText().length() - 3));
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public TitledPane getItemPane() {
        return itemPane;
    }
}
