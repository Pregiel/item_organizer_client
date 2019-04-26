package item_organizer_client.controller;

import item_organizer_client.ItemOrganizer;
import item_organizer_client.Main;
import item_organizer_client.category.DatabaseTestCategory;
import item_organizer_client.category.ValidationTestCategory;
import item_organizer_client.database.ItemOrganizerDatabase;
import item_organizer_client.database.service.ItemService;
import item_organizer_client.model.element.ItemTableElement;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.cfg.Configuration;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import tornadofx.control.DateTimePicker;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

@Component
public class AddItemControllerTest extends ApplicationTest {
    private ItemOrganizer itemOrganizer;

    @Autowired
    private ItemService itemService;

    @Override
    public void init() throws Exception {
        SpringFXMLLoader.setApplicationContext(SpringApplication.run(Main.class), this);
    }

    @Override
    public void start(Stage stage) throws Exception {
        itemOrganizer = new ItemOrganizer(stage);
    }

    @BeforeClass
    public static void setUpClass() {
        ItemOrganizerDatabase.setSessionFactory(new Configuration().configure("hibernate_test.cfg.xml").buildSessionFactory());
    }

    @After
    public void tearDown() throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    private void writeItem(String id, String name, String category, String amount, String date, String buyPrice, String sellPrice) {
        clickOn("#numberText").write(id);
        clickOn("#nameText").write(name);
        clickOn("#categoryText").write(category);
        clickOn("#amountText").write(amount);
        clickOn("#dateText").write(date);
        clickOn("#buyPriceText").write(buyPrice);
        clickOn("#sellPriceText").write(sellPrice);
    }

    private void addItem(String id, String name, String category, String amount, String date, String buyPrice, String sellPrice) {
        clickOn("#addButton");
        writeItem(id, name, category, amount, date, buyPrice, sellPrice);
        clickOn("#submit").clickOn("OK");
    }

    private static final String defaultId = "1234", defaultName = "Kubek 330ml czarny", defaultCategory = "kubek",
            defaultAmount = "5", defaultDate = "01.01.2019 02:56", defaultBuyPrice = "9.99", defaultSellPrice = "18.99";

    private void addItem() {
        addItem(defaultId, defaultName, defaultCategory, defaultAmount, defaultDate, defaultBuyPrice, defaultSellPrice);
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void idText_ValidId() {
        clickOn("#addButton").clickOn("#numberText");
        write("1234");

        clickOn("#submit");
        assertEquals("1234", ((TextField) lookup("#numberText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void idText_Null() {
        clickOn("#addButton").clickOn("#numberText");
        press(KeyCode.BACK_SPACE);


        clickOn("#submit");
        assertTrue(lookup("#numberNullAlert").query().isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void idText_InvalidChars() {
        clickOn("#addButton").clickOn("#numberText");
        write("abcABC!@#$%^&*()-_=+[{]};:'\",<.>/?\\|`~ ", 2);

        clickOn("#submit");
        assertEquals("", ((TextField) lookup("#numberText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void idText_TooMuchValidChars() {
        clickOn("#addButton").clickOn("#numberText");
        write("1234567890");

        clickOn("#submit");
        assertEquals("1234", ((TextField) lookup("#numberText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void idText_MixedChars() {
        clickOn("#addButton").clickOn("#numberText");
        write("sf2cvbdf6dfgdf7cvbcbc9cvbd");

        clickOn("#submit");
        assertEquals("2679", ((TextField) lookup("#numberText").query()).getText());
    }

    @Category({ValidationTestCategory.class, DatabaseTestCategory.class})
    @Test
    public void idText_DuplicatedId() {
        addItem();

        clickOn("#numberText");
        write(defaultId);

        clickOn("#submit");
        assertTrue((lookup("#numberDuplicateAlert").query()).isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void nameText_ValidName() {
        clickOn("#addButton").clickOn("#nameText");
        write("Kubek 330ml");

        clickOn("#submit");
        assertEquals("Kubek 330ml", ((TextField) lookup("#nameText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void nameText_Null() {
        clickOn("#addButton").clickOn("#nameText");
        press(KeyCode.BACK_SPACE);


        clickOn("#submit");
        assertTrue(lookup("#nameNullAlert").query().isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void nameText_NotEnoughChars() {
        clickOn("#addButton").clickOn("#nameText");
        write(RandomStringUtils.randomAlphanumeric(2));


        clickOn("#submit");
        assertTrue(lookup("#nameMinAlert").query().isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void nameText_TooMuchChars() {
        clickOn("#addButton").clickOn("#nameText");
        write(RandomStringUtils.randomAlphanumeric(254), 2);

        clickOn("#submit");
        assertEquals(250, ((TextField) lookup("#nameText").query()).getText().length());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void nameText_SpacesBeforeAndAfterValidText() {
        clickOn("#addButton").clickOn("#nameText");
        write("    Kubek 330ml    ");

        clickOn("#submit");
        assertEquals("Kubek 330ml", ((TextField) lookup("#nameText").query()).getText());
    }

    @Category({ValidationTestCategory.class, DatabaseTestCategory.class})
    @Test
    public void nameText_DuplicatedName() {
        addItem();

        clickOn("#nameText");
        write(defaultName);

        clickOn("#submit");
        assertTrue((lookup("#nameDuplicateAlert").query()).isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void categoryText_ValidCategory() {
        clickOn("#addButton").clickOn("#categoryText");
        write("kubek");

        clickOn("#submit");
        assertEquals("kubek", ((ComboBox) lookup("#categoryText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void categoryText_Null() {
        clickOn("#addButton").clickOn("#categoryText");
        press(KeyCode.BACK_SPACE);


        clickOn("#submit");
        assertTrue(lookup("#categoryNullAlert").query().isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void categoryText_NotEnoughChars() {
        clickOn("#addButton").clickOn("#categoryText");
        write(RandomStringUtils.randomAlphanumeric(2));


        clickOn("#submit");
        assertTrue(lookup("#categoryMinAlert").query().isVisible());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void categoryText_SpacesBeforeAndAfterValidText() {
        clickOn("#addButton").clickOn("#categoryText");
        write("    kubek    ");

        clickOn("#submit");
        assertEquals("kubek", ((ComboBox) lookup("#categoryText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void amountText_ValidAmount() {
        clickOn("#addButton").clickOn("#amountText");
        write("12");

        clickOn("#submit");
        assertEquals("12", ((Spinner) lookup("#amountText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void amountText_Null() {
        clickOn("#addButton").clickOn("#amountText");
        press(KeyCode.BACK_SPACE);

        clickOn("#submit");
        assertEquals("1", ((Spinner) lookup("#amountText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void amountText_InvalidChars() {
        clickOn("#addButton").clickOn("#amountText");
        write("abcABC!@#$%^&*()-_=+[{]};:'\",<.>/?\\|`~ ", 2);

        clickOn("#submit");
        assertEquals("1", ((Spinner) lookup("#amountText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void amountText_MixedChars() {
        clickOn("#addButton").clickOn("#amountText");
        write("sdfsv2;'.,3ghjg");

        clickOn("#submit");
        assertEquals("23", ((Spinner) lookup("#amountText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void dateText_ValidDate() {
        clickOn("#addButton").clickOn("#dateText");
        write("01.01.2019 02:56");

        clickOn("#submit");
        assertEquals("01.01.2019 02:56", ((DateTimePicker) lookup("#dateText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void dateText_AdditionalChars() {
        clickOn("#addButton").clickOn("#dateText");
        String currentText = ((DateTimePicker) lookup("#dateText").query()).getEditor().getText();
        write("xcv01.01.xcv2019 02:56vcxv");

        clickOn("#submit");
        assertEquals(currentText, ((DateTimePicker) lookup("#dateText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void dateText_InvalidDate() {
        clickOn("#addButton").clickOn("#dateText");
        String currentText = ((DateTimePicker) lookup("#dateText").query()).getEditor().getText();
        write("34.01.2019 02:56");

        clickOn("#submit");
        assertEquals(currentText, ((DateTimePicker) lookup("#dateText").query()).getEditor().getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_ValidPrice() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("26.15");

        clickOn("#submit");
        assertEquals("26.15", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_Null() {
        clickOn("#addButton").clickOn("#buyPriceText");
        press(KeyCode.BACK_SPACE);

        clickOn("#submit");
        assertEquals("0.00", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_InvalidChars() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("2cvb6.1xc5dsa");

        clickOn("#submit");
        assertEquals("26.15", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_NoDot() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("253");

        clickOn("#submit");
        assertEquals("253.00", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_DotAtEnd() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("253.");

        clickOn("#submit");
        assertEquals("253.00", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_CommaInsteadDot() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("25,31");

        clickOn("#submit");
        assertEquals("25.31", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void buyPriceText_TooMuchAfterDot() {
        clickOn("#addButton").clickOn("#buyPriceText");
        write("25.31213453");

        clickOn("#submit");
        assertEquals("25.31", ((TextField) lookup("#buyPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_ValidPrice() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("26.15");

        clickOn("#submit");
        assertEquals("26.15", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_Null() {
        clickOn("#addButton").clickOn("#sellPriceText");
        press(KeyCode.BACK_SPACE);

        clickOn("#submit");
        assertEquals("0.00", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_InvalidChars() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("2cvb6.1xc5dsa");

        clickOn("#submit");
        assertEquals("26.15", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_NoDot() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("253");

        clickOn("#submit");
        assertEquals("253.00", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_DotAtEnd() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("253.");

        clickOn("#submit");
        assertEquals("253.00", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_CommaInsteadDot() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("25,31");

        clickOn("#submit");
        assertEquals("25.31", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(ValidationTestCategory.class)
    @Test
    public void sellPriceText_TooMuchAfterDot() {
        clickOn("#addButton").clickOn("#sellPriceText");
        write("25.31213453");

        clickOn("#submit");
        assertEquals("25.31", ((TextField) lookup("#sellPriceText").query()).getText());
    }

    @Category(DatabaseTestCategory.class)
    @Test
    public void addItem_ValidItem() {
        String id = "1234", name = "Kubek 330ml czarny", category = "kubek", amount = "5", date = "01.01.2019 02:56",
                buyPrice = "9.99", sellPrice = "18.99";

        addItem(id, name, category, amount, date, buyPrice, sellPrice);

        ObservableList<ItemTableElement> itemList = ((TableView<ItemTableElement>) lookup("#itemTableView").query()).getItems();

        for (ItemTableElement item : itemList) {
            assertEquals(id, item.getNumber().toString());
            assertEquals(name, item.getName());
            assertEquals(category, item.getCategory().getName());
            assertEquals(amount, item.getAmount().toString());
        }
    }
}