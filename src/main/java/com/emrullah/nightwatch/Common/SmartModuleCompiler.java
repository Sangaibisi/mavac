package com.emrullah.nightwatch.Common;

import com.emrullah.nightwatch.Base.ISmartModuleCompiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
public class SmartModuleCompiler implements ISmartModuleCompiler {

    private List<File> moduleList = null;
    private HashMap<File,Long> sizeOfModulesBefore = new HashMap<>();
    private HashSet<Path> changedModules;
    private List<File> ready4DeploymentList;

    public SmartModuleCompiler(List _moduleList) throws IOException {
        if(_moduleList == null || _moduleList.isEmpty()) throw new IOException();
        this.moduleList=_moduleList;
        preOperations();
    }

    @Override
    public void preOperations(){
        long begin = System.currentTimeMillis();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executor.execute(() -> {
            try {
                for(File module : moduleList){
                    long fileSize = FileUtils.sizeOf(module);
                    sizeOfModulesBefore.put(module,fileSize);
                    System.out.println("The size of "+module.getName()+" "+fileSize);

                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                executor.shutdownNow();
            }
        });
        long end = System.currentTimeMillis();
        System.out.println("Total execution time : "+ (end - begin));
    }

    public void decideWhichModulesWillBeDeploy() throws UnsupportedOperationException{
        HashMap<File,Long> sizeOfModulesAfter = new HashMap<>();
        for(File module : moduleList){
            long fileSize = FileUtils.sizeOf(module);
            sizeOfModulesAfter.put(module,fileSize);
            System.out.println("The size of " + module.getName() + " " + fileSize);
        }

        changedModules = WatcherServiceInitializr.ready4Deployment;
        if(changedModules.isEmpty() && compareSizeOfModules(sizeOfModulesAfter).isEmpty()){
            throw new UnsupportedOperationException();
        }else if (!changedModules.isEmpty()){
            List<File> changedSizeModuleList = compareSizeOfModules(sizeOfModulesAfter);
            if(!changedSizeModuleList.isEmpty()){
                ready4DeploymentList = findSetOfIntersection(changedModules,changedSizeModuleList);

            }
        }
    }

    private List<File> compareSizeOfModules(HashMap<File,Long> sizeOfModulesAfter){
        List<File> changedModules = new ArrayList<>();
        for(HashMap.Entry<File,Long> theModule : sizeOfModulesAfter.entrySet()){
            if(!sizeOfModulesBefore.get(theModule.getKey()).equals(theModule.getValue())){
                changedModules.add(theModule.getKey());
            }
        }
        return changedModules;
    }

    private List<File> findSetOfIntersection(HashSet<Path> sectionA, List<File> sectionB){
        List<File> setOfIntersection = new ArrayList<>();

        return setOfIntersection;
    }

}
