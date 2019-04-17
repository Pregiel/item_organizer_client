package item_organizer_client.controller;

import item_organizer_client.database.service.CategoryService;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.listeners.*;
import item_organizer_client.model.Category;
import item_organizer_client.model.Price;
import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.commons.lang3.ArrayUtils;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import tornadofx.control.DateTimePicker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public abstract class SideBarMenuViewController extends Controller {
    protected abstract void clearAlerts();

    protected abstract void initFields();

    protected void setIdTextFieldListeners(TextField idText, int digits, Parent parent, Label nullAlert, Label... alerts) {
        TextFieldListener.selectAllOnFocusListener(idText);
        TextFieldListener.onlyNumericListener(idText);
        TextFieldListener.maxCharsAmountListener(idText, digits);
        TextFieldListener.fillWithZerosListener(idText, digits);
        TextFieldListener.isNullListener(idText, nullAlert);
        TextFieldListener.autoTrimListener(idText);
        TextFieldListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{nullAlert}, alerts));
    }

    protected void setIdComboBoxListeners(ComboBox<String> searchText,TransactionService transactionService, Parent parent, Label idNotExistAlert, Label... alerts) {
        ComboBoxListener.selectAllOnFocusListener(searchText);
        ComboBoxListener.onlyNumericListener(searchText);
        ComboBoxListener.autoTrimListener(searchText);
        ComboBoxListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{idNotExistAlert}, alerts));

        searchText.getItems().addAll(transactionService.getAllIDs());
        TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());
    }

    protected void setNameTextFieldListeners(TextField nameText, int min, int max, Parent parent, Label nullAlert, Label minAlert, Label... alerts) {
        TextFieldListener.selectAllOnFocusListener(nameText);
        TextFieldListener.maxCharsAmountListener(nameText, max);
        TextFieldListener.minCharsAmountListener(nameText, min, minAlert);
        TextFieldListener.isNullListener(nameText, nullAlert);
        TextFieldListener.autoTrimListener(nameText);
        TextFieldListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{nullAlert, minAlert}, alerts));
    }

    protected void setCategoryComboBoxListeners(ComboBox<String> categoryText, int min, int max,
                                                CategoryService categoryService, Parent parent, Label nullAlert,
                                                Label minAlert, Label... alerts) {
        categoryText.getItems().addAll(categoryService.getAll()
                .stream().map(Category::getName).collect(Collectors.toList()));
        TextFields.bindAutoCompletion(categoryText.getEditor(), categoryText.getItems());

        ComboBoxListener.selectAllOnFocusListener(categoryText);
        ComboBoxListener.maxCharsAmountListener(categoryText, max);
        ComboBoxListener.minCharsAmountListener(categoryText, min, minAlert);
        ComboBoxListener.isNullListener(categoryText, nullAlert);
        ComboBoxListener.autoTrimListener(categoryText);
        ComboBoxListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{nullAlert, minAlert}, alerts));
    }

    protected void setAmountSpinnerListeners(Spinner<Integer> amountText, int defaultValue, Parent parent, Label nullAlert, Label... alerts) {
        SpinnerValueFactory<Integer> valueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 1);
        amountText.setValueFactory(valueFactory);

        SpinnerListener.selectAllOnFocusListener(amountText);
        SpinnerListener.onlyNumericListener(amountText);
        SpinnerListener.autoFillListener(amountText, defaultValue);
        SpinnerListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{nullAlert}, alerts));
    }

    protected void setPriceTextFieldListeners(TextField priceText, String defaultPrice, Parent parent, Label nullAlert, Label... alerts) {
        TextFieldListener.selectAllOnFocusListener(priceText);
        TextFieldListener.priceListener(priceText);
        TextFieldListener.autoFillPriceListener(priceText, defaultPrice);
        TextFieldListener.isNullListener(priceText, nullAlert, (Pane) parent);
        TextFieldListener.autoTrimListener(priceText);
        TextFieldListener.removeAlertsListener(parent, ArrayUtils.addAll(new Label[]{nullAlert}, alerts));
    }

    protected void setDateDatePickerListeners(DateTimePicker dateText) {
        DatePickerListener.selectAllOnFocusListener(dateText);
        DatePickerListener.autoFillDateListener(dateText);
    }

    private ChangeListener<String> onlyNumericListener, maxIdCharsAmountListener, maxNameCharsAmountListener;
    private ChangeListener<Boolean> fillWithZerosListener;
    private ComboBox<String> searchText;
    private ToggleGroup searchGroup;
    private RadioButton idRadioButton, nameRadioButton;
    private AutoCompletionBinding<String> autoCompletionSearch;

    @SuppressWarnings({"AccessStaticViaInstance", "ConstantConditions"})
    protected void setItemSearchComboBox(ComboBox<String> searchText, int idDigits, int nameMax, ToggleGroup searchGroup,
                                         RadioButton idRadioButton, RadioButton nameRadioButton,
                                         ItemService itemService, Label idNotExistAlert, Label nameNotExistAlert) {
        this.searchText = searchText;
        this.searchGroup = searchGroup;
        this.idRadioButton = idRadioButton;
        this.nameRadioButton = nameRadioButton;

        int selectedSearchType = getPreferences().getInt("search_type", 1);

        if (selectedSearchType == 0) {
            searchText.getItems().addAll(itemService.getAllIDs());
            searchGroup.selectToggle(idRadioButton);
        } else {
            searchText.getItems().addAll(itemService.getAllNames());
            searchGroup.selectToggle(nameRadioButton);
        }

        searchGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            searchText.getItems().clear();

            if (newValue.equals(idRadioButton)) {
                getPreferences().putInt("search_type", 0);
                searchText.getItems().addAll(itemService.getAllIDs());
            } else if (newValue.equals(nameRadioButton)) {
                getPreferences().putInt("search_type", 1);
                searchText.getItems().addAll(itemService.getAllNames());
            }

            if (autoCompletionSearch != null)
                autoCompletionSearch.dispose();
            autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());
            refreshSearchTextListeners();
        });

        autoCompletionSearch = TextFields.bindAutoCompletion(searchText.getEditor(), searchText.getItems());

        onlyNumericListener = ComboBoxListener.onlyReturn().onlyNumericListener(searchText);
        maxIdCharsAmountListener = ComboBoxListener.onlyReturn().maxCharsAmountListener(searchText, idDigits);
        maxNameCharsAmountListener = ComboBoxListener.onlyReturn().maxCharsAmountListener(searchText, nameMax);
        fillWithZerosListener = ComboBoxListener.onlyReturn().fillWithZerosListener(searchText, idDigits);

        ComboBoxListener.autoTrimListener(searchText);
        ComboBoxListener.removeAlertsListener(searchText.getParent(), idNotExistAlert, nameNotExistAlert);
    }

    protected void refreshSearchTextListeners() {
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

    protected void setPriceTypeListeners(TextField priceText, ComboBox<String> priceType, Parent parent,
                                         Pane pricePerItemPane, Label pricePerItemText, Spinner<Integer> amountText) {
        priceType.getItems().clear();
        priceType.getItems().addAll(ResourceBundle.getBundle("strings").getString("price.perItem"),
                ResourceBundle.getBundle("strings").getString("price.total"));
        priceType.getSelectionModel().select(0);

        ((Pane) parent).getChildren().remove(pricePerItemPane);
        priceType.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 0) {
                ((Pane) parent).getChildren().remove(pricePerItemPane);
            } else {
                ((Pane) parent).getChildren().add(pricePerItemPane);
                BigDecimal pricePerItem = new BigDecimal(priceText.getText()).divide(
                        new BigDecimal(amountText.getEditor().getText()), 2, RoundingMode.CEILING);
                pricePerItemText.setText(Price.priceFormat(pricePerItem));
            }
        });

        CustomListener.updateBuyPerItemLabelListener(pricePerItemText, priceText, amountText, priceType);
    }
}
