package item_organizer_client.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class MyAlerts {
    public static void showError(String title, String content) {
        showError(title, content, null);
    }

    public static void showError(String title, String content, Runnable runnableOnOk) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (runnableOnOk != null) {
                runnableOnOk.run();
            }
        }
    }

    public static void showInfo(String title, String content) {
        showInfo(title, content, null);
    }

    public static void showInfo(String title, String content, Runnable runnableOnOk) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (runnableOnOk != null) {
                runnableOnOk.run();
            }
        }
    }

    public static boolean showConfirmationDialog(String title, String content) {
        return showConfirmationDialog(title, content, null, null);
    }

    public static boolean showConfirmationDialog(String title, String content, Runnable runnableOnOk) {
        return showConfirmationDialog(title, content, runnableOnOk, null);
    }

    public static boolean showConfirmationDialog(String title, String content, Runnable runnableOnOk, Runnable runnableOnCancel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (runnableOnOk != null) {
                runnableOnOk.run();
            }
            return true;
        } else {
            if (runnableOnCancel != null) {
                runnableOnCancel.run();
            }
            return false;
        }

    }

}
