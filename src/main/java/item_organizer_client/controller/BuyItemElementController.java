package item_organizer_client.controller;

import item_organizer_client.database.repository.ItemRepository;
import item_organizer_client.database.repository.PriceRepository;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Utils;
import item_organizer_client.utils.listeners.ComboBoxListeners;
import item_organizer_client.utils.listeners.SpinnerListeners;
import item_organizer_client.utils.listeners.TextFieldListeners;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class BuyItemElementController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferences = Preferences.userRoot().node(this.getClass().getName());

        titlePane.prefWidthProperty().bind(itemPane.widthProperty());

        int selectedSearchType = preferences.getInt("search_type", 1);
        searchGroup.selectToggle((selectedSearchType == 0 ? idRadioButton : nameRadioButton));

        searchText.getItems().addAll((selectedSearchType == 0 ? ItemRepository.getAllIDs() : ItemRepository.getAllNames()));

        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1);
        amountText.setValueFactory(valueFactory);

        buyPriceType.getItems().addAll("za sztukę", "za wszystko");
        buyPriceType.getSelectionModel().select(0);

        initListeners();

        refreshSearchTextListeners();
        goToStep(0);

        clearAlerts();
    }

    private void initListeners() {
        searchGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            searchText.getItems().clear();

            if (newValue.equals(idRadioButton)) {
                preferences.putInt("search_type", 0);
                searchText.getItems().addAll(ItemRepository.getAllIDs());

            } else {
                preferences.putInt("search_type", 1);
                searchText.getItems().addAll(ItemRepository.getAllNames());
            }

            if (autoCompletionSearch != null)
                autoCompletionSearch.dispose();
            autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());
            refreshSearchTextListeners();
        });

        autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());

        onlyNumericListener = ComboBoxListeners.onlyNumericListener(searchText);
        maxIdCharsAmountListener = ComboBoxListeners.maxCharsAmountListener(searchText, 4);
        maxNameCharsAmountListener = ComboBoxListeners.maxCharsAmountListener(searchText, 250);
        fillWithZerosListener = ComboBoxListeners.fillWithZerosListener(searchText, 4);

        searchText.focusedProperty().addListener(ComboBoxListeners.autoTrimListener(searchText));
        searchText.focusedProperty().addListener(ComboBoxListeners.removeAlertsListener(searchText.getParent(), idNotExistAlert, nameNotExistAlert));

        amountText.getEditor().textProperty().addListener(SpinnerListeners.onlyNumericListener(amountText));
        amountText.focusedProperty().addListener(SpinnerListeners.autoFillListener(amountText, 1));
        amountText.getEditor().textProperty().addListener(TextFieldListeners.setBuyPriceDependOnTypeListener(buyPriceText,
                buyPricePerItemText, amountText, buyPriceType));

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

        buyPriceText.textProperty().addListener(TextFieldListeners.priceListener(buyPriceText));
        buyPriceText.textProperty().addListener(TextFieldListeners.setBuyPriceDependOnTypeListener(buyPriceText,
                buyPricePerItemText, amountText, buyPriceType));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.autoFillPriceListener(buyPriceText, "0.00"));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.isNullListener(buyPriceText, buyNullAlert,
                (Pane) buyPriceText.getParent().getParent()));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(buyPriceText));
        buyPriceText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(
                buyPriceText.getParent().getParent(), buyNullAlert));

        sellPriceText.textProperty().addListener(TextFieldListeners.priceListener(sellPriceText));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.autoFillPriceListener(sellPriceText, "0.00"));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.isNullListener(sellPriceText, sellNullAlert));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.autoTrimListener(sellPriceText));
        sellPriceText.focusedProperty().addListener(TextFieldListeners.removeAlertsListener(sellPriceText.getParent(), sellNullAlert));
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
                selectedItem = ItemRepository.findById(Integer.parseInt(searchText.getEditor().getText()));
            } else {
                selectedItem = ItemRepository.findByName(searchText.getEditor().getText()).get(0);
            }
            if (selectedItem == null)
                throw new NullPointerException();

            Price lastedBuyPrice = PriceRepository.getLastedForItem(selectedItem, PriceType.BUY);
            if (lastedBuyPrice != null) {
                buyPriceText.setText(lastedBuyPrice.getValue().toString());
            }

            Price lastedSellPrice = PriceRepository.getLastedForItem(selectedItem, PriceType.SELL);
            if (lastedSellPrice != null) {
                sellPriceText.setText(lastedSellPrice.getValue().toString());
            }

            if (checkIfItemIsAlreadyAdded(selectedItem)) {
                if (!showConfirmationDialog("Powtórzenie produktu", "Wybrany produkt znajduję się już na liście. Chcesz kontynuować?"))
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
            if (!showConfirmationDialog("Liczba sztuk równa zero", "Zakupiona liczba sztuk jest równa 0. Chcesz kontynuować?"))
                return;

        if (Double.valueOf(buyPriceText.getText()) == 0.0)
            if (!showConfirmationDialog("Cena kupna równa zero", "Cena kupna jest równa 0.00zł. Chcesz kontynuować?"))
                return;

        if (Double.valueOf(sellPriceText.getText()) == 0.0)
            if (!showConfirmationDialog("Cena sprzedaży równa zero", "Cena sprzedaży jest równa 0.00zł. Chcesz kontynuować?"))
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

    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }

    }

    public void changeSelectedDetails(ActionEvent event) {
        goToStep(1);
    }

    /**
     * @param step 0 - search item, 1 - insert details, 2 - summary
     */
    private void goToStep(int step) {
        clearAlerts();
        buyItemPane.getChildren().removeAll(searchInputPane, searchPane, detailsInputPane, detailsPane);
        switch (step) {
            case 0:
                buyItemPane.getChildren().addAll(searchInputPane);
                selectedItemId.setText("");
                selectedItemName.setText("");
                setItemTitle();
                break;
            case 1:
                buyItemPane.getChildren().addAll(searchPane, detailsInputPane);
                selectedItemId.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                selectedItemName.setText(String.valueOf(selectedItem.getName()));
                itemTitle.setText(selectedItemId.getText() + ". " + selectedItemName.getText());

                if (buyPriceType.getSelectionModel().getSelectedIndex() != 0) {
                    ((Pane) buyPriceText.getParent().getParent()).getChildren().add(buyPricePerItemPane);
                }
                break;

            case 2:
                buyItemPane.getChildren().addAll(searchPane, detailsPane);
                break;
        }
    }

    public void setElementId(int elementId) {
        this.elementId = elementId;
        if (itemTitle.getText().matches("Produkt [\\d]+")) {
            setItemTitle();
        }
    }

    public void setItemTitle() {
        itemTitle.setText("Produkt " + elementId);
    }

    public void setBuyItemController(BuyItemController buyItemController) {
        this.buyItemController = buyItemController;
    }

    public void removeItem(ActionEvent event) {
        buyItemController.removeItem(this);
    }

    public int getSelectedId() {
        return Integer.valueOf((selectedItemId.getText().equals("") ? "0" : selectedItemId.getText()));
    }

    public String getSelectedName() {
        return selectedItemName.getText();
    }

    public TitledPane getItemPane() {
        return itemPane;
    }
}
