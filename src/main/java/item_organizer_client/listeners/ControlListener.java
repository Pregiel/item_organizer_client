package item_organizer_client.listeners;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ControlListener {
    private static boolean addListener = true;

    public static ChangeListener<Boolean> removeAlertsListener(Control control, Parent parent, Label... labels) {
        return handleListener(control.focusedProperty(), (observable, oldValue, newValue) -> {
            if (newValue) {
                ((Pane) parent).getChildren().removeAll(labels);
            }
        });
    }

    protected static <T> ChangeListener<T> handleListener(ReadOnlyProperty<T> property, ChangeListener<T> listener) {
        if (addListener) {
            property.addListener(listener);
        }
        addListener = true;
        return listener;
    }

    static void dontAdd() {
        addListener = false;
    }

    public static ControlListener onlyReturn() {
        dontAdd();
        return null;
    }
}
