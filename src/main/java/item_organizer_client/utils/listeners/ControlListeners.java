package item_organizer_client.utils.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ControlListeners {
    public static ChangeListener<Boolean> removeAlertsListener(Control control, Label... labels) {
        return (observable, oldValue, newValue) -> {
            if (newValue) {
                ((Pane) control.getParent()).getChildren().removeAll(labels);
            }
        };
    }

}
