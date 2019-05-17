package item_organizer_client.controller.summary;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

@FXMLController
@Component
public class SummaryIncomeBetweenController extends SideBarMenuViewController {
    @Autowired
    private TransactionService transactionService;

    public VBox dateFromPane, dateToPane, returnTypePane;
    public Hyperlink dateFromText, dateToText, returnTypeText;
    public DateTimePicker dateFromPicker, dateToPicker;
    public ComboBox<String> returnTypeComboBox;
    public Button submitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showControl(dateFromPane, dateFromText);
        showControl(dateToPane, dateToText);
        showControl(returnTypePane, returnTypeText);

        initFields();
    }

    private void showControl(Pane pane, Control control) {
        pane.getChildren().clear();
        pane.getChildren().add(control);
    }

    public void show(ActionEvent event) {
        TransactionType type = returnTypeComboBox.getSelectionModel().getSelectedIndex() == 0 ?
                TransactionType.SELL : TransactionType.BUY;
        LocalDateTime dateFrom = dateFromPicker.getDateTimeValue();
        LocalDateTime dateTo = dateToPicker.getDateTimeValue();

        BigDecimal income = transactionService.getAllBetweenDates(dateFrom, dateTo).stream().filter(
                transaction -> transaction.getType().equals(type)).map(transaction -> transaction.getTransactionItems().stream().map(
                transactionItem -> transactionItem.getPrice().getValue().multiply(BigDecimal.valueOf(transactionItem.getAmount())))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
        ).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        MyAlerts.showInfo(Utils.getString("summary.alert.incomebetween.title",
                StringUtils.capitalize(returnTypeComboBox.getSelectionModel().getSelectedItem()),
                dateFrom.format(Utils.getDateTimeFormatter()), dateTo.format(Utils.getDateTimeFormatter())),
                Utils.getString("summary.alert.incomebetween", returnTypeComboBox.getSelectionModel().getSelectedItem(),
                        dateFrom.format(Utils.getDateTimeFormatter()), dateTo.format(Utils.getDateTimeFormatter())) +
                        ":\n" + Price.priceFormat(income));
    }

    @Override
    protected void initFields() {
        dateFromText.setOnAction(event -> {
            showControl(dateFromPane, dateFromPicker);
            showControl(dateToPane, dateToPicker);
            showControl(returnTypePane, returnTypeComboBox);
            dateFromPicker.requestFocus();
        });

        LocalDateTime dateFrom = Utils.localDateToLocalDateTime(LocalDate.now(), 0, 0, 0);

        dateFromPicker.setDateTimeValue(dateFrom);
        dateFromText.setText(dateFrom.format(Utils.getDateTimeFormatter()));

        dateToText.setOnAction(event -> {
            showControl(dateFromPane, dateFromPicker);
            showControl(dateToPane, dateToPicker);
            showControl(returnTypePane, returnTypeComboBox);
            dateToPicker.requestFocus();
        });

        LocalDateTime dateTo = Utils.localDateToLocalDateTime(LocalDate.now(), 23, 59, 59);

        dateToPicker.setDateTimeValue(dateTo);
        dateToText.setText(dateTo.format(Utils.getDateTimeFormatter()));

        returnTypeText.setOnAction(event -> {
            showControl(dateFromPane, dateFromPicker);
            showControl(dateToPane, dateToPicker);
            showControl(returnTypePane, returnTypeComboBox);
            returnTypeComboBox.requestFocus();
        });

        ObservableList<String> returnTypeList = FXCollections.observableArrayList("dochód", "koszty");

        returnTypeComboBox.setItems(returnTypeList);
        returnTypeComboBox.getSelectionModel().selectFirst();

        ChangeListener<Boolean> focusLostListener = (observable, oldValue, newValue) -> {
            if (!dateFromPicker.isFocused() && !dateToPicker.isFocused() && !returnTypeComboBox.isFocused()) {
                dateFromText.setText(dateFromPicker.getEditor().getText());
                dateToText.setText(dateToPicker.getEditor().getText());
                returnTypeText.setText(returnTypeComboBox.getSelectionModel().getSelectedItem());
                showControl(dateFromPane, dateFromText);
                showControl(dateToPane, dateToText);
                showControl(returnTypePane, returnTypeText);
            }
        };

        dateFromPicker.focusedProperty().addListener(focusLostListener);
        dateToPicker.focusedProperty().addListener(focusLostListener);
        returnTypeComboBox.focusedProperty().addListener(focusLostListener);

        setDateDatePickerListeners(dateFromPicker, true, 0, 0, 0);
        setDateDatePickerListeners(dateToPicker, true, 23, 59, 59);
    }

    @Override
    protected void clearAlerts() {

    }
}
