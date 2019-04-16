package item_organizer_client.utils;

import item_organizer_client.model.table_item.ItemTableItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Utils {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String fillWithZeros(Object value, int digits) {
        int diff = digits - value.toString().length();
        return IntStream.range(0, diff).mapToObj(i -> "0").collect(Collectors.joining(""))
                        + value.toString();
    }

    public static <T> void setTableColumnWidthProperty(TableView<T> tableView, Preferences preferences) {
        int i = 0;
        for (TableColumn<T, ?> column : tableView.getColumns()) {
            int finalI = i;
            if (column.getColumns().size() > 0) {
                int ii = 0;
                for (TableColumn<T, ?> subcolumn : column.getColumns()) {
                    double width = preferences.getDouble(
                            "column" + i + "_" + ii + "_width",
                            subcolumn.getPrefWidth());

                    subcolumn.setPrefWidth(width);

                    int finalII = ii;
                    subcolumn.widthProperty().addListener((observable, oldValue, newValue) -> {
                        preferences.putDouble("column" + finalI + "_" + finalII + "_width", (Double) newValue);
                    });
                    ii++;
                }
            } else {
                double width = preferences.getDouble(
                        "column" + i + "_width",
                        column.getPrefWidth());

                column.setPrefWidth(width);

                column.widthProperty().addListener((observable, oldValue, newValue) -> {
                    preferences.putDouble("column" + finalI + "_width", (Double) newValue);
                });
                i++;
            }
        }
    }

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }
}
