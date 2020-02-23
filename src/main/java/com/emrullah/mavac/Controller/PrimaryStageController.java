package com.emrullah.mavac.Controller;

import com.emrullah.mavac.Base.ApplicationInitializer;
import com.emrullah.mavac.Controller.Compiler.ModuleCompilerImpl;
import com.emrullah.mavac.Controller.Watcher.DirectoryWatchServiceImpl;
import com.emrullah.mavac.Model.Module;
import com.emrullah.mavac.Model.Project;
import com.emrullah.mavac.Utility.GeneralEnums;
import com.emrullah.mavac.Utility.MavacUtility;
import com.emrullah.nightwatch.Model.TableViewItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PrimaryStageController {

    @FXML public Button openPathButton;
    @FXML public Button synchronisationButton;
    @FXML public Button compilerButton;
    @FXML public Button resetButton;
    @FXML public ListView commandLineArea;
    @FXML public Label totalWatches;
    @FXML public Label totalModules;
    @FXML public TableColumn<TableViewItem, String> module;
    @FXML public TableColumn<TableViewItem, CheckBox> checkBox;
    @FXML public TableView registerList;
    @FXML public ImageView processImage;

    private static Logger logger;

    private DirectoryWatchServiceImpl directoryWatchService;
    private ModuleCompilerImpl moduleCompiler;
    private ObservableList<TableViewItem> moduleList = FXCollections.observableArrayList();
    private ApplicationInitializer mainApp;
    private Project theProject;

    public void setMainApp(ApplicationInitializer mainApp) {
        this.mainApp = mainApp;
        logger = LogManager.getContext().getLogger(getClass().getName());
    }

    @FXML
    private void actionOpenPath(){
        String thePath = MavacUtility.getDirectoryPathWithChooser(mainApp);
        theProject = new Project(new File(thePath));
        addItemsToTableView();
    }

    @FXML
    private void actionSynchronisation() {
        Path path = FileSystems.getDefault().getPath(theProject.getFile().getPath());
        processImage.setVisible(true);
        try {
            directoryWatchService = new DirectoryWatchServiceImpl(commandLineArea);
            directoryWatchService.register(
                    new DirectoryWatchServiceImpl.OnFileChangeListener() {
                        @Override
                        public void onFileCreate(Path filePath) {
                            logger.debug("File Create Event | " + filePath);
                            if(!filePath.toString().endsWith(".watchsrvc"))
                                Platform.runLater(() -> {
                                    theProject.getChangedCompileList().add(filePath);
                                    commandLineArea.getItems().add("\nNew file created at " + filePath.toAbsolutePath().toString());
                                });
                        }

                        @Override
                        public void onFileModify(Path filePath) {
                            logger.debug("File Modify Event | " + filePath);
                            Platform.runLater(() -> {
                                theProject.getChangedCompileList().add(filePath);
                                commandLineArea.getItems().add("\nFile modify event " + filePath.getFileName().toAbsolutePath().getFileName().toString());
                            });
                        }

                        @Override
                        public void onFileDelete(Path filePath) {
                            logger.debug("File Delete Event | " + filePath);
                            if(!filePath.toString().endsWith(".watchsrvc"))
                                Platform.runLater(() -> {
                                    theProject.getChangedCompileList().add(filePath);
                                    commandLineArea.getItems().add("\nFile delete event " + filePath.getFileName().toAbsolutePath().getFileName().toString());
                                });
                        }
                    },
                    path);

            directoryWatchService.start();

            compilerButton.setDisable(false);
            synchronisationButton.setDisable(true);
            totalWatches.setText(String.valueOf(directoryWatchService.getSubDirCount()));

        } catch (IOException e) {
            logger.error("Unable to register file change listener for " + theProject.getFile().getName());
        }
    }

    @FXML
    private void actionCompiler() {
        try {
            moduleCompiler = new ModuleCompilerImpl(commandLineArea);

            moduleCompiler.start();

            compilerButton.setDisable(true);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Maven unable to compile");
        }
    }

    private void addItemsToTableView(){
        List<File> listOfModules = MavacUtility.findSubFolders(theProject.getFile().getPath());
        if (listOfModules == null || listOfModules.isEmpty()) {
            MavacUtility.createAlert(AlertType.ERROR,"Error","Select proper project structure","This root folder is empty. Nothing to watch!");
        } else {
            for (File dir : listOfModules) {
                CheckBox checkBox = new CheckBox();
                moduleList.add(new TableViewItem(dir, dir.getName(), checkBox));

                Module newModule = new Module();
                newModule.setFile(dir);
                if(dir.getName().equals(GeneralEnums.PriorityModules.BASE.toString())){
                    newModule.setModulePriority(GeneralEnums.PriorityModules.BASE);
                }else if(dir.getName().equals(GeneralEnums.PriorityModules.COMMON.toString())){
                    newModule.setModulePriority(GeneralEnums.PriorityModules.COMMON);
                }else if(dir.getName().equals(GeneralEnums.PriorityModules.ESB.toString())){
                    newModule.setModulePriority(GeneralEnums.PriorityModules.ESB);
                }else{
                    newModule.setModulePriority(GeneralEnums.PriorityModules.OTHER);
                }

                newModule.setModuleSize(MavacUtility.getFileSizeWithAsync(dir));
                theProject.getModuleList().add(newModule);
            }

            module.setCellValueFactory(new PropertyValueFactory<>("fileName"));
            module.setStyle("-fx-alignment: CENTER;");
            checkBox.setStyle("-fx-alignment: CENTER;");
            checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

            registerList.setItems(moduleList);
            totalModules.setText(String.valueOf(listOfModules.size()));

            synchronisationButton.setDisable(false);
            openPathButton.setDisable(true);
        }
    }


}
