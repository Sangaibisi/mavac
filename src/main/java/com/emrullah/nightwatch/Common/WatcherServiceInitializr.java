package com.emrullah.nightwatch.Common;

import com.emrullah.nightwatch.Base.IWatcherServiceInitializr;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import org.controlsfx.dialog.ProgressDialog;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class WatcherServiceInitializr implements IWatcherServiceInitializr {

    private Map<WatchKey, Path> keyPathMap = new HashMap<WatchKey, Path>();
    private static String path;
    private List<File> selectedModules;
    private Task copyworker;
    private ProgressDialog progressDialog;

    public WatcherServiceInitializr(String _path) {
        this.path = _path;
    }

    @Override
    public WatchService initializeWatchService(List<File> fileList) throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        copyworker=createWorker(fileList,watchService);
        progressDialog = new ProgressDialog(copyworker);
        progressDialog.setTitle("Processing");
        progressDialog.setHeaderText("Watcher service is registering to given path");
        new Thread(copyworker).start();
        selectedModules = fileList;
        return watchService;
    }

    @Override
    public void startListening(WatchService watchService, TextArea commandLineArea) throws Exception {
        while (true) {
            WatchKey queuedKey = watchService.take();
            for (WatchEvent<?> watchEvent : queuedKey.pollEvents()) {
                WatchEvent.Kind<?> kind = watchEvent.kind();

                if (ENTRY_MODIFY.equals(kind)) {
                    Path path = (Path) watchEvent.context();
                    Path parentPath = keyPathMap.get(queuedKey);
                    path = parentPath.resolve(path);

                    commandLineArea.setText(commandLineArea.getText() + "\n" + path.toAbsolutePath().toString() + " is "+ watchEvent.kind());
                }else if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    // this is not a complete path
                    Path path = (Path) watchEvent.context();
                    // need to get parent path
                    Path parentPath = keyPathMap.get(queuedKey);
                    // get complete path
                    path = parentPath.resolve(path);
                    commandLineArea.setText(commandLineArea.getText() + "\n" + path.toAbsolutePath().toString() + " is "+ watchEvent.kind());
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

    public static List<File> listOfModules() {
        File root = new File(getPath());
        if(Arrays.asList(root.list()).isEmpty()) return null;
        return Arrays.asList(Objects.requireNonNull(root.listFiles((current, name) -> new File(current, name)
                .isDirectory())))
                .stream()
                .filter(p -> !p.getName().contains("."))
                .collect(Collectors.toList());
    }

    private void registerDir(Path path, WatchService watchService) throws IOException {
        if (!Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS) || path.toString().startsWith(".") || path.toString().contains("target")) {
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

    public static String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Task createWorker(List<File> fileList,WatchService watchService) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int i=0;
                for (File file : fileList) {
                    registerDir(Paths.get(file.getPath()), watchService);
                    updateProgress(i++,fileList.size());
                    updateMessage(file.getPath());
                }
                return true;
            }
        };
    }
}
