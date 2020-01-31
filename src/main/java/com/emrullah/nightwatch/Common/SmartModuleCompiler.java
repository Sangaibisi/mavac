package com.emrullah.nightwatch.Common;

import com.emrullah.nightwatch.Base.ISmartModuleCompiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.emrullah.nightwatch.Controller.MainFrameController;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FileUtils;
public class SmartModuleCompiler implements ISmartModuleCompiler {

    private List<File> moduleList = null;
    private HashMap<File,Long> sizeOfModulesBefore = new HashMap<>();
    private HashSet<Path> changedModules;
    private List<String> readyForDeploy;

    private HashSet<String> hashedPaths = new HashSet<>();

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

    @Override
    public void preDeploymentProcess() throws UnsupportedOperationException {
        HashSet<Path> changedPaths = WatcherServiceInitializr.ready4Deployment;
        List<File> changedFiles = new ArrayList<>();
        for (Path thePath : changedPaths){
            changedFiles.add(new File(thePath.toString()));
        }
        for(File file : changedFiles){
            if(!file.isDirectory()) continue;
            String temp = findPOMPath(file);
            if(temp != null) hashedPaths.add(temp);
        }

        if(!hashedPaths.isEmpty()) startDeployment(hashedPaths);
        else throw new UnsupportedOperationException();
    }

    @Override
    public void startDeployment(HashSet<String> deploymentList) {

    }

    private void goThreadForDeploy(String path){
            Thread executor =
                    new Thread(
                            () -> {
                                String maven = "C:\\apache-maven-3.0.5\\bin\\mvn";
                                Process process = null;
                                try {
                                    process = Runtime.getRuntime().exec(
                                            "cmd.exe /c "
                                                    + maven
                                                    + " "
                                                    + "clean install -Dmaven.test.skip"
                                                    + " -f "
                                                    + path
                                                    + "\\pom.xml");
                                    BufferedReader reader =
                                            new BufferedReader(new InputStreamReader(process.getInputStream()));
                                    String buffer = reader.readLine();
                                    while (buffer != null) {
                                        buffer = reader.readLine();
                                        System.out.println(buffer);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
            executor.start();
    }

    private String findPOMPath(File file){
        String path = file.getPath();
        if(!Arrays.asList(file.listFiles()).stream().filter(p->p.getName().contains("pom.xml")).collect(Collectors.toList()).isEmpty()){
            return path;
        }

        return findPOMPath(new File(file.getParent()));
    }

    /*private String commonPath(String[] paths){
        String commonPath = "";
        String[][] folders = new String[paths.length][];
        for(int i = 0; i < paths.length; i++){
            folders[i] = paths[i].split("\\\\"); //split on file separator
        }
        for(int j = 0; j < folders[0].length; j++){
            String thisFolder = folders[0][j]; //grab the next folder name in the first path
            boolean allMatched = true; //assume all have matched in case there are no more paths
            for(int i = 1; i < folders.length && allMatched; i++){ //look at the other paths
                if(folders[i].length < j){ //if there is no folder here
                    allMatched = false; //no match
                    break; //stop looking because we've gone as far as we can
                }
                //otherwise
                allMatched &= folders[i][j].equals(thisFolder); //check if it matched
            }
            if(allMatched){ //if they all matched this folder name
                commonPath += thisFolder + "/"; //add it to the answer
            }else{//otherwise
                break;//stop looking
            }
        }
        return commonPath;
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

    private List<String> findSetOfIntersection(HashSet<Path> sectionA, List<File> sectionB){
        List<String> setOfIntersection = new ArrayList<>();
        List<Path> temp;
        List<String> paths = new ArrayList<>();

        for (File theModule : sectionB) {
            temp = sectionA.stream().filter(p -> p.toString().contains(theModule.getName())).collect(Collectors.toList());
            if(!temp.isEmpty()){
                for(Path thePath : temp){
                    paths.add(thePath.toAbsolutePath().toString());
                }
                setOfIntersection.add(commonPath(Arrays.copyOf(paths.toArray(),paths.size(),String[].class)));
                paths.clear();
            }
        }

        return setOfIntersection;
    }*/

}
