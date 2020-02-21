package com.emrullah.mavac.Utility;

import com.emrullah.mavac.Base.ApplicationInitializer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MavacUtility {


    private static Logger logger = LogManager.getContext().getLogger(MavacUtility.class.getName());

    private static String localVersion = "1.0.0";

    public static String getDirectoryPathWithChooser(ApplicationInitializer mainApp) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory =  directoryChooser.showDialog(((ApplicationInitializer)mainApp).getPrimaryStage());

        return selectedDirectory != null ? selectedDirectory.getAbsolutePath() : "";
    }

    public static List<File> findSubFolders(String path) {
        File root = new File(path);
        if(Arrays.asList(root.list()).isEmpty()) return null;
        return Arrays.asList(Objects.requireNonNull(root.listFiles((current, name) -> new File(current, name)
                .isDirectory())))
                .stream()
                .filter(p -> !p.getName().startsWith("."))
                .collect(Collectors.toList());
    }

    public static void createAlert(AlertType alertType, String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            alert.showAndWait();
        });
    }
}
