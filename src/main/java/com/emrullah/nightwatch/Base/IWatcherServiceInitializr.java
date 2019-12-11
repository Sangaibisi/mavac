package com.emrullah.nightwatch.Base;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.WatchService;

public interface IWatcherServiceInitializr {
    WatchService initializeWatchService() throws IOException;
    void startListening(WatchService watchService, TextArea commandLineArea) throws Exception;
}
