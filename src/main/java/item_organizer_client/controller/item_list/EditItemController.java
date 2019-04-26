package item_organizer_client.controller.item_list;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.CategoryService;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.listeners.TextFieldListener;
import item_organizer_client.model.Category;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.utils.Icon;
import item_organizer_client.utils.IconGraphic;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.Utils;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

@FXMLController
@Component
public class EditItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PriceService priceService;

    public HBox footerPane, selectedItemPane;
    public VBox editItemPane, headerPane;
    public Button sellPriceReset, buyPriceReset, safeAmountReset, amountReset, categoryReset, nameReset, idReset;
    public GridPane searchInputPane, editValuePane;
    public BorderPane editPane;
    public TextField idText, nameText, buyPriceText, sellPriceText;
    public Spinner<Integer> amountText, safeAmountText;
    public ComboBox<String> searchText, categoryText;
    public Label nameMinAlert, categoryMinAlert, idNullAlert, idDuplicateAlert, nameNullAlert, nameDuplicateAlert,
            amountNullAlert, categoryMaxAlert, nameMaxAlert, buyNullAlert, sellNullAlert, categoryNullAlert, idMaxAlert,
            safeAmountNullAlert, selectedItemTitle, itemNotExistAlert;

    private Item selectedItem;
    private ChangeListener<Boolean> checkDuplicatedIdListener, checkDuplicatedNameListener;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setPreferences(Preferences.userRoot().node(this.getClass().getName()));

        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setItemSearchComboBox(searchText, itemService.getAllTitles(), itemNotExistAlert);

        setIdTextFieldListeners(idText, Item.ID_DIGITS, idText.getParent(), idNullAlert, idDuplicateAlert, idMaxAlert);
        setNameTextFieldListeners(nameText, nameText.getParent(), nameNullAlert, nameMinAlert, nameMaxAlert,
                nameDuplicateAlert);
        setCategoryComboBoxListeners(categoryText, categoryService, categoryText.getParent(),
                categoryNullAlert, categoryMinAlert, categoryMaxAlert);
        setAmountSpinnerListeners(amountText, Item.INITIAL_AMOUNT_VALUE, amountText.getParent(), amountNullAlert);
        setAmountSpinnerListeners(safeAmountText, Item.INITIAL_SAFE_AMOUNT_VALUE, safeAmountText.getParent(),
                safeAmountNullAlert);
        setPriceTextFieldListeners(buyPriceText, buyPriceText.getParent(), buyNullAlert);
        setPriceTextFieldListeners(sellPriceText, sellPriceText.getParent(), sellNullAlert);

        for (Button button : new Button[]{idReset, nameReset, categoryReset, amountReset, safeAmountReset,
                buyPriceReset, sellPriceReset}) {
            Icon.setIconButton(button, Icon.createSVGIcon(IconGraphic.RESTORE,
                    "-icon-color",
                    "-icon-hover-color",
                    12));
        }
    }

    @Override
    protected void clearAlerts() {
        ((Pane) searchText.getParent()).getChildren().removeAll(itemNotExistAlert);

        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
        ((Pane) safeAmountText.getParent()).getChildren().removeAll(safeAmountNullAlert);
        ((Pane) buyPriceText.getParent()).getChildren().removeAll(buyNullAlert);
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellNullAlert);
    }

    public void reset(ActionEvent event) {
        clearAlerts();

        selectedItemTitle.setText(selectedItem.toTitle());

        resetId(null);
        resetName(null);
        resetCategory(null);
        resetAmount(null);
        resetSafeAmount(null);
        resetBuyPrice(null);
        resetSellPrice(null);
    }

    public void resetId(ActionEvent event) {
        idText.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
        ((Pane) idText.getParent()).getChildren().removeAll(idNullAlert, idMaxAlert, idDuplicateAlert);
    }

    public void resetName(ActionEvent event) {
        nameText.setText(String.valueOf(selectedItem.getName()));
        ((Pane) nameText.getParent()).getChildren().removeAll(nameNullAlert, nameMinAlert,
                nameMaxAlert, nameDuplicateAlert);
    }

    public void resetCategory(ActionEvent event) {
        categoryText.getEditor().setText(selectedItem.getCategory().getName());
        ((Pane) categoryText.getParent()).getChildren().removeAll(categoryNullAlert, categoryMinAlert,
                categoryMaxAlert);
    }

    public void resetAmount(ActionEvent event) {
        amountText.getEditor().setText(String.valueOf(selectedItem.getAmount()));
        ((Pane) amountText.getParent()).getChildren().removeAll(amountNullAlert);
    }

    public void resetSafeAmount(ActionEvent event) {
        safeAmountText.getEditor().setText(String.valueOf(selectedItem.getSafeAmount()));
        ((Pane) safeAmountText.getParent()).getChildren().removeAll(safeAmountNullAlert);
    }

    public void resetBuyPrice(ActionEvent event) {
        Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
        if (lastedBuyPrice != null) {
            buyPriceText.setText(lastedBuyPrice.priceFormat());
        }
        ((Pane) buyPriceText.getParent()).getChildren().removeAll(buyNullAlert);
    }

    public void resetSellPrice(ActionEvent event) {
        Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
        if (lastedSellPrice != null) {
            sellPriceText.setText(lastedSellPrice.priceFormat());
        }
        ((Pane) sellPriceText.getParent()).getChildren().removeAll(sellNullAlert);
    }

    public void submit(ActionEvent event) {
        idText.setText(idText.getText().trim());
        nameText.setText(nameText.getText().trim());
        categoryText.getEditor().setText(categoryText.getEditor().getText().trim());
        buyPriceText.setText(buyPriceText.getText().trim());
        sellPriceText.setText(sellPriceText.getText().trim());

        if (validate()) {
            int amountValue = Integer.parseInt(amountText.getEditor().getText());
            int safeAmountValue = Integer.parseInt(safeAmountText.getEditor().getText());
            BigDecimal sellPriceValue = new BigDecimal(sellPriceText.getText());
            BigDecimal buyPriceValue = new BigDecimal(buyPriceText.getText());
            Timestamp date = Timestamp.valueOf(LocalDateTime.now());


            Item item = new Item(selectedItem.getId(), nameText.getText(),
                    categoryService.findOrAdd(new Category(categoryText.getEditor().getText())), amountValue, safeAmountValue);

            Price buyPrice = priceService.getLastedForItem(item, PriceType.BUY);
            if (buyPrice.getValue().compareTo(buyPriceValue) != 0) {
                buyPrice = new Price(buyPriceValue, PriceType.BUY, item, date);
                priceService.add(buyPrice);
            }

            Price sellPrice = priceService.getLastedForItem(item, PriceType.SELL);
            if (sellPrice.getValue().compareTo(sellPriceValue) != 0) {
                sellPrice = new Price(sellPriceValue, PriceType.SELL, item, date);
                priceService.add(sellPrice);
            }

            itemService.update(item);

//            if (!item.getId().equals(Integer.parseInt(idText.getText()))) {
//                selectedItem = itemService.updateId(item, Integer.parseInt(idText.getText()));
//            } else {
                selectedItem = item;
//            }

            reset(null);
            MyAlerts.showInfo("Sukces", "Operacja zakończona sukcesem.");
            refresh();
        }
    }


    private void refresh() {
        ItemList.getInstance().refresh();

        categoryText.getItems().clear();
        categoryText.getItems().addAll(categoryService.getAll()
                .stream().map(Category::getName).collect(Collectors.toList()));
        TextFields.bindAutoCompletion(categoryText.getEditor(), categoryText.getItems());
    }

    private boolean validate() {
        boolean success = true;
        if (idText.getText().length() == 0) {
            showAlert(idText, idNullAlert);
            success = false;
        } else if (idText.getText().length() > 4) {
            showAlert(idText, idMaxAlert);
            success = false;
        }

        if (nameText.getText().length() == 0) {
            showAlert(nameText, nameNullAlert);
            success = false;
        } else if (nameText.getText().length() < 3) {
            showAlert(nameText, nameMinAlert);
            success = false;
        } else if (nameText.getText().length() > 250) {
            showAlert(nameText, nameMaxAlert);
            success = false;
        }

        if (categoryText.getEditor().getText() == null) {
            categoryText.getEditor().setText("");
            showAlert(categoryText, categoryNullAlert);
        } else if (categoryText.getEditor().getText().length() == 0) {
            showAlert(categoryText, categoryNullAlert);
            success = false;
        } else if (categoryText.getEditor().getText().length() < 3) {
            showAlert(categoryText, categoryMinAlert);
            success = false;
        } else if (categoryText.getEditor().getText().length() > 250) {
            showAlert(categoryText, categoryMaxAlert);
            success = false;
        }

        if (amountText.getEditor().getText().length() == 0) {
            showAlert(amountText, amountNullAlert);
            success = false;
        }

        if (safeAmountText.getEditor().getText().length() == 0) {
            showAlert(safeAmountText, safeAmountNullAlert);
            success = false;
        }

        if (buyPriceText.getText().length() == 0) {
            showAlert(buyPriceText, buyNullAlert);
            success = false;
        }

        if (sellPriceText.getText().length() == 0) {
            showAlert(sellPriceText, sellNullAlert);
            success = false;
        }

        return success;
    }


    private void showAlert(Control control, Label label) {
        if (!((Pane) control.getParent()).getChildren().contains(label)) {
            ((Pane) control.getParent()).getChildren().add(label);
        }
    }

    /**
     * @param step 0 - search item, 1 - details
     */
    @SuppressWarnings({"ConstantConditions", "AccessStaticViaInstance"})
    private void goToStep(int step) {
        clearAlerts();
        editItemPane.getChildren().removeAll(searchInputPane, editValuePane);
        editPane.setBottom(null);
        headerPane.getChildren().remove(selectedItemPane);
        switch (step) {
            case 0:
                editItemPane.getChildren().addAll(searchInputPane);
                break;
            case 1:
                editItemPane.getChildren().addAll(editValuePane);
                headerPane.getChildren().add(selectedItemPane);
                editPane.setBottom(footerPane);

                if (checkDuplicatedIdListener != null) {
                    idText.focusedProperty().removeListener(checkDuplicatedIdListener);
                }
                checkDuplicatedIdListener = TextFieldListener.onlyReturn().checkItemIdIfExistListener(idText,
                        itemService, selectedItem.getId(), idText.getParent(), idDuplicateAlert);
                idText.focusedProperty().addListener(checkDuplicatedIdListener);

                if (checkDuplicatedNameListener != null) {
                    nameText.focusedProperty().removeListener(checkDuplicatedNameListener);
                }
                checkDuplicatedNameListener = TextFieldListener.onlyReturn().checkItemNameIfExistListener(nameText,
                        itemService, selectedItem.getName(), nameText.getParent(), nameDuplicateAlert);
                nameText.focusedProperty().addListener(checkDuplicatedNameListener);

                selectedItemTitle.setText(selectedItem.toTitle());
                idText.setText(Utils.fillWithZeros(selectedItem.getId(), 4));
                nameText.setText(String.valueOf(selectedItem.getName()));
                categoryText.getEditor().setText(selectedItem.getCategory().getName());
                amountText.getEditor().setText(String.valueOf(selectedItem.getAmount()));
                safeAmountText.getEditor().setText(String.valueOf(selectedItem.getSafeAmount()));

                Price lastedBuyPrice = priceService.getLastedForItem(selectedItem, PriceType.BUY);
                if (lastedBuyPrice != null) {
                    buyPriceText.setText(lastedBuyPrice.priceFormat());
                }

                Price lastedSellPrice = priceService.getLastedForItem(selectedItem, PriceType.SELL);
                if (lastedSellPrice != null) {
                    sellPriceText.setText(lastedSellPrice.priceFormat());
                }
                break;
        }
    }

    public void nextStepSearch(ActionEvent event) {
        try {
            String text = searchText.getEditor().getText();
            if (text.substring(0, 4).matches("\\d{4}")) {
                selectedItem = itemService.findById(Integer.parseInt(text.substring(0, 4)));
            } else {
                selectedItem = itemService.findByName(text);
            }

            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }

    public void changeSelectedItem(ActionEvent event) {
        goToStep(0);
    }

    public void setEditItem(Item item) {
        try {
            selectedItem = item;

            if (selectedItem == null)
                throw new NullPointerException();

            goToStep(1);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            searchText.getEditor().setText(Utils.fillWithZeros(item.getId(), 4));
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }
}
