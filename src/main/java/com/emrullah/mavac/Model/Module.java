package com.emrullah.mavac.Model;

import com.emrullah.mavac.Utility.GeneralEnums;

import java.io.File;

public class Module {

    private File file;
    private GeneralEnums.PriorityModules modulePriority;
    private long moduleSizeBeforeRun;
    private long moduleSizeAfterRun;

    public Module(){}

    public Module(File file) {
        this.file = file;
        this.modulePriority = null;
        this.moduleSizeBeforeRun = 0;
    }

    public Module(File file, GeneralEnums.PriorityModules modulePriority) {
        this.file = file;
        this.modulePriority = modulePriority;
    }

    public File getFile() {
        return file;
    }

    public void setModuleSize(long moduleSizeBeforeRun) {
        this.moduleSizeBeforeRun = moduleSizeBeforeRun;
    }

    public long getModuleSize() {
        return moduleSizeBeforeRun;
    }

    public long getModuleSizeBeforeRun() {
        return moduleSizeBeforeRun;
    }

    public void setModuleSizeBeforeRun(long moduleSizeBeforeRun) {
        this.moduleSizeBeforeRun = moduleSizeBeforeRun;
    }

    public long getModuleSizeAfterRun() {
        return moduleSizeAfterRun;
    }

    public void setModuleSizeAfterRun(long moduleSizeAfterRun) {
        this.moduleSizeAfterRun = moduleSizeAfterRun;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isSizeChanged() {
        return moduleSizeBeforeRun == moduleSizeAfterRun ? true : false;
    }

    public void setModulePriority(GeneralEnums.PriorityModules modulePriority) {
        this.modulePriority = modulePriority;
    }

    public GeneralEnums.PriorityModules getModulePriority() {
        return modulePriority;
    }
}
