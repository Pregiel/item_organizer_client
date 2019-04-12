package item_organizer_client.database.service.implementation;

import item_organizer_client.database.QueryParameter;
import item_organizer_client.database.Repository;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.PriceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceServiceImpl implements PriceService {
    @Autowired
    private Repository<Price> priceRepository;

    @Override
    public Price add(Price price) {
        return priceRepository.add(price);
    }

    @Override
    public List<Price> addAll(Price... prices) {
        return priceRepository.addAll(prices);
    }

    @Override
    public List<Price> addAll(List<Price> priceList) {
        return priceRepository.addAll(priceList);
    }

    @Override
    public Price findById(int id) {
        return priceRepository.findById(Price.class, id);
    }

    @Override
    public List<Price> findByValue(String value) {
        return priceRepository.findBy(Price.class, "value", value);
    }

    @Override
    public Price getLastedForItem(Item item, PriceType priceType) {
        List<Price> list = priceRepository.findByQuery(
                "FROM Price p " +
                        "WHERE p.item = :item_id AND p.type = :type " +
                        "ORDER BY p.date DESC, p.id ASC",
                new QueryParameter<>("item_id", item),
                new QueryParameter<>("type", priceType));

        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<Price> getAll() {
        return priceRepository.getAll(Price.class);
    }
}
