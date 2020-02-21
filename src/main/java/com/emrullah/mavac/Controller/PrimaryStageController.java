package com.emrullah.mavac.Controller;

import com.emrullah.mavac.Base.ApplicationInitializer;
import com.emrullah.mavac.Model.Module;
import com.emrullah.mavac.Model.Project;
import com.emrullah.mavac.Utility.GeneralEnums;
import com.emrullah.mavac.Utility.MavacUtility;
import com.emrullah.nightwatch.Model.TableViewItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrimaryStageController {

    @FXML public Button openPathButton;
    @FXML public Button watchButton;
    @FXML public Button compilerButton;
    @FXML public Button stopWatchingButton;
    @FXML public TextArea commandLineArea;
    @FXML public Label totalWatches;
    @FXML public Label totalModules;
    @FXML public TableColumn<TableViewItem, String> module;
    @FXML public TableColumn<TableViewItem, CheckBox> checkBox;
    @FXML public TableView registerList;

    private static Logger logger;

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

    private void addItemsToTableView(){
        List<File> listOfModules = MavacUtility.findSubFolders(theProject.getFile().getPath());
        if (listOfModules == null || listOfModules.isEmpty()) {
            MavacUtility.createAlert(AlertType.ERROR,"Error","Select proper project structure","This root folder is empty. Nothing to watch!");
        } else {
            for (File dir : listOfModules) {
                CheckBox checkBox = new CheckBox();
                moduleList.add(new TableViewItem(dir, dir.getName(), checkBox));

                if(dir.getName().equals(GeneralEnums.PriorityModules.BASE.toString())){
                    theProject.getModuleList().add(new Module(dir,GeneralEnums.PriorityModules.BASE));
                }else if(dir.getName().equals(GeneralEnums.PriorityModules.COMMON.toString())){
                    theProject.getModuleList().add(new Module(dir,GeneralEnums.PriorityModules.BASE));
                }else if(dir.getName().equals(GeneralEnums.PriorityModules.ESB.toString())){
                    theProject.getModuleList().add(new Module(dir,GeneralEnums.PriorityModules.BASE));
                }else{
                    theProject.getModuleList().add(new Module(dir,GeneralEnums.PriorityModules.BASE));
                }
            }

            module.setCellValueFactory(new PropertyValueFactory<>("fileName"));
            module.setStyle("-fx-alignment: CENTER;");
            checkBox.setStyle("-fx-alignment: CENTER;");
            checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

            registerList.setItems(moduleList);
            totalModules.setText(String.valueOf(listOfModules.size()));

            watchButton.setDisable(false);
            openPathButton.setDisable(true);
        }
    }


}
