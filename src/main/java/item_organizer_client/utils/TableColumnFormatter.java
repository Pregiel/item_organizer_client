package item_organizer_client.utils;

import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class TableColumnFormatter {
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> priceFormat() {
        return param -> new TableCell<S, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    if (item instanceof BigDecimal) {
                        setText(Price.priceFormat((BigDecimal) item));
                    } else if (item instanceof Price) {
                        setText(((Price) item).priceFormat());
                    }
                }
            }
        };
    }

    public static <S> Callback<TableColumn<S, Timestamp>, TableCell<S, Timestamp>> dateFormat() {
        return param -> new TableCell<S, Timestamp>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.toLocalDateTime().format(Utils.getDateFormatter()));
                }
            }
        };
    }

    public static <S> Callback<TableColumn<S, Integer>, TableCell<S, Integer>> idFormat() {
        return param -> new TableCell<S, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(Utils.fillWithZeros(item, Item.ID_DIGITS));
                }
            }
        };
    }
}
