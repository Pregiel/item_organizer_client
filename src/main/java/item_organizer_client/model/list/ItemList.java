package item_organizer_client.model.list;

import item_organizer_client.controller.item_list.ItemListController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import item_organizer_client.model.element.ItemTableElement;
import item_organizer_client.utils.Utils;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ItemList {
    @Autowired
    private ItemService itemService;

    private ObservableList<Item> itemList;
    private FilteredList<ItemTableElement> filteredItemList;
    private ListChangeListener<Item> listChangeListener;

    private static ItemList instance;

    public static ItemList getInstance() {
        if (instance == null) {
            instance = new ItemList();
            instance.init();
        }
        return instance;
    }

    public ItemList() {
        instance = this;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void init() {
        itemList = FXCollections.observableList(itemService.getAll());
    }

    public void refresh() {
        itemList.clear();
        itemList.addAll(itemService.getAll());
    }

    public void addListener(Runnable runnable) {
        removeListener();
        listChangeListener = c -> runnable.run();
        itemList.addListener(listChangeListener);
    }

    private void removeListener() {
        if (listChangeListener != null) {
            itemList.removeListener(listChangeListener);
        }
        listChangeListener = null;
    }

    public FilteredList<ItemTableElement> getFilteredItemList() {
        return filteredItemList;
    }

    public void setFilteredItemList(FilteredList<ItemTableElement> filteredItemList) {
        this.filteredItemList = filteredItemList;
    }

    private ObservableValue<? extends Predicate<? super ItemTableElement>> binding;

    public void bindPredicate() {
        if (binding != null) {
            filteredItemList.predicateProperty().unbind();
            filteredItemList.predicateProperty().bind(binding);
        }
    }

    public void setUpSearchBoxFilters(TextField searchBoxField, CheckBox showHiddenProductsCheckBox) {
        ObjectProperty<Predicate<ItemTableElement>> searchBoxFilter = new SimpleObjectProperty<>();
        searchBoxFilter.bind(Bindings.createObjectBinding(() -> item -> {
            String value = searchBoxField.getText().toLowerCase().trim();
            if (value.length() <= 0) {
                return true;
            }

            for (String s : value.split(" ")) {
                if (!Utils.fillWithZeros(item.getNumber(), Item.ID_DIGITS).contains(s) &&
                        !item.getName().toLowerCase().contains(s) &&
                        !item.getCategory().getName().toLowerCase().contains(s)) {
                    return false;
                }
            }
            return true;
        }, searchBoxField.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> showHiddenProductsFilter = new SimpleObjectProperty<>();
        showHiddenProductsFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (!showHiddenProductsCheckBox.isSelected()) {
                return !item.getHidden();
            }
            return true;
        }, showHiddenProductsCheckBox.selectedProperty()));

        binding = Bindings.createObjectBinding(() ->
                        searchBoxFilter.get().and(showHiddenProductsFilter.get()),
                searchBoxFilter, showHiddenProductsFilter);

        bindPredicate();
    }

    public void setUpSearchViewFilters(TextField searchBoxField, CheckBox showHiddenProductsCheckBox, TextField idText,
                                       TextField nameText, ComboBox<String> categoryText, Spinner<Integer> amountFromText,
                                       Spinner<Integer> amountToText, TextField buyPriceFromText,
                                       TextField buyPriceToText, TextField sellPriceFromText, TextField sellPriceToText) {
        ObjectProperty<Predicate<ItemTableElement>> searchBoxFilter = new SimpleObjectProperty<>();
        searchBoxFilter.bind(Bindings.createObjectBinding(() -> item -> {
            String value = searchBoxField.getText().toLowerCase().trim();
            if (value.length() <= 0) {
                return true;
            }

            for (String s : value.split(" ")) {
                if (!Utils.fillWithZeros(item.getNumber(), Item.ID_DIGITS).contains(s) &&
                        !item.getName().toLowerCase().contains(s) &&
                        !item.getCategory().getName().toLowerCase().contains(s)) {
                    return false;
                }
            }
            return true;
        }, searchBoxField.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> showHiddenProductsFilter = new SimpleObjectProperty<>();
        showHiddenProductsFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (!showHiddenProductsCheckBox.isSelected()) {
                return !item.getHidden();
            }
            return true;
        }, showHiddenProductsCheckBox.selectedProperty()));

        ObjectProperty<Predicate<ItemTableElement>> idFilter = new SimpleObjectProperty<>();
        idFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (idText.getText().equals("")) {
                return true;
            } else if (idText.getText().matches("\\d*"))
                return Utils.fillWithZeros(item.getNumber(), Item.ID_DIGITS).contains(idText.getText());
            return false;
        }, idText.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> nameFilter = new SimpleObjectProperty<>();
        nameFilter.bind(Bindings.createObjectBinding(() -> item ->
                        item.getName().toLowerCase().contains(nameText.getText().toLowerCase()),
                nameText.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> categoryFilter = new SimpleObjectProperty<>();
        categoryFilter.bind(Bindings.createObjectBinding(() -> item ->
                        item.getCategory().getName().toLowerCase().contains(categoryText.getEditor().getText().toLowerCase()),
                categoryText.getEditor().textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> amountFromFilter = new SimpleObjectProperty<>();
        amountFromFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (amountFromText.getEditor().getText().equals("")) {
                return true;
            } else if (amountFromText.getEditor().getText().matches("\\d*"))
                return item.getAmount().compareTo(Integer.valueOf(amountFromText.getEditor().getText())) >= 0;
            return false;
        }, amountFromText.getEditor().textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> amountToFilter = new SimpleObjectProperty<>();
        amountToFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (amountToText.getEditor().getText().equals("")) {
                return true;
            } else if (amountToText.getEditor().getText().matches("\\d*"))
                return item.getAmount().compareTo(Integer.valueOf(amountToText.getEditor().getText())) <= 0;
            return false;
        }, amountToText.getEditor().textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> buyPriceFromFilter = new SimpleObjectProperty<>();
        buyPriceFromFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (buyPriceFromText.getText().equals("")) {
                return true;
            } else if (buyPriceFromText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return item.getBuyPrice().getValue().compareTo(new BigDecimal(buyPriceFromText.getText())) >= 0;
            return false;
        }, buyPriceFromText.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> buyPriceToFilter = new SimpleObjectProperty<>();
        buyPriceToFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (buyPriceToText.getText().equals("")) {
                return true;
            } else if (buyPriceToText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return item.getBuyPrice().getValue().compareTo(new BigDecimal(buyPriceToText.getText())) <= 0;
            return false;
        }, buyPriceToText.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> sellPriceFromFilter = new SimpleObjectProperty<>();
        sellPriceFromFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (sellPriceFromText.getText().equals("")) {
                return true;
            } else if (sellPriceFromText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return item.getSellPrice().getValue().compareTo(new BigDecimal(sellPriceFromText.getText())) >= 0;
            return false;
        }, sellPriceFromText.textProperty()));

        ObjectProperty<Predicate<ItemTableElement>> sellPriceToFilter = new SimpleObjectProperty<>();
        sellPriceToFilter.bind(Bindings.createObjectBinding(() -> item -> {
            if (sellPriceToText.getText().equals("")) {
                return true;
            } else if (sellPriceToText.getText().matches("[\\d]*[.]?[\\d]{0,2}"))
                return item.getSellPrice().getValue().compareTo(new BigDecimal(sellPriceToText.getText())) <= 0;
            return false;
        }, sellPriceToText.textProperty()));


        binding = Bindings.createObjectBinding(() ->
                        searchBoxFilter.get().and(showHiddenProductsFilter.get()).and(idFilter.get()).and(nameFilter.get()).and(categoryFilter.get()).and(amountFromFilter.get())
                                .and(amountToFilter.get()).and(buyPriceFromFilter.get()).and(buyPriceToFilter.get())
                                .and(sellPriceFromFilter.get()).and(sellPriceToFilter.get()),
                searchBoxFilter, showHiddenProductsFilter, idFilter, nameFilter, categoryFilter, amountFromFilter,
                amountToFilter, buyPriceFromFilter, buyPriceToFilter, sellPriceFromFilter, sellPriceToFilter);
    }

    public void updateItem(Item item) {
        itemService.update(item);
        ItemListController.getInstance().getItemTableView().refresh();
        ItemListController.getInstance().getShowHiddenProductsCheckBox().fire();
        ItemListController.getInstance().getShowHiddenProductsCheckBox().fire();
    }
}
