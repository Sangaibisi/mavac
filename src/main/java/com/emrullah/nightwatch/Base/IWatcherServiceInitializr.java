package com.emrullah.nightwatch.Base;

import java.io.IOException;
import java.nio.file.WatchService;

public interface IWatcherServiceInitializr {
    WatchService initializeWatchService() throws IOException;
}
