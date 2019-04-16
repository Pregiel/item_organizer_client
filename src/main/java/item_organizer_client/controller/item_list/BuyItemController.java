package item_organizer_client.controller.item_list;

import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.database.service.PriceService;
import item_organizer_client.database.service.TransactionItemService;
import item_organizer_client.database.service.TransactionService;
import item_organizer_client.exception.ItemNotFinishedException;
import item_organizer_client.model.Item;
import item_organizer_client.model.Price;
import item_organizer_client.model.Transaction;
import item_organizer_client.model.TransactionItem;
import item_organizer_client.model.list.ItemList;
import item_organizer_client.model.type.PriceType;
import item_organizer_client.model.type.TransactionType;
import item_organizer_client.utils.MyAlerts;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class BuyItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;
    @Autowired
    private PriceService priceService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionItemService transactionItemService;

    private static final String BUY_ELEMENT_FXML = "/layout/BuyItemElementLayout.fxml";

    public VBox newItemPane;
    public DateTimePicker dateText;
    private List<BuyItemElementController> controllerList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        controllerList = new ArrayList<>();
        addItem(null);

        dateText.setValue(LocalDate.now());
        initFields();
    }

    @Override
    protected void clearAlerts() {

    }

    @Override
    protected void initFields() {
        setDateDatePickerListeners(dateText);
    }

    public void clearAll(ActionEvent event) {
        newItemPane.getChildren().removeAll(newItemPane.getChildren());
        controllerList.clear();
        addItem(null);
        dateText.setValue(LocalDate.now());
    }

    public void submit(ActionEvent event) {
        List<Item> itemList = new ArrayList<>();
        List<Price> priceList = new ArrayList<>();
        List<TransactionItem> transactionItemList = new ArrayList<>();

        try {
            Timestamp date = Timestamp.valueOf(dateText.getDateTimeValue());
            Transaction transaction = new Transaction(date, TransactionType.BUY);

            for (BuyItemElementController controller : controllerList) {
                if (controller.getStep() != 2) {
                    throw new ItemNotFinishedException();
                }
                Item item = controller.getSelectedItem();
                int amount = controller.getSelectedAmount();

                item.setAmount(item.getAmount() + amount);

                Price buyPrice = priceService.getLastedForItem(item, PriceType.BUY);
                if (buyPrice.getValue() != controller.getSelectedBuyPrice()) {
                    priceList.add(new Price(controller.getSelectedBuyPrice(), PriceType.BUY, item, date));
                }

                Price sellPrice = priceService.getLastedForItem(item, PriceType.SELL);
                if (sellPrice.getValue() != controller.getSelectedSellPrice()) {
                    priceList.add(new Price(controller.getSelectedSellPrice(), PriceType.SELL, item, date));
                }

                TransactionItem transactionItem = new TransactionItem(item, transaction, buyPrice, amount);

                itemList.add(item);
                transactionItemList.add(transactionItem);
            }

            transactionService.add(transaction);
            priceService.addAll(priceList);
            itemService.updateAll(itemList);
            transactionItemService.addAll(transactionItemList);

            clearAll(null);
            MyAlerts.showInfo("Sukces", "Operacja zakończona sukcesem.");
            ItemList.getInstance().refresh();
        } catch (ItemNotFinishedException e) {
            MyAlerts.showError("Nie można zakończyć transakcji", "Dokończ dodawanie produktów zanim zakończysz transakcje.");
        } catch (Exception e) {
            MyAlerts.showError("Nie można zakończyć transakcji", "Wystąpił nieznany błąd.");
            e.printStackTrace();
        }
    }

    public void addItem(ActionEvent event) {
        SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource(BUY_ELEMENT_FXML));

        try {
            TitledPane newElement = loader.load();

            BuyItemElementController controller = loader.getController();

            controllerList.add(controller);
            newItemPane.getChildren().add(newElement);

            controller.setElementId(controllerList.size());
            controller.setItemTitle();
            controller.setBuyItemController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<BuyItemElementController> getControllerList() {
        return controllerList;
    }

    public VBox getNewItemPane() {
        return newItemPane;
    }

    public void removeItem(BuyItemElementController controller) {
        newItemPane.getChildren().remove(controller.getItemPane());
        controllerList.remove(controller);

        if (controllerList.size() > 0) {
            for (int i = 0; i < controllerList.size(); i++) {
                controllerList.get(i).setElementId(i + 1);
            }
        } else {
            addItem(null);
        }
    }
}
