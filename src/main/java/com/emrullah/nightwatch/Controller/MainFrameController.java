package com.emrullah.nightwatch.Controller;

import com.emrullah.nightwatch.Common.WatcherServiceInitializr;
import com.github.lalyos.jfiglet.FigletFont;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainFrameController {

    @FXML
    public Button openPathButton;
    @FXML
    public Button watchButton;
    @FXML
    public Button compilerButton;
    @FXML
    public Button stopWatchingButton;
    @FXML
    public TextArea commandLineArea;
    @FXML
    public Label totalWatches;
    @FXML
    public Label totalModules;
    @FXML
    public TableColumn<com.emrullah.nightwatch.Model.TableView, String> module;
    @FXML
    public TableColumn<com.emrullah.nightwatch.Model.TableView, CheckBox> checkBox;
    @FXML
    public TableView registerList;


    WatcherServiceInitializr watcherServiceInitializr = null;
    WatchService nightWatcher = null;
    ObservableList<com.emrullah.nightwatch.Model.TableView> moduleList = FXCollections.observableArrayList();


    public void openPathDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        File selectedFile = directoryChooser.showDialog(openPathButton.getScene().getWindow());

        watcherServiceInitializr = new WatcherServiceInitializr(selectedFile.getPath());
        addItemsToTableView();
    }

    public void startWatching() {
        writeIntro();
        stopWatchingButton.setDisable(false);
        registerList.setDisable(true);

        if(getSelectedModuleList() == null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("What am i supposed to do?");
            alert.setContentText("This root folder is empty. Nothing to watch!");
            alert.showAndWait();
        }else if(getSelectedModuleList().isEmpty()){
            for( com.emrullah.nightwatch.Model.TableView item : moduleList){
                item.getCheckBox().setSelected(true);
            }
            commandLineArea.setText(commandLineArea.getText()+"\n All modules will be under watch!");
        }
      /*  try {
            nightWatcher = watcherServiceInitializr.initializeWatchService();

            totalWatches.setText(String.valueOf(watcherServiceInitializr.getKeyPathMap().size()));
            watchButton.setDisable(false);
            compilerButton.setDisable(false);
            openPathButton.setDisable(true);

            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(() -> {
                try {
                    watcherServiceInitializr.startListening(nightWatcher, commandLineArea);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            System.out.println("Watcher service couldn't initialize. Given path couldn't be a directory.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("System Error during listening folders");
            e.printStackTrace();
        }*/
    }

    private ObservableList<com.emrullah.nightwatch.Model.TableView> getSelectedModuleList(){
        if(moduleList == null || moduleList.isEmpty()){
            return null;
        }
        ObservableList<com.emrullah.nightwatch.Model.TableView> selectedModuleList = FXCollections.observableArrayList();
        for(com.emrullah.nightwatch.Model.TableView item : moduleList){
            if(item.getCheckBox().isSelected()){
                selectedModuleList.add(item);
            }
        }
        return selectedModuleList;
    }

    private void addItemsToTableView() {
        List<File> listOfModules = watcherServiceInitializr.listOfModules();
        if (listOfModules == null || listOfModules.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("What am i supposed to do?");
            alert.setContentText("This root folder is empty. Nothing to watch!");
            alert.showAndWait();
        } else {
            for (File dir : listOfModules) {
                CheckBox checkBox = new CheckBox();
                moduleList.add(new com.emrullah.nightwatch.Model.TableView(dir, dir.getName(), checkBox));
            }

            module.setCellValueFactory(new PropertyValueFactory<>("fileName"));
            module.setStyle( "-fx-alignment: CENTER;");
            checkBox.setStyle( "-fx-alignment: CENTER;");
            checkBox.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

            registerList.setItems(moduleList);
            totalModules.setText(String.valueOf(listOfModules.size()));

            watchButton.setDisable(false);
            openPathButton.setDisable(true);
        }
    }

    private void writeIntro() {
        commandLineArea.setText(commandLineArea.getText()+FigletFont.convertOneLine("Welcome"));
        commandLineArea.setText(commandLineArea.getText()+FigletFont.convertOneLine("NightWatch    is    begin"));
    }
}
