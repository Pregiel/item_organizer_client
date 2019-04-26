package item_organizer_client.utils;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.shape.SVGPath;

public class Icon {
    private static final int DEFAULT_ICON_SIZE = 16;

    public static Button setIconButton(Button button, Node graphic) {
        button.getStyleClass().add("no-background-button");
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        button.setGraphic(graphic);
        return button;
    }

    public static Button createIconButton(Node graphic) {
        Button button = new Button("", graphic);
        button.getStyleClass().add("no-background-button");
        button.getStyleClass().add("highlight-onhover-button");
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
        return createSVGIcon(path, IconGraphic.DEFAULT_ICON_COLOR);
    }

    public static Group createSVGIcon(IconGraphic icon, String fill, String hoverFill, int size) {
        return createSVGIcon(icon.getSVGPath(), fill, hoverFill, size);
    }

    public static Group createSVGIcon(IconGraphic icon, String fill, String hoverFill) {
        return createSVGIcon(icon, fill, hoverFill, DEFAULT_ICON_SIZE);
    }

    public static Group createSVGIcon(IconGraphic icon, String fill) {
        return createSVGIcon(icon, fill, fill);
    }

    public static Group createSVGIcon(IconGraphic icon, String fill, int size) {
        return createSVGIcon(icon, fill, fill, size);
    }

    public static Group createSVGIcon(IconGraphic icon) {
        return createSVGIcon(icon, icon.getColor(), icon.getHoverColor());
    }
}
