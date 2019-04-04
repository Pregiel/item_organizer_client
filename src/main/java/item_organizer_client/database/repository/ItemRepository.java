package item_organizer_client.database.repository;

import item_organizer_client.model.Item;
import java.util.List;


public class ItemRepository extends Repository{

    public static Item add(Item item) {
        return Repository.add(item);
    }

    public static Item findById(int id) {
        return findById(Item.class, id);
    }

    public static List<Item> findByName(String name) {
        return findBy(Item.class, "name", name);
    }

    public static List<Item> getAll() {
        return getAll(Item.class);
    }
}
