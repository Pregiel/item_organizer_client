package item_organizer_client.utils.listeners;

import item_organizer_client.utils.listeners.ControlListeners;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.Serializable;


public class ComboboxListeners extends ControlListeners {
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
