package com.emrullah.mavac.Utility;

import com.emrullah.mavac.Base.ApplicationInitializer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
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

    public static long getFileSizeWithAsync(File file) {
        AtomicLong fileSize = new AtomicLong();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executor.execute(() -> {
            try {
                fileSize.set(FileUtils.sizeOf(file));
                logger.info("Size of "+ file.getName() + fileSize);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdownNow();
            }
        });

        return fileSize.get();
    }

    public static String findPOMPath(File file){
        if(!file.isDirectory()) return null;

        String path = file.getPath();
        if(!Arrays.asList(file.listFiles()).stream().filter(p->p.getName().contains("pom.xml")).collect(Collectors.toList()).isEmpty()){
            return path;
        }

        return findPOMPath(new File(file.getParent()));
    }

    public static HashSet<String> normalizationOfChangedModuleList(HashSet<Path> changedModules) {
        if(changedModules.isEmpty()) return null;

        HashSet<String> determinedList = new HashSet<String>();
        for (Path path : changedModules) {
            String pomPath = findPOMPath(new File(path.toString()));
            if (pomPath != null && !pomPath.isEmpty())
                determinedList.add(pomPath);
        }
        return determinedList;
    }
}
