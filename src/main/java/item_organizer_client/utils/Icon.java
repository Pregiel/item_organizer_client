package item_organizer_client.utils;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.shape.SVGPath;

import java.util.ResourceBundle;

public class Icon {
    private static final int DEFAULT_ICON_SIZE = 16;
    private static final String DEFAULT_ICON_COLOR = "#000000";

    public enum IconPath {
        ELEMENT_EDIT, ELEMENT_BUY, ELEMENT_SELL, ELEMENT_INFO, ELEMENT_HIDE, ELEMENT_SHOW;

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
            }
            return null;
        }
    }

    public static Button createIconButton(Node graphic) {
        Button button = new Button("", graphic);
        button.getStyleClass().add("no-background-button");
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        return button;
    }

    public static Group createSVGIcon(String path, String fill, String hoverFill, int size) {
        SVGPath svgPath = new SVGPath();
        svgPath.setContent(path);
        svgPath.getStyleClass().add("svg-icon");
        svgPath.setStyle("-fill:" + fill + ";-hover-fill:" + hoverFill + ';');

        Group group = new Group(svgPath);

        Bounds bounds = svgPath.getBoundsInParent();
        double scale = Math.min(size / bounds.getWidth(), size / bounds.getHeight());
        svgPath.setScaleX(scale);
        svgPath.setScaleY(scale);

        return group;
    }

    public static Group createSVGIcon(String path, String fill, String hoverFill) {
        return createSVGIcon(path, fill, hoverFill, DEFAULT_ICON_SIZE);
    }

    public static Group createSVGIcon(String path, String fill) {
        return createSVGIcon(path, fill, fill);
    }

    public static Group createSVGIcon(String path) {
        return createSVGIcon(path, DEFAULT_ICON_COLOR);
    }

    public static Group createSVGIcon(IconPath path, String fill, String hoverFill, int size) {
        return createSVGIcon(path.getSVGPath(), fill, hoverFill, size);
    }

    public static Group createSVGIcon(IconPath path, String fill, String hoverFill) {
        return createSVGIcon(path, fill, hoverFill, DEFAULT_ICON_SIZE);
    }

    public static Group createSVGIcon(IconPath path, String fill) {
        return createSVGIcon(path, fill, fill);
    }

    public static Group createSVGIcon(IconPath path) {
        return createSVGIcon(path, DEFAULT_ICON_COLOR);
    }
}
