package item_organizer_client.listeners;

import item_organizer_client.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import tornadofx.control.DateTimePicker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;

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

    public static ChangeListener<LocalDateTime> fillTimeOnActionListener(DateTimePicker dateTimePicker, int hour, int minute, int second) {
        dateTimePicker.setOnAction(event -> {
            if (!dateTimePicker.getEditor().getText().matches(
                    ".*" + Utils.dateRegex + " " + Utils.fillWithZeros(hour, 2) + ":" + Utils.fillWithZeros(minute, 2))) {
                LocalDateTime time = dateTimePicker.getDateTimeValue();
                if (time != null) {
                    dateTimePicker.setDateTimeValue(Utils.localDateToLocalDateTime(time.toLocalDate(), hour, minute, second));
                }
            }
        });
        return null;
    }

    public static DatePickerListener onlyReturn() {
        dontAdd();
        return null;
    }
}
