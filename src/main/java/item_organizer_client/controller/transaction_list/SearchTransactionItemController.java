package item_organizer_client.controller.transaction_list;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import item_organizer_client.controller.SideBarMenuViewController;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.Item;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SearchTransactionItemController extends SideBarMenuViewController implements Initializable {
    @Autowired
    private ItemService itemService;

    public VBox elementPane, searchPane;
    public HBox infoPane;
    public ComboBox<String> searchText;
    public Label itemTitle, itemNotExistAlert;
    public Button deleteButton;

    private int step;
    private Item selectedItem;

    private Runnable addItemToListRunnable, removeItemFromListRunnable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        deleteButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        deleteButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
        deleteButton.setCursor(Cursor.HAND);

        initFields();
        goToStep(0);
        clearAlerts();
    }

    @Override
    protected void initFields() {
        setItemSearchComboBox(searchText, itemService.getAllTitles(), itemNotExistAlert);
    }

    @Override
    protected void clearAlerts() {
        ((Pane)searchText.getParent().getParent()).getChildren().removeAll(itemNotExistAlert);
    }

    public void submit(ActionEvent event) {
        try {
            String text = searchText.getEditor().getText();
            if (text.substring(0, 4).matches("\\d{4}")) {
                selectedItem = itemService.findByNumber(Integer.parseInt(text.substring(0, 4)));
            } else {
                selectedItem = itemService.findByName(text);
            }

            if (selectedItem == null)
                throw new NullPointerException();

            addItemToListRunnable.run();

            goToStep(1);
        } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException ex) {
            ex.printStackTrace();
            ((Pane) searchText.getParent()).getChildren().add(itemNotExistAlert);
        }
    }

    public void changeItem(ActionEvent event) {
        removeItemFromListRunnable.run();
        goToStep(0);
    }

    /**
     * @param step 0 - search item, 1 - item title
     */
    public void goToStep(int step) {
        this.step = step;
        clearAlerts();
        elementPane.getChildren().removeAll(searchPane, infoPane);
        switch (step) {
            case 0:
                elementPane.getChildren().add(searchPane);
                itemTitle.setText("");
                Platform.runLater(() -> {
                    searchText.requestFocus();
                    searchText.getEditor().selectAll();
                });
                selectedItem = null;
                break;
            case 1:
                elementPane.getChildren().add(infoPane);

                itemTitle.setText(selectedItem.toTitle());
                break;
        }
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Runnable getAddItemToListRunnable() {
        return addItemToListRunnable;
    }

    public void setAddItemToListRunnable(Runnable addItemToListRunnable) {
        this.addItemToListRunnable = addItemToListRunnable;
    }

    public Runnable getRemoveItemFromListRunnable() {
        return removeItemFromListRunnable;
    }

    public void setRemoveItemFromListRunnable(Runnable removeItemFromListRunnable) {
        this.removeItemFromListRunnable = removeItemFromListRunnable;
    }

    public Item getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(Item selectedItem) {
        this.selectedItem = selectedItem;
        goToStep(1);
    }
}
