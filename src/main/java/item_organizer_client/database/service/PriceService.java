package item_organizer_client.database.service;

import item_organizer_client.database.QueryParameter;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;

import java.util.List;

public interface PriceService {
    Price add(Price price);

    List<Price> addAll(Price... prices);

    Price findById(int id);

    List<Price> findByValue(String value);

    Price getLastedForItem(Item item, PriceType priceType);

    List<Price> getAll();
}
