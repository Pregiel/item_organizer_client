package item_organizer_client.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
}
