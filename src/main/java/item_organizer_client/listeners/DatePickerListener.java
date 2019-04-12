package item_organizer_client.listeners;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import tornadofx.control.DateTimePicker;

import java.time.LocalDate;

public class DatePickerListener extends TextInputControlListener {

    public static ChangeListener<Boolean> autoFillDateListener(DatePicker datePicker) {
        return handleListener(datePicker.focusedProperty(), (observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!newValue) {
                    if (datePicker.getValue() == null) {
                        datePicker.setValue(LocalDate.now());
                    }
                }

            });
        });
    }

    public static ChangeListener<Boolean> selectAllOnFocusListener(DatePicker datePicker) {
        return selectAllOnFocusListener(datePicker.getEditor());
    }

    public static DatePickerListener onlyReturn() {
        dontAdd();
        return null;
    }
}
