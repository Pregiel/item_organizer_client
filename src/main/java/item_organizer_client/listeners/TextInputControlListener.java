package item_organizer_client.listeners;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextInputControlListener extends ControlListener {

    public static ChangeListener<String> onlyNumericListener(TextField textField) {
        return handleListener(textField.textProperty(), (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public static ChangeListener<String> maxCharsAmountListener(TextField textField, int max) {
        return handleListener(textField.textProperty(), (observable, oldValue, newValue) -> {
            if (newValue.length() > max) {
                textField.setText(newValue.substring(0, max));
            }
        });
    }

    public static ChangeListener<Boolean> selectAllOnFocusListener(TextField textField) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue && !textField.getText().isEmpty()) {
                    textField.selectAll();
                }
            });
        });
    }

    public static ChangeListener<Boolean> fillWithZerosListener(TextField textField, int digits) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() < digits && textField.getText().length() > 0) {
                    int diff = digits - textField.getText().length();
                    textField.setText(
                            IntStream.range(0, diff).mapToObj(i -> "0").collect(Collectors.joining(""))
                                    + textField.getText());
                }
            }
        });
    }

    public static ChangeListener<Boolean> minCharsAmountListener(TextField textField, int min, Label alertLabel) {
        return minCharsAmountListener(textField, min, alertLabel, (Pane) textField.getParent());
    }

    public static ChangeListener<Boolean> minCharsAmountListener(TextField textField, int min, Label alertLabel, Parent parent) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                textField.setText(textField.getText().trim());
                if (textField.getText().length() < min && textField.getText().length() > 0) {
                    ((Pane) parent).getChildren().add(alertLabel);
                }
            } else {
                ((Pane) parent).getChildren().remove(alertLabel);
            }
        });
    }

    public static ChangeListener<Boolean> isNullListener(TextField textField, Label alertLabel) {
        return isNullListener(textField, alertLabel, (Pane) textField.getParent());
    }

    public static ChangeListener<Boolean> isNullListener(TextField textField, Label alertLabel, Parent parent) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() == 0) {
                    ((Pane) parent).getChildren().add(alertLabel);
                }
            }
        });
    }

    public static ChangeListener<Boolean> autoTrimListener(TextField textField) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                textField.setText(textField.getText().trim());
            }
        });
    }

    public static <T> ChangeListener<Boolean> autoFillListener(TextField textField, T value) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() == 0) {
                    textField.setText(value.toString());
                }
            }
        });
    }

    public static TextInputControlListener onlyReturn() {
        dontAdd();
        return null;
    }
}
