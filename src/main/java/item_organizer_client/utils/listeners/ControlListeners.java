package item_organizer_client.utils.listeners;

import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ControlListeners {
    public static ChangeListener<Boolean> removeAlertsListener(Parent parent, Label... labels) {
        return (observable, oldValue, newValue) -> {
            if (newValue) {
                ((Pane) parent).getChildren().removeAll(labels);
            }
        };
    }
}
