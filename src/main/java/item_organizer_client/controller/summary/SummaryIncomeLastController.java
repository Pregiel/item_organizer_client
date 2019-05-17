package item_organizer_client.controller.summary;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.database.service.TransactionItemService;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.model.Price;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@FXMLController
@Component
public class SummaryIncomeLastController extends SideBarMenuViewController {
    @Autowired
    private TransactionService transactionService;

    public VBox valuePane, typePane, returnTypePane;
    public Hyperlink valueText, typeText, returnTypeText;
    public Spinner<Integer> valueSpinner;
    public ComboBox<String> typeComboBox, returnTypeComboBox;
    public Button submitButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showControl(valuePane, valueText);
        showControl(typePane, typeText);
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
        LocalDateTime startDate = Utils.localDateToLocalDateTime(LocalDate.now(), 0, 0, 0);
        Integer value = Integer.parseInt(valueSpinner.getEditor().getText());
        switch (typeComboBox.getSelectionModel().getSelectedIndex()) {
            case 1:
                startDate = startDate.minus(Period.ofWeeks(value));
                break;
            case 2:
                startDate = startDate.minus(Period.ofMonths(value));
                break;
            case 3:
                startDate = startDate.minus(Period.ofMonths(value));
                break;
            default:
                startDate = startDate.minus(Period.ofDays(value));
                break;
        }

        BigDecimal income = transactionService.getAllAfterDate(startDate).stream().filter(
                transaction -> transaction.getType().equals(type)).map(transaction -> transaction.getTransactionItems().stream().map(
                transactionItem -> transactionItem.getPrice().getValue().multiply(BigDecimal.valueOf(transactionItem.getAmount())))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)
        ).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

        MyAlerts.showInfo(Utils.getString("summary.alert.incomelast.title",
                StringUtils.capitalize(returnTypeComboBox.getSelectionModel().getSelectedItem()),
                valueText.getText(), typeText.getText()),
                Utils.getString("summary.alert.incomelast", returnTypeComboBox.getSelectionModel().getSelectedItem(),
                        valueText.getText(), typeText.getText()) +
                        " (od " + startDate.format(Utils.getDateTimeFormatter()) + "):\n" + Price.priceFormat(income));
    }

    @Override
    protected void initFields() {
        valueText.setOnAction(event -> {
            showControl(valuePane, valueSpinner);
            showControl(typePane, typeComboBox);
            showControl(returnTypePane, returnTypeComboBox);
            valueSpinner.requestFocus();
        });

        setAmountSpinnerListeners(valueSpinner, true, 5);

        typeText.setOnAction(event -> {
            showControl(valuePane, valueSpinner);
            showControl(typePane, typeComboBox);
            showControl(returnTypePane, returnTypeComboBox);
            typeComboBox.requestFocus();
        });

        ObservableList<String> typeList = FXCollections.observableArrayList("dni", "tygodni", "miesięcy", "lat");

        typeComboBox.setItems(typeList);
        typeComboBox.getSelectionModel().selectFirst();

        returnTypeText.setOnAction(event -> {
            showControl(valuePane, valueSpinner);
            showControl(typePane, typeComboBox);
            showControl(returnTypePane, returnTypeComboBox);
            returnTypeComboBox.requestFocus();
        });

        ObservableList<String> returnTypeList = FXCollections.observableArrayList("dochód", "koszty");

        returnTypeComboBox.setItems(returnTypeList);
        returnTypeComboBox.getSelectionModel().selectFirst();

        ChangeListener<Boolean> focusLostListener = (observable, oldValue, newValue) -> {
            if (!valueSpinner.isFocused() && !typeComboBox.isFocused() && !returnTypeComboBox.isFocused()) {
                valueText.setText(valueSpinner.getEditor().getText());
                typeText.setText(typeComboBox.getSelectionModel().getSelectedItem());
                returnTypeText.setText(returnTypeComboBox.getSelectionModel().getSelectedItem());
                showControl(valuePane, valueText);
                showControl(typePane, typeText);
                showControl(returnTypePane, returnTypeText);
            }
        };

        valueSpinner.focusedProperty().addListener(focusLostListener);
        typeComboBox.focusedProperty().addListener(focusLostListener);
        returnTypeComboBox.focusedProperty().addListener(focusLostListener);
    }

    @Override
    protected void clearAlerts() {

    }
}
