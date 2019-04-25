package item_organizer_client.model.element;

import item_organizer_client.model.Price;

public class PriceTableElement extends Price {
    public PriceTableElement(Price price) {
        super(price);
    }

    @Override
    public int compareTo(Price o) {
        if (getDate() == null || o.getDate() == null) {
            return 0;
        } else if (getDate().compareTo(o.getDate()) == 0) {
            if (getType() == null || o.getType() == null) {
                return 0;
            } else if (getType().compareTo(o.getType()) == 0) {
                if (getId() == null || o.getId() == null) {
                    return 0;
                }
                return getId().compareTo(o.getId());
            }
            return getType().compareTo(o.getType());
        }
        return getDate().compareTo(o.getDate());
    }
}
