package com.emrullah.mavac.Utility;

import com.emrullah.mavac.Controller.Compiler.ITaskExecutorListener;
import com.emrullah.mavac.Controller.Compiler.UpdaterState;
import com.emrullah.mavac.Model.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandExecutorUtil {

    private static Logger logger = LogManager.getContext().getLogger(MavacUtility.class.getName());
    private static Runtime runtime = Runtime.getRuntime();

    private final AtomicBoolean mIsRunning;

    public CommandExecutorUtil() {
        mIsRunning = new AtomicBoolean(false);
    }

    public static void executeCommand(Project theProject, String path, final String command, final ITaskExecutorListener listener) {
        Thread executor =
                new Thread(
                        () -> {
                            UpdaterState updaterState = startConsoleUpdater(listener);
                            try {
                                String maven = theProject.getMavenHome();
                                Process process = null;
                                    process =
                                            runtime.exec(
                                                    "cmd.exe /c "
                                                            + maven
                                                            + " "
                                                            + command
                                                            + " -f "
                                                            + path
                                                            + "/pom.xml");

                                BufferedReader reader =
                                        new BufferedReader(new InputStreamReader(process.getInputStream()));
                                String buffer = reader.readLine();
                                while (buffer != null) {
                                    logger.info(buffer);
                                    theProject.getConsoleLog().append(buffer);
                                    theProject.getConsoleLog().append("\n");
                                    buffer = reader.readLine();
                                }
                                process.waitFor();
                            } catch (Exception e) {
                                theProject.getConsoleLog().append(e.getMessage());
                                theProject.getConsoleLog().append("\n");
                            }
                            listener.executed();
                            if (updaterState.isWork()) {
                                updaterState.setWork(false);
                            }
                        });
        executor.start();
    }

    private static UpdaterState startConsoleUpdater(final ITaskExecutorListener listener) {
        final UpdaterState state = new UpdaterState();
        state.setWork(true);

        Thread updater =
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                while (state.isWork()) {
                                    listener.updateConsole();
                                    try {
                                        synchronized (this) {
                                            this.wait(500);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        updater.start();
        return state;
    }
}
