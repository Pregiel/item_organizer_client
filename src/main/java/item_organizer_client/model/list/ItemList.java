package item_organizer_client.model.list;

import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import item_organizer_client.model.table_item.ItemTableItem;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemList {
    @Autowired
    private ItemService itemService;

    private ObservableList<Item> itemList;
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

    public List<ItemTableItem> getItemListAsTableItems() {
        return itemList
                .stream()
                .map((Item item) -> new ItemTableItem(item))
                .collect(Collectors.toList());
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
}
