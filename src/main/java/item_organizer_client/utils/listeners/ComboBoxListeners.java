package item_organizer_client.utils.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ComboBoxListeners extends ControlListeners {
    public static ChangeListener<String> onlyNumericListener(ComboBox<String> comboBox) {
        return (observable, oldValue, newValue) -> {
            if (!comboBox.getEditor().getText().matches("\\d*")) {
                comboBox.getEditor().setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    public static ChangeListener<Boolean> fillWithZerosListener(ComboBox<String> comboBox, int digits) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (comboBox.getEditor().getText().length() < digits && comboBox.getEditor().getText().length() > 0) {
                    int diff = digits - comboBox.getEditor().getText().length();
                    comboBox.getEditor().setText(
                            IntStream.range(0, diff).mapToObj(i -> "0").collect(Collectors.joining(""))
                                    + comboBox.getEditor().getText());
                }
            }
        };
    }

    public static ChangeListener<String> maxCharsAmountListener(ComboBox<String> comboBox, int max) {
        return (observable, oldValue, newValue) -> {
            if (comboBox.getEditor().getText().length() > max) {
                comboBox.getEditor().setText(newValue.substring(0, max));
            }
        };
    }

    public static ChangeListener<Boolean> minCharsAmountListener(ComboBox<String> comboBox, int min, Label alertLabel) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                comboBox.getEditor().setText(comboBox.getEditor().getText().trim());
                if (comboBox.getEditor().getText().length() < min && comboBox.getEditor().getText().length() > 0) {
                    ((Pane) comboBox.getParent()).getChildren().add(alertLabel);
                }
            } else {
                ((Pane) comboBox.getParent()).getChildren().remove(alertLabel);
            }
        };
    }

    public static ChangeListener<Boolean> isNullListener(ComboBox<String> comboBox, Label alertLabel) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (comboBox.getEditor().getText().length() == 0) {
                    ((Pane) comboBox.getParent()).getChildren().add(alertLabel);
                }
            }
        };
    }

    public static ChangeListener<Boolean> autoTrimListener(ComboBox<String> comboBox) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                comboBox.getEditor().setText(comboBox.getEditor().getText().trim());
            }
        };
    }

    private static void checkLaterIfNull(ComboBox<String> comboBox, Runnable runnable) {
        if (comboBox.getValue() == null) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (comboBox.getValue() != null) {
            runnable.run();
        }
    }
}
