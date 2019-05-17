package item_organizer_client.controller;

import javafx.fxml.Initializable;

import java.util.prefs.Preferences;

public abstract class Controller implements Initializable {
    private Preferences preferences;

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
