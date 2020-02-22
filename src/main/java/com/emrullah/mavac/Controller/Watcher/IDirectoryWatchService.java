package com.emrullah.mavac.Controller.Watcher;

import java.io.IOException;
import java.nio.file.Path;

public interface IDirectoryWatchService extends IService {

    @Override
    void start();

    void register(OnFileChangeListener listener, Path path, String... globPatterns) throws IOException;

    /**
     * Interface definition for a callback to be invoked when a file under
     * watch is changed.
     */
    interface OnFileChangeListener {

        /**
         * Called when the file is created.
         * @param filePath The file path.
         */
        default void onFileCreate(Path filePath) {}

        /**
         * Called when the file is modified.
         * @param filePath The file path.
         */
        default void onFileModify(Path filePath) {}

        /**
         * Called when the file is deleted.
         * @param filePath The file path.
         */
        default void onFileDelete(Path filePath) {}
    }
}