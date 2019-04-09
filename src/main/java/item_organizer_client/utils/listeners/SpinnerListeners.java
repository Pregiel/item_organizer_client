package item_organizer_client.utils.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class SpinnerListeners extends ControlListeners {
    public static ChangeListener<String> onlyNumericListener(Spinner spinner) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                spinner.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    public static ChangeListener<Boolean> isNullListener(Spinner spinner, Label alertLabel) {
        return isNullListener(spinner, alertLabel, (Pane) spinner.getParent());
    }

    public static ChangeListener<Boolean> isNullListener(Spinner spinner, Label alertLabel, Pane parent) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (spinner.getEditor().getText().length() == 0) {
                    parent.getChildren().add(alertLabel);
                }
            }
        };
    }

    public static <T> ChangeListener<Boolean> autoFillListener(Spinner<T> spinner, T value) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (spinner.getEditor().getText().length() == 0) {
                    spinner.getEditor().setText(value.toString());
                }
            }
        };
    }

}
