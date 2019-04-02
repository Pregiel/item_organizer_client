package item_organizer_client;

import item_organizer_client.model.Item;
import item_organizer_client.model.ItemTableItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemList {
    private static List<Item> itemList;

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
        itemList = new ArrayList<>();
    }

    public static void add(Item item) {
        itemList.add(item);
    }
}
