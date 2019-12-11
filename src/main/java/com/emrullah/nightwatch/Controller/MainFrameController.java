package com.emrullah.nightwatch.Controller;

import com.emrullah.nightwatch.Common.WatcherServiceInitializr;

import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainFrameController {
    WatcherServiceInitializr watcherServiceInitializr = null;
    WatchService nightWatcher = null;

    public Button registerButton;
    public Button openPathButton;
    public Button watchButton;
    public Button compilerButton;
    public TextArea commandLineArea;
    public ListView registerList;
    public Label totalWatches;

    public void openPathDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        File selectedFile = directoryChooser.showDialog(openPathButton.getScene().getWindow());

        watcherServiceInitializr = new WatcherServiceInitializr(selectedFile.getPath());

        registerButton.setDisable(false);
    }

    public void listOfModulesForTheWatching() {
        List<File> listOfModules = watcherServiceInitializr.listOfModules();
        if(listOfModules == null || listOfModules.size() == 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("What am i supposed to do?");
            alert.setContentText("This root folder is empty. Nothing to watch!");
            alert.showAndWait();
        }else {
            registerList.getItems().addAll(listOfModules);
            watchButton.setDisable(false);
        }
    }

    public void startWatching() {
        try {
            nightWatcher = watcherServiceInitializr.initializeWatchService();

            totalWatches.setText(String.valueOf(watcherServiceInitializr.getKeyPathMap().size()));
            watchButton.setDisable(false);
            compilerButton.setDisable(false);
            registerButton.setDisable(true);
            openPathButton.setDisable(true);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(() -> {
                try {
                    watcherServiceInitializr.startListening(nightWatcher, commandLineArea);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            System.out.println("Watcher service couldn't initialize. Given path couldn't be a directory.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("System Error during listening folders");
            e.printStackTrace();
        }
    }
}
