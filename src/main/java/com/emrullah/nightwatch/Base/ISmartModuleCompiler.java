package com.emrullah.nightwatch.Base;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;

public interface ISmartModuleCompiler {
    void preOperations();
    void startDeployment(HashSet<String> deploymentList);
}
