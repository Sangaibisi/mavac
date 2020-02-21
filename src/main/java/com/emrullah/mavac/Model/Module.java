package com.emrullah.mavac.Model;

import com.emrullah.mavac.Utility.GeneralEnums;

import java.io.File;

public class Module {

    private File file;
    private GeneralEnums.PriorityModules modulePriority;

    public Module(File file) {
        this.file = file;
        this.modulePriority = null;
    }

    public Module(File file, GeneralEnums.PriorityModules modulePriority) {
        this.file = file;
        this.modulePriority = modulePriority;
    }

    public File getFile() {
        return file;
    }

    public GeneralEnums.PriorityModules getModulePriority() {
        return modulePriority;
    }
}
