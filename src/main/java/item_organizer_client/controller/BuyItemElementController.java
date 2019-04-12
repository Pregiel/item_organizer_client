package item_organizer_client.controller;

import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.listeners.CustomListener;
import item_organizer_client.listeners.SpinnerListener;
import item_organizer_client.listeners.TextFieldListener;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import item_organizer_client.listeners.ComboBoxListener;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BuyItemElementController implements Initializable {
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

    private Preferences preferences;

    private Item selectedItem;

    private AutoCompletionBinding<String> autoCompletionSearch;

    private ChangeListener<String> onlyNumericListener, maxIdCharsAmountListener, maxNameCharsAmountListener;
    private ChangeListener<Boolean> fillWithZerosListener, autoTrimListener, removeAlertsListener;

    private BuyItemController buyItemController;

    private int step = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        titlePane.prefWidthProperty().bind(itemPane.widthProperty());

        int selectedSearchType = preferences.getInt("search_type", 1);
        searchGroup.selectToggle((selectedSearchType == 0 ? idRadioButton : nameRadioButton));

        searchText.getItems().addAll((selectedSearchType == 0 ? itemService.getAllIDs() : itemService.getAllNames()));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1);
        amountText.setValueFactory(valueFactory);

        buyPriceType.getItems().addAll("za sztukę", "za wszystko");
        buyPriceType.getSelectionModel().select(0);

        initListeners();

        refreshSearchTextListeners();
        goToStep(0);

        clearAlerts();
    }

    @SuppressWarnings({"AccessStaticViaInstance", "ConstantConditions"})
    private void initListeners() {
        searchGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            searchText.getItems().clear();

            if (newValue.equals(idRadioButton)) {
                preferences.putInt("search_type", 0);
                searchText.getItems().addAll(itemService.getAllIDs());

            } else {
                preferences.putInt("search_type", 1);
                searchText.getItems().addAll(itemService.getAllNames());
            }

            if (autoCompletionSearch != null)
                autoCompletionSearch.dispose();
            autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());
            refreshSearchTextListeners();
        });

        autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());

        onlyNumericListener = ComboBoxListener.onlyReturn().onlyNumericListener(searchText);
        maxIdCharsAmountListener = ComboBoxListener.onlyReturn().maxCharsAmountListener(searchText, 4);
        maxNameCharsAmountListener = ComboBoxListener.onlyReturn().maxCharsAmountListener(searchText, 250);
        fillWithZerosListener = ComboBoxListener.onlyReturn().fillWithZerosListener(searchText, 4);

        ComboBoxListener.autoTrimListener(searchText);
        ComboBoxListener.removeAlertsListener(searchText.getParent(), idNotExistAlert, nameNotExistAlert);

        SpinnerListener.onlyNumericListener(amountText);
        SpinnerListener.autoFillListener(amountText, 1);

        ((Pane) buyPriceText.getParent().getParent()).getChildren().remove(buyPricePerItemPane);
        buyPriceType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                ((Pane) buyPriceText.getParent().getParent()).getChildren().remove(buyPricePerItemPane);
            } else {
                ((Pane) buyPriceText.getParent().getParent()).getChildren().add(buyPricePerItemPane);
                buyPricePerItemText.setText(Utils.round(
                        Double.valueOf(buyPriceText.getText()) / Integer.valueOf(amountText.getEditor().getText()), 2) + " zł");
            }
        });

        TextFieldListener.priceListener(buyPriceText);
        TextFieldListener.autoFillPriceListener(buyPriceText, "0.00");
        TextFieldListener.isNullListener(buyPriceText, buyNullAlert, (Pane) buyPriceText.getParent().getParent());
        TextFieldListener.autoTrimListener(buyPriceText);
        TextFieldListener.removeAlertsListener(buyPriceText.getParent().getParent(), buyNullAlert);

        TextFieldListener.priceListener(sellPriceText);
        TextFieldListener.autoFillPriceListener(sellPriceText, "0.00");
        TextFieldListener.isNullListener(sellPriceText, sellNullAlert);
        TextFieldListener.autoTrimListener(sellPriceText);
        TextFieldListener.removeAlertsListener(sellPriceText.getParent(), sellNullAlert);

        CustomListener.updateBuyPerItemLabelListener(buyPricePerItemText, buyPriceText, amountText, buyPriceType);
    }

    private void refreshSearchTextListeners() {
        searchText.getEditor().textProperty().removeListener(onlyNumericListener);
        searchText.getEditor().textProperty().removeListener(maxIdCharsAmountListener);
        searchText.getEditor().textProperty().removeListener(maxNameCharsAmountListener);
        searchText.focusedProperty().removeListener(fillWithZerosListener);

        searchText.getEditor().setText("");

        if (searchGroup.getSelectedToggle().equals(idRadioButton)) {
            searchText.getEditor().textProperty().addListener(onlyNumericListener);
            searchText.getEditor().textProperty().addListener(maxIdCharsAmountListener);
            searchText.focusedProperty().addListener(fillWithZerosListener);
        } else {
            searchText.getEditor().textProperty().addListener(maxNameCharsAmountListener);
        }

    }

    private void clearAlerts() {
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

        if (Integer.valueOf(amountText.getEditor().getText()) == 0)
            if (!MyAlerts.showConfirmationDialog("Liczba sztuk równa zero", "Zakupiona liczba sztuk jest równa 0. Chcesz kontynuować?"))
                return;

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
