package item_organizer_client.utils;

import java.util.ResourceBundle;

public enum IconGraphic {
    ELEMENT_EDIT, ELEMENT_BUY, ELEMENT_SELL, ELEMENT_INFO, ELEMENT_HIDE, ELEMENT_SHOW, RESTORE;

    public static final String DEFAULT_ICON_COLOR = "#000000";

    private static final String ELEMENT_EDIT_COLOR = "";

    public String getSVGPath() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("svgPaths");
        switch (this) {
            case ELEMENT_EDIT:
                return resourceBundle.getString("icon.element.edit");
            case ELEMENT_BUY:
                return resourceBundle.getString("icon.element.buy");
            case ELEMENT_SELL:
                return resourceBundle.getString("icon.element.sell");
            case ELEMENT_INFO:
                return resourceBundle.getString("icon.element.info");
            case ELEMENT_HIDE:
                return resourceBundle.getString("icon.element.hide");
            case ELEMENT_SHOW:
                return resourceBundle.getString("icon.element.show");
            case RESTORE:
                return resourceBundle.getString("icon.restore");
        }
        return null;
    }

    public String getColor() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("colors");
        switch (this) {
            case ELEMENT_EDIT:
                return resourceBundle.getString("icon.element.edit");
            case ELEMENT_BUY:
                return resourceBundle.getString("icon.element.buy");
            case ELEMENT_SELL:
                return resourceBundle.getString("icon.element.sell");
            case ELEMENT_INFO:
                return resourceBundle.getString("icon.element.info");
            case ELEMENT_HIDE:
            case ELEMENT_SHOW:
                return resourceBundle.getString("icon.element.hide");
        }
        return DEFAULT_ICON_COLOR;
    }

    public String getHoverColor() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("colors");
        switch (this) {
            case ELEMENT_EDIT:
                return resourceBundle.getString("icon.element.edit.hover");
            case ELEMENT_BUY:
                return resourceBundle.getString("icon.element.buy.hover");
            case ELEMENT_SELL:
                return resourceBundle.getString("icon.element.sell.hover");
            case ELEMENT_INFO:
                return resourceBundle.getString("icon.element.info.hover");
            case ELEMENT_HIDE:
            case ELEMENT_SHOW:
                return resourceBundle.getString("icon.element.hide.hover");
        }
        return DEFAULT_ICON_COLOR;
    }
}
