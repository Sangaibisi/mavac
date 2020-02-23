package com.emrullah.mavac.Controller.Compiler;

import com.emrullah.mavac.Controller.Watcher.IService;

public interface IModuleCompiler extends IService {

    @Override
    void start() throws Exception;

    void preOperations();

    interface MavenCompile {

        /**
         * Called when the maven begin to compile.
         * @param path The file path.
         */
        default void onMavenCompile(String path) {}

    }
}
