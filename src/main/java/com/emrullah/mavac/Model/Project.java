package com.emrullah.mavac.Model;

import com.emrullah.mavac.Utility.GeneralEnums;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Project extends Module {

    private List<Module> moduleList = new ArrayList<Module>();
    private String projectName;

    public Project(File file) {
        super(file);
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
}
