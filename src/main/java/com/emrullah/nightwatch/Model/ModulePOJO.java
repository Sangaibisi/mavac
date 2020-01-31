package com.emrullah.nightwatch.Model;

import com.emrullah.nightwatch.Common.GeneralEnumerationDefinitions;

import java.io.File;

public class ModulePOJO {
    private String moduleName;
    private File file;
    private GeneralEnumerationDefinitions.PriorityModules modulePriority;

    public ModulePOJO(String moduleName, File file, GeneralEnumerationDefinitions.PriorityModules modulePriority) {
        this.moduleName = moduleName;
        this.file = file;
        this.modulePriority = modulePriority;
    }

    public String getModuleName() {
        return moduleName;
    }

    public File getFile() {
        return file;
    }

    public GeneralEnumerationDefinitions.PriorityModules getModulePriority() {
        return modulePriority;
    }
}
