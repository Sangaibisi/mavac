package com.emrullah.nightwatch.Common;

import com.emrullah.nightwatch.Base.IWatcherServiceInitializr;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static java.nio.file.StandardWatchEventKinds.*;

public class WatcherServiceInitializr implements IWatcherServiceInitializr {

    private Map<WatchKey, Path> keyPathMap = new HashMap<WatchKey, Path>();
    private String path;

    public WatcherServiceInitializr(String _path) {
        this.path = _path;
    }

    @Override
    public WatchService initializeWatchService() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        registerDir(Paths.get(getPath()), watchService);
        return watchService;
    }

    @Override
    public WatchService initializeWatchService(List<File> fileList) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        for (File file : fileList) {
            registerDir(Paths.get(file.getPath()), watchService);
        }
        return watchService;
    }

    @Override
    public void startListening(WatchService watchService, TextArea commandLineArea) throws Exception {
        while (true) {
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
                WatchEvent.Kind<?> kind = watchEvent.kind();
                String output = watchEvent.kind().toString() + watchEvent.count() + watchEvent.context();
                System.out.printf("\nEvent... kind=%s, count=%d, context=%s Context type=%s%n", watchEvent.kind(),
                        watchEvent.count(), watchEvent.context(), ((Path) watchEvent.context()).getClass());
                commandLineArea.setText(commandLineArea.getText() + "\n" + output);

                if (ENTRY_CREATE.equals(kind)) {
                    Path path = (Path) watchEvent.context();
                    System.out.println(path.toAbsolutePath().toString());
                } else if (ENTRY_MODIFY.equals(kind)) {
                    Path path = (Path) watchEvent.context();
                    System.out.println(path.toAbsolutePath().toString());
                } else if (ENTRY_DELETE.equals(kind)) {
                    Path path = (Path) watchEvent.context();
                    System.out.println(path.toAbsolutePath().toString());
                }

                if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    // this is not a complete path
                    Path path = (Path) watchEvent.context();
                    // need to get parent path
                    Path parentPath = keyPathMap.get(queuedKey);
                    // get complete path
                    path = parentPath.resolve(path);

                    registerDir(path, watchService);
                }
            }
            if (!queuedKey.reset()) {
                keyPathMap.remove(queuedKey);
            }
            if (keyPathMap.isEmpty()) {
                break;
            }
        }
    }

    public List<File> listOfModules() {
        File root = new File(getPath());
        if(Arrays.asList(root.list()).isEmpty()) return null;
        return Arrays.asList(Objects.requireNonNull(root.listFiles((current, name) -> new File(current, name).isDirectory())));
    }

    private void registerDir(Path path, WatchService watchService) throws IOException {
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }

        WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        keyPathMap.put(key, path);

        for (File f : path.toFile().listFiles()) {
            registerDir(f.toPath(), watchService);
        }
    }

    public Map<WatchKey, Path> getKeyPathMap() {
        return keyPathMap;
    }

    public void setKeyPathMap(Map<WatchKey, Path> keyPathMap) {
        this.keyPathMap = keyPathMap;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
