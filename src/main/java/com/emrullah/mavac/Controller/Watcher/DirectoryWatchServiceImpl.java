package com.emrullah.mavac.Controller.Watcher;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirectoryWatchServiceImpl implements IDirectoryWatchService, Runnable {

    private static Logger logger;

    private final WatchService mWatchService;
    private final AtomicBoolean mIsRunning;
    private final ConcurrentMap<WatchKey, Path> mWatchKeyToDirPathMap;
    private final ConcurrentMap<Path, Set<OnFileChangeListener>> mDirPathToListenersMap;
    private final ConcurrentMap<OnFileChangeListener, Set<PathMatcher>> mListenerToFilePatternsMap;

    private ListView<String> listView;

    public int subDirCount = 0;

    /**
     * A simple argument constructor for creating a <code>DirectoryWatchService</code>.
     *
     * @throws IOException If an I/O error occurs.
     */
    public DirectoryWatchServiceImpl(ListView<String> listView) throws IOException {
        this.listView = listView;

        mWatchService = FileSystems.getDefault().newWatchService();
        mIsRunning = new AtomicBoolean(false);
        mWatchKeyToDirPathMap = newConcurrentMap();
        mDirPathToListenersMap = newConcurrentMap();
        mListenerToFilePatternsMap = newConcurrentMap();

        logger = LogManager.getContext().getLogger(getClass().getName());
    }

    @SuppressWarnings("unchecked")
    private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    private static <K, V> ConcurrentMap<K, V> newConcurrentMap() {
        return new ConcurrentHashMap<>();
    }

    private static <T> Set<T> newConcurrentSet() {
        return Collections.newSetFromMap(newConcurrentMap());
    }

    public static PathMatcher matcherForGlobExpression(String globPattern) {
        return FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
    }

    public static boolean matches(Path input, PathMatcher pattern) {
        return pattern.matches(input);
    }

    public static boolean matchesAny(Path input, Set<PathMatcher> patterns) {
        for (PathMatcher pattern : patterns) {
            if (matches(input, pattern)) {
                return true;
            }
        }

        return false;
    }

    private Path getDirPath(WatchKey key) {
        return mWatchKeyToDirPathMap.get(key);
    }

    private Set<OnFileChangeListener> getListeners(Path dir) {
        return mDirPathToListenersMap.get(dir);
    }

    private Set<PathMatcher> getPatterns(OnFileChangeListener listener) {
        return mListenerToFilePatternsMap.get(listener);
    }

    private Set<OnFileChangeListener> matchedListeners(Path dir, Path file) {
        return getListeners(dir)
                .stream()
                .filter(listener -> matchesAny(file, getPatterns(listener)))
                .collect(Collectors.toSet());
    }

    private void notifyListeners(WatchKey key) {
        for (WatchEvent<?> event : key.pollEvents()) {
            @SuppressWarnings("rawtypes")
            WatchEvent.Kind eventKind = event.kind();

            // Overflow occurs when the watch event queue is overflow with events.
            if (eventKind.equals(OVERFLOW)) {
                // TODO: Notify all listeners.
                return;
            }

            WatchEvent<Path> pathEvent = cast(event);
            Path file = pathEvent.context();

            // TODO remove
            Path dir = (Path) key.watchable();
            Path fullPath = dir.resolve(pathEvent.context());
            // TODO remove

            if (eventKind.equals(ENTRY_CREATE)) {
                matchedListeners(getDirPath(key), file).forEach(listener -> listener.onFileCreate(fullPath));
            } else if (eventKind.equals(ENTRY_MODIFY)) {
                matchedListeners(getDirPath(key), file).forEach(listener -> listener.onFileModify(fullPath));
            } else if (eventKind.equals(ENTRY_DELETE)) {
                matchedListeners(getDirPath(key), file).forEach(listener -> listener.onFileDelete(fullPath));
            }
        }
    }

    private boolean isSuitablePathToWatch(Path subDir) {
        if (subDir.toString().contains(File.separator + ".git")
                || subDir.toString().contains(File.separator + ".adf")
                || subDir.toString().contains(File.separator + ".data")
                || subDir.toString().contains(File.separator + "target")
                || subDir.toString().contains(File.separator + "Voucher")
                || subDir.toString().contains(File.separator + "Survey")
                || subDir.toString().contains(File.separator + "Scheduling")
                || subDir.toString().contains(File.separator + "MTSTestBase")
                || subDir.toString().contains(File.separator + "Cargo")) {
            return false;
        }
        return true;
    }

    @Override
    public void register(OnFileChangeListener listener, Path path, String... globPatterns) throws IOException {

        if (!Files.isDirectory(path)) {
            throw new IllegalArgumentException(path + " is not a directory.");
        }

        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                @Override
                public FileVisitResult preVisitDirectory(Path subPath, BasicFileAttributes attrs) throws IOException {
                    if (isSuitablePathToWatch(subPath)) {
                        subDirCount++;

                        if (!mDirPathToListenersMap.containsKey(subPath)) {
                            // May throw
                            WatchKey key = subPath.register(mWatchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

                            mWatchKeyToDirPathMap.put(key, subPath);
                            mDirPathToListenersMap.put(subPath, newConcurrentSet());
                        }

                        getListeners(subPath).add(listener);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }

        Set<PathMatcher> patterns = newConcurrentSet();

        for (String globPattern : globPatterns) {
            patterns.add(matcherForGlobExpression(globPattern));
        }

        if (patterns.isEmpty()) {
            patterns.add(matcherForGlobExpression("*")); // Match everything if no filter is found
        }

        mListenerToFilePatternsMap.put(listener, patterns);

        listView.getItems().add("Watching " + subDirCount + " sub directories under " + path);
        logger.debug(getThreadName() + " | Watching " + subDirCount + " sub directories under " + path);
    }

    @Override
    public void start() {
        if (mIsRunning.compareAndSet(false, true)) {
            Thread runnerThread = new Thread(this, DirectoryWatchServiceImpl.class.getSimpleName());
            runnerThread.start();
        }
    }

    @Override
    public void stop() {
        mIsRunning.set(false);
    }

    @Override
    public void run() {
        logger.debug(getThreadName() + " | Starting file watcher service.");

        while (mIsRunning.get()) {

            WatchKey key;
            try {
                key = mWatchService.take();
            } catch (InterruptedException e) {
                logger.error(getThreadName() + " | " + DirectoryWatchServiceImpl.class.getSimpleName() + " service interrupted.");
                break;
            }

            if (null == getDirPath(key)) {
                logger.warn(getThreadName() + " | Watch key not recognized.");
                continue;
            }

            notifyListeners(key);

            // Reset key to allow further events for this key to be processed.
            boolean valid = key.reset();

            if (!valid) {
                mWatchKeyToDirPathMap.remove(key);
                if (mWatchKeyToDirPathMap.isEmpty()) {
                    break;
                }
            }

        }

        mIsRunning.set(false);

        addItemToListView("Stopping file watcher service...");
        logger.debug(getThreadName() + " | Stopping file watcher service.");
    }

    private void addItemToListView(String item) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                listView.getItems().add(item);
            }
        });
    }

    private static String getThreadName() {
        return Thread.currentThread().getName();
    }

    public int getSubDirCount() {
        return subDirCount;
    }

}
