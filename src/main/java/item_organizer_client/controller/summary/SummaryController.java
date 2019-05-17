package item_organizer_client.controller.summary;

import de.felixroske.jfxsupport.FXMLController;
import item_organizer_client.controller.Controller;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
public class SummaryController extends Controller {
    private static final String[] SUMMARY_LIST
            = {"/layout/SummaryIncomeLastLayout.fxml", "/layout/SummaryIncomeBetweenLayout.fxml"};
    public VBox elementsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (String layout : SUMMARY_LIST) {
            try {
                FXMLLoader loader = new SpringFXMLLoader(getClass().getResource(layout));

                ResourceBundle resourceBundle = ResourceBundle.getBundle("colors");

                Node node = loader.load();
                node.setStyle("-fx-background-color: " + (elementsPane.getChildren().size() % 2 == 0 ?
                        resourceBundle.getString("element.background") :
                        resourceBundle.getString("element.background.secondary")));

                elementsPane.getChildren().add(node);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
