package item_organizer_client.listeners;

import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class CustomListener extends ControlListener {
    public static ChangeListener<String> updateBuyPerItemLabelListener(Label infoLabel, TextField price,
                                                                       Spinner<Integer> amount, ComboBox type) {
        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            if (type.getSelectionModel().getSelectedIndex() != 0) {
                if (price.getText().length() > 0) {
                    if (amount.getValue() > 0) {
                        infoLabel.setText(Utils.round(
                                Double.valueOf(price.getText()) / amount.getValue(), 2) + " zł");
                    } else {
                        infoLabel.setText("0.0 zł");
                    }
                }
            }
        };

        price.textProperty().addListener(listener);
        amount.getEditor().textProperty().addListener(listener);

        return listener;
    }

    public static CustomListener onlyReturn() {
        dontAdd();
        return null;
    }
}
