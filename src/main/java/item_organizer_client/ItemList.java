package item_organizer_client;

import item_organizer_client.model.Item;
import item_organizer_client.model.ItemTableItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import java.util.ArrayList;
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
        itemList = FXCollections.observableList(new ArrayList<>());
    }

    public static void add(Item item) {
        itemList.add(item);
    }

    public static void addListener(Runnable runnable) {
        removeListener();
        listChangeListener = c -> runnable.run();
        itemList.addListener(listChangeListener);
    }

    public static void removeListener() {
        if (listChangeListener != null) {
            itemList.removeListener(listChangeListener);
        }
        listChangeListener = null;
    }
}
