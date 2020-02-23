package com.emrullah.mavac.Controller.Compiler;

import com.emrullah.mavac.Model.Module;
import com.emrullah.mavac.Model.Project;
import com.emrullah.mavac.Utility.MavacUtility;
import javafx.scene.control.ListView;

import java.util.concurrent.atomic.AtomicBoolean;

public class ModuleCompilerImpl implements IModuleCompiler, Runnable {

    private final AtomicBoolean mIsRunning;
    private ListView<String> listView;

    public ModuleCompilerImpl(ListView<String> listView){
        mIsRunning = new AtomicBoolean(false);
        this.listView=listView;
    }

    @Override
    public void preOperations() {
        for(Module temp : theProject.getModuleList()){
            temp.setModuleSizeAfterRun(MavacUtility.getFileSizeWithAsync(temp.getFile()));
        }

    }

    @Override
    public void run() {

    }

    @Override
    public void start() throws Exception {
        if (mIsRunning.compareAndSet(false, true)) {
            Thread runnerThread = new Thread(this, ModuleCompilerImpl.class.getSimpleName());
            runnerThread.start();
        }
    }

    @Override
    public void stop() {

    }
}
