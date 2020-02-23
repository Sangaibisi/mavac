package com.emrullah.mavac.Model;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Project extends Module {

    private List<Module> moduleList;
    private HashSet<Path> changedCompileList;
    private String projectName;

    public Project(File file) {
        super(file);
        this.moduleList = new ArrayList<>();
        this.changedCompileList = new HashSet<>();
    }

    public List<Module> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<Module> moduleList) {
        this.moduleList = moduleList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public HashSet<Path> getChangedCompileList() {
        return changedCompileList;
    }

    public void setChangedCompileList(HashSet<Path> changedCompileList) {
        this.changedCompileList = changedCompileList;
    }
}
