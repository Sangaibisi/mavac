package com.emrullah.nightwatch.Base;

import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.List;

public interface IWatcherServiceInitializr {
    WatchService initializeWatchService() throws IOException;
    WatchService initializeWatchService(List<File> fileList) throws IOException;
    void startListening(WatchService watchService, TextArea commandLineArea) throws Exception;
}
