package item_organizer_client.controller;

import item_organizer_client.MenuView;
import item_organizer_client.utils.SpringFXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

abstract class SideBarController implements Initializable {
    private MenuView currentView;
    private Node currentNode;
    private SplitPane splitPane;
    private Preferences preferences;
    private Map<MenuView, Button> buttonMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentView = MenuView.NONE;
    }

    Map<MenuView, Button> getButtonMap() {
        return buttonMap;
    }

    void toggleView(MenuView menuView) {
        if (currentView == menuView) {
            hideView();
        } else {
            showView(menuView);
        }
    }

    void showView(MenuView menuView) {
        SpringFXMLLoader loader = null;
        hideView();

        currentView = menuView;

        if (currentView != MenuView.NONE) {
            loader = new SpringFXMLLoader(getClass().getResource(currentView.getView()));
            toggleButton(buttonMap.get(currentView));
        }

        if (loader != null) {
            try {
                currentNode = loader.load();
                splitPane.getItems().add(0, currentNode);

                double position = preferences.getDouble(currentView.toString().toLowerCase() + "_divider", 0);
                if (position <= 0) {
                    position = ((Pane) currentNode).getPrefWidth() / splitPane.getWidth();
                }

                splitPane.getDividers().get(0).setPosition(position);

                splitPane.getDividers().get(0).positionProperty().addListener((observable, oldValue, newValue) -> {
                    preferences.putDouble(currentView.toString().toLowerCase() + "_divider", (Double) newValue);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void hideView() {
        if (currentNode != null) {
            splitPane.getItems().remove(currentNode);
        }
        currentView = MenuView.NONE;
        untoggleAllButtons();
    }

    private static final String TOGGLED_SIDE_ICON_BUTTON_STYLECLASS = "toggled-side-icon-button";

    private void toggleButton(Button button) {
        button.getStyleClass().add(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS);
    }

    private void untoggleAllButtons() {
        buttonMap.forEach((menuView, button) -> button.getStyleClass().remove(TOGGLED_SIDE_ICON_BUTTON_STYLECLASS));
    }

    void setSplitPane(SplitPane splitPane) {
        this.splitPane = splitPane;
    }

    void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
