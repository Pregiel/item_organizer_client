package item_organizer_client.database.service;

import item_organizer_client.model.Item;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ItemService {
    Item add(Item item);

    List<Item> addAll(Item... items) ;

//    public static List<Item> addAll(List<Item> itemList) {
//        return Repos.addAll(itemList);
//    }

    Item findById(int id) ;

    List<Item> findByName(String name);

    List<Item> getAll() ;

    List<String> getAllIDs();

    List<String> getAllNames();

    Item update(Item item);

    List<Item> updateAll(List<Item> itemList);
}
