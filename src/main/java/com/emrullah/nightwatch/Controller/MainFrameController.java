package com.emrullah.nightwatch.Controller;

import com.emrullah.nightwatch.Common.WatcherServiceInitializr;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainFrameController{

    WatcherServiceInitializr watcherServiceInitializr = null;
    WatchService nightWatcher = null;

    public Button registerButton;
    public Button openPathButton;
    public Button watchButton;
    public Button compilerButton;
    public TextArea commandLineArea;
    public ListView registerList;
    public Label totalWatches;


    public void openPathDialog(){
        registerButton.setDisable(false);
    }

    public void registerToFolders(){
        watcherServiceInitializr = new WatcherServiceInitializr();
        try {
            nightWatcher = watcherServiceInitializr.initializeWatchService();

            for (Map.Entry<WatchKey, Path> entry : watcherServiceInitializr.getKeyPathMap().entrySet()) {
                registerList.getItems().add(entry.getValue());
            }
            totalWatches.setText(String.valueOf(watcherServiceInitializr.getKeyPathMap().size()));
            watchButton.setDisable(false);
            compilerButton.setDisable(false);

        } catch (IOException e) {
            System.out.println("Watcher service couldn't initialize. Given path couldn't be a directory.");
            e.printStackTrace();
        }
    }

    public void startWatching() {
        registerButton.setDisable(true);
        openPathButton.setDisable(true);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            executor.execute(() -> {
                try {
                    watcherServiceInitializr.startListening(nightWatcher, commandLineArea);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            System.out.println("System Error during listening folders");
            e.printStackTrace();
        }
    }
}
