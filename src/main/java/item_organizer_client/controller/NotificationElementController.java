package item_organizer_client.controller;

import de.felixroske.jfxsupport.FXMLController;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@FXMLController
@Component
public class NotificationElementController implements Initializable {
    public Label tagText;
    public Button closeButton;
    public Label messageText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        closeButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.CLOSE));
        closeButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }

    public Label getTagText() {
        return tagText;
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Label getMessageText() {
        return messageText;
    }

}
