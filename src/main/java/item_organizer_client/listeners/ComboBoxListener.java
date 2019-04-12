package item_organizer_client.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;


public class ComboBoxListener extends TextInputControlListener {
    public static ChangeListener<String> onlyNumericListener(ComboBox<String> comboBox) {
        return onlyNumericListener(comboBox.getEditor());
    }

    public static ChangeListener<Boolean> fillWithZerosListener(ComboBox<String> comboBox, int digits) {
        return fillWithZerosListener(comboBox.getEditor(), digits);
    }

    public static ChangeListener<String> maxCharsAmountListener(ComboBox<String> comboBox, int max) {
        return maxCharsAmountListener(comboBox.getEditor(), max);
    }

    public static ChangeListener<Boolean> minCharsAmountListener(ComboBox<String> comboBox, int min, Label alertLabel) {
        return minCharsAmountListener(comboBox.getEditor(), min, alertLabel, (Pane) comboBox.getParent().getParent());
    }

    public static ChangeListener<Boolean> isNullListener(ComboBox<String> comboBox, Label alertLabel) {
        return isNullListener(comboBox.getEditor(), alertLabel, (Pane) comboBox.getParent().getParent());
    }

    public static ChangeListener<Boolean> autoTrimListener(ComboBox<String> comboBox) {
        return autoTrimListener(comboBox.getEditor());
    }

    public static ChangeListener<Boolean> selectAllOnFocusListener(ComboBox<String> comboBox) {
        return selectAllOnFocusListener(comboBox.getEditor());
    }

    public static ComboBoxListener onlyReturn() {
        dontAdd();
        return null;
    }
}
