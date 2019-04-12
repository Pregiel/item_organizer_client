package item_organizer_client.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


public class SpinnerListener extends TextInputControlListener {
    public static ChangeListener<Boolean> isNullListener(Spinner spinner, Label alertLabel) {
        return TextInputControlListener.isNullListener(spinner.getEditor(), alertLabel);
    }

    public static ChangeListener<Boolean> isNullListener(Spinner spinner, Label alertLabel, Pane parent) {
        return TextInputControlListener.isNullListener(spinner.getEditor(), alertLabel, parent);
    }

    public static <T> ChangeListener<String> onlyNumericListener(Spinner<T> spinner) {
        return TextInputControlListener.onlyNumericListener(spinner.getEditor());
    }

    public static <T> ChangeListener<Boolean> autoFillListener(Spinner<T> spinner, T value) {
        return TextInputControlListener.autoFillListener(spinner.getEditor(), value);
    }

    public static <T> ChangeListener<Boolean> selectAllOnFocusListener(Spinner<T> spinner) {
        return selectAllOnFocusListener(spinner.getEditor());
    }

    public static SpinnerListener onlyReturn() {
        dontAdd();
        return null;
    }
}
