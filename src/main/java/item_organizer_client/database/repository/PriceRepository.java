package item_organizer_client.database.repository;


import item_organizer_client.database.repository.parameter.QueryParameter;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;

import java.util.List;

public class PriceRepository extends Repository {
    public static Price add(Price price) {
        return Repository.add(price);
    }

    public static Price findById(int id) {
        return findById(Price.class, id);
    }

    public static List<Price> findByValue(String value) {
        return findBy(Price.class, "value", value);
    }

    public static Price getLastedForItem(Item item) {
        List<Price> list = findByQuery(
                "FROM Price p " +
                        "WHERE p.item = :item_id AND p.type = :type " +
                        "ORDER BY p.date DESC, p.id ASC",
                new QueryParameter<>("item_id", item),
                new QueryParameter<>("type", PriceType.SELL));

        return list.size() > 0 ? list.get(0) : null;
    }

    public static List<Price> getAll() {
        return getAll(Price.class);
    }
}
