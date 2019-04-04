package item_organizer_client.utils.listeners;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class DatePickerListeners extends ControlListeners{

    public static ChangeListener<Boolean> autoFillDateListener(DatePicker datePicker) {
        return (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!newValue) {
                    if (datePicker.getValue() == null) {
                        datePicker.setValue(LocalDate.now());
                    }
                }

            });
        };
    }
}
