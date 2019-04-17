package item_organizer_client.model.table_item;

import item_organizer_client.model.Price;
import item_organizer_client.utils.Utils;

public class PriceTableElement extends Price implements Comparable<PriceTableElement> {
    private String formattedDate, price;

    public PriceTableElement(Price price) {
        super(price);
        this.formattedDate = getDate().toLocalDateTime().format(Utils.getDateFormatter());
        this.price = Price.priceFormat(price.getValue());
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public int compareTo(PriceTableElement o) {
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
