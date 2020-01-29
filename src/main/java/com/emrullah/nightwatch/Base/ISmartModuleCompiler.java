package com.emrullah.nightwatch.Base;

import javafx.scene.control.TextArea;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public interface ISmartModuleCompiler {

    void preOperations();
    void preDeploymentProcess() throws UnsupportedOperationException;

    void startDeployment(HashSet<String> deploymentList);
}
