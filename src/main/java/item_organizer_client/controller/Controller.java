package item_organizer_client.controller;

import java.util.prefs.Preferences;

public class Controller {
    private Preferences preferences;

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }
}
