package item_organizer_client.listeners;

import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;


public class TextFieldListener extends TextInputControlListener {
    public static ChangeListener<String> priceListener(TextField textField) {
        return handleListener(textField.textProperty(), (observable, oldValue, newValue) -> {
            String newText = newValue;

            newText = newText.replaceAll(",", ".")
                    .replaceAll("[^\\d.]", "");

            if (!newText.matches("[\\d]*[.]?[\\d]{0,2}")) {
                if (newText.indexOf(".") != newText.lastIndexOf(".")) {
                    newText = newText.substring(0, newText.indexOf("."))
                            + newText.substring(newText.indexOf(".") + 1);
                }

                if (newText.matches("[\\d]*[.][\\d][\\d][\\d]+")) {
                    newText = newText.substring(0, newText.indexOf(".") + 3);
                }
            }
            textField.setText(newText);
        });
    }

    public static ChangeListener<Boolean> autoFillPriceListener(TextField textField, String text) {
        return autoFillPriceListener(textField, text, true);
    }

    public static ChangeListener<Boolean> autoFillPriceListener(TextField textField, String text, boolean autoFill) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().length() == 0) {
                    if (autoFill)
                        textField.setText(text);
                } else if (!textField.getText().matches(".*[.]\\d{2}")) {
                    if (!textField.getText().contains(".")) {
                        textField.setText(textField.getText() + ".00");
                    } else {
                        textField.setText(textField.getText() + 0);
                        if (!textField.getText().matches(".*[.]\\d{2}")) {
                            textField.setText(textField.getText() + 0);
                        }
                    }
                }
            }
        });
    }

    public static ChangeListener<Boolean> checkItemIdIfExistListener(TextField textField, ItemService itemService, Integer exclude,
                                                                     Parent parent, Label duplicateAlert) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (textField.getText().matches("\\d{4}")) {
                    if (exclude != null) {
                        if (Utils.fillWithZeros(exclude, Item.ID_DIGITS).equals(textField.getText())) {
                            return;
                        }
                    }

                    if (itemService.findById(Integer.parseInt(textField.getText())) != null) {
                        ((Pane) parent).getChildren().add(duplicateAlert);
                    }
                }
            }
        });
    }

    public static ChangeListener<Boolean> checkItemIdIfExistListener(TextField textField, ItemService itemService,
                                                                     Parent parent, Label duplicateAlert) {
        return checkItemIdIfExistListener(textField, itemService, null, parent, duplicateAlert);
    }

    public static ChangeListener<Boolean> checkItemNameIfExistListener(TextField textField, ItemService itemService, String exclude,
                                                                       Parent parent, Label duplicateAlert) {
        return handleListener(textField.focusedProperty(), (observable, oldValue, newValue) -> {
            if (!newValue) {
                if (exclude != null) {
                    if (exclude.equals(textField.getText())) {
                        return;
                    }
                }

                if (itemService.findByName(textField.getText()) != null) {
                    ((Pane) parent).getChildren().add(duplicateAlert);
                }
            }
        });
    }

    public static ChangeListener<Boolean> checkItemNameIfExistListener(TextField textField, ItemService itemService,
                                                                       Parent parent, Label duplicateAlert) {
        return checkItemNameIfExistListener(textField, itemService, null, parent, duplicateAlert);
    }

    public static TextFieldListener onlyReturn() {
        dontAdd();
        return null;
    }
}
