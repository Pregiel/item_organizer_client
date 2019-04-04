package item_organizer_client.utils.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class TextFieldListeners extends ControlListeners {
    public static ChangeListener<String> onlyNumericListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        };
    }

    public static ChangeListener<String> priceListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            String newText = newValue;

            if (newText.contains(".")) {
                newText = newText.replace(".", ",");
            }

            if (!newText.matches("[\\d]*[,.]?[\\d]{0,2}")) {
                newText = newText.replaceAll("[^\\d,.]", "");

                if (newText.matches("[\\d]*[,.]?[\\d][\\d]+")) {
                    newText = newText.substring(0, newValue.indexOf(",") + 3);
                }

                if (newText.indexOf(",") != newValue.lastIndexOf(",")) {
                    newText = newText.substring(0, newText.lastIndexOf(","));
                }
            }
            textField.setText(newText);
        };
    }

    public static ChangeListener<String> maxCharsAmountListener(TextField textField, int max) {
        return (observable, oldValue, newValue) -> {
            if (newValue.length() > max) {
                textField.setText(newValue.substring(0, max));
            }
        };
    }

    public static ChangeListener<Boolean> fillWithZerosListener(TextField textField, int digits) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() < digits && textField.getText().length() > 0) {
                    int diff = digits - textField.getText().length();
                    textField.setText(
                            IntStream.range(0, diff).mapToObj(i -> "0").collect(Collectors.joining(""))
                                    + textField.getText());
                }
            }
        };
    }

    public static ChangeListener<Boolean> minCharsAmountListener(TextField textField, int min, Label alertLabel) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                textField.setText(textField.getText().trim());
                if (textField.getText().length() < min && textField.getText().length() > 0) {
                    ((Pane) textField.getParent()).getChildren().add(alertLabel);
                }
            } else {
                ((Pane) textField.getParent()).getChildren().remove(alertLabel);
            }
        };
    }

    public static ChangeListener<Boolean> autoFillPriceListener(TextField textField, String text) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() == 0) {
                    textField.setText(text);
                } else if (!textField.getText().matches(".*(,)\\d{2}")) {
                    if (!textField.getText().contains(",")) {
                        textField.setText(textField.getText() + ",00");
                    } else {
                        textField.setText(textField.getText() + 0);
                        if (!textField.getText().matches(".*(,)\\d{2}")) {
                            textField.setText(textField.getText() + 0);
                        }
                    }
                }
            }
        };
    }

    public static ChangeListener<Boolean> isNullListener(TextField textField, Label alertLabel) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() == 0) {
                    ((Pane) textField.getParent()).getChildren().add(alertLabel);
                }
            }
        };
    }

    public static ChangeListener<Boolean> autoTrimListener(TextField textField) {
        return (observable, oldValue, newValue) -> {
            if (!newValue) {
                textField.setText(textField.getText().trim());
            }
        };
    }
}
