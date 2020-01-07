package com.emrullah.nightwatch.Common;

import com.emrullah.nightwatch.Base.ISmartModuleCompiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SmartModuleCompilerInitializr implements ISmartModuleCompiler {

    private ArrayList<File> moduleList = null;
    private HashMap<File,Integer> moduleSizeList = new HashMap<>();

    public SmartModuleCompilerInitializr(ArrayList _moduleList) throws IOException {
        if(moduleList == null || moduleList.isEmpty()) throw new IOException();
        this.moduleList=_moduleList;
        preOperations();
    }

    @Override
    public void preOperations(){
        for(File module : moduleList){

        }
    }
}
