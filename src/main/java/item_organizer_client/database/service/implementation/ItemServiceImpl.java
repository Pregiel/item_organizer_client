package item_organizer_client.database.service.implementation;

import item_organizer_client.database.Repository;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private Repository<Item> itemRepository;

    @Override
    public Item add(Item item) {
        return itemRepository.add(item);
    }

    @Override
    public List<Item> addAll(Item... items) {
        return itemRepository.addAll(items);
    }

//    public List<Item> addAll(List<Item> itemList) {
//        return addAll(itemList.toArray(new Item[0]));
//    }

    @Override
    public Item findById(int id) {
        return itemRepository.findById(Item.class, id);
    }

    @Override
    public List<Item> findByName(String name) {
        return itemRepository.findBy(Item.class, "name", name);
    }

    @Override
    public List<Item> getAll() {
        return itemRepository.getAll(Item.class);
    }

    @Override
    public List<String> getAllIDs() {
        return itemRepository.getAllColumn(Item.class, "id");
    }

    @Override
    public List<String> getAllNames() {
        return itemRepository.getAllColumn(Item.class, "name");
    }

}
