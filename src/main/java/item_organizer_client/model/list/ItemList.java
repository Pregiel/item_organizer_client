package item_organizer_client.model.list;

import item_organizer_client.database.repository.ItemRepository;
import item_organizer_client.model.Item;
import item_organizer_client.model.table_item.ItemTableItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class ItemList {
    private static ObservableList<Item> itemList;
    private static ListChangeListener<Item> listChangeListener;

    public static List<Item> getItemList() {
        return itemList;
    }

    public static List<ItemTableItem> getItemListAsTableItems() {
        return itemList
                .stream()
                .map(ItemTableItem::new)
                .collect(Collectors.toList());
    }

    public static void init() {
        itemList = FXCollections.observableList(ItemRepository.getAll());
    }

    public static void refresh() {
        itemList.clear();
        itemList.addAll(ItemRepository.getAll());
    }

    public static void addListener(Runnable runnable) {
        removeListener();
        listChangeListener = c -> runnable.run();
        itemList.addListener(listChangeListener);
    }

    private static void removeListener() {
        if (listChangeListener != null) {
            itemList.removeListener(listChangeListener);
        }
        listChangeListener = null;
    }
}
