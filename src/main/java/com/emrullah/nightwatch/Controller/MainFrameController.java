package com.emrullah.nightwatch.Controller;

import com.emrullah.nightwatch.Base.ApplicationInitializer;
import com.emrullah.nightwatch.Common.GeneralEnumerationDefinitions;
import com.emrullah.nightwatch.Common.SmartModuleCompiler;
import com.emrullah.nightwatch.Common.WatcherServiceInitializr;
import com.emrullah.nightwatch.Model.ModulePOJO;
import com.emrullah.nightwatch.Model.TableViewItem;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchService;
import java.util.ArrayList;
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
    public TableColumn<TableViewItem, String> module;
    @FXML
    public TableColumn<TableViewItem, CheckBox> checkBox;
    @FXML
    public TableView registerList;

    private WatcherServiceInitializr watcherServiceInitializr = null;
    private SmartModuleCompiler smci = null;
    private WatchService nightWatcher = null;
    private ObservableList<TableViewItem> moduleList = FXCollections.observableArrayList();
    private ExecutorService executor;
    private List<ModulePOJO> projectModule;

    public void openPathDialog() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource File");
        File selectedFile = directoryChooser.showDialog(openPathButton.getScene().getWindow());

        watcherServiceInitializr = new WatcherServiceInitializr(selectedFile.getPath());
        addItemsToTableView();
    }

    public void startWatching() {
        try {
            preWatchingOperations();

            Platform.runLater( () -> {
                totalWatches.setText(String.valueOf(watcherServiceInitializr.getKeyPathMap().size()));
            });
            watchButton.setDisable(false);
            compilerButton.setDisable(false);
            openPathButton.setDisable(true);
            watchButton.setDisable(true);
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
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
        }
    }

    public void startCompile() {
        try {
            smci.preDeploymentProcess();
            nightWatcher.close();
            executor.shutdownNow();
        } catch (UnsupportedOperationException | IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("What am i supposed to do?");
            alert.setContentText("No changes detected. Nothing to deploy!");
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    public void stopWatching() {
        try {
            nightWatcher.close();
            executor.shutdownNow();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("CRASH!");
            alert.setContentText("The main thread is crashed. Please close application!");
            alert.showAndWait();
        } finally {
            resetNighWatch();
        }
    }

    private void resetNighWatch() {
        Stage stage = (Stage) compilerButton.getScene().getWindow();
        stage.close();
        Platform.runLater( () -> {
            try {
                new ApplicationInitializer().start( new Stage() );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void preWatchingOperations() throws IOException {
        writeIntro();

        stopWatchingButton.setDisable(false);
        registerList.setDisable(true);

        if (getSelectedModuleList() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("What am i supposed to do?");
            alert.setContentText("This root folder is empty. Nothing to watch!");
            alert.showAndWait();

        } else if (getSelectedModuleList().isEmpty()) {
            for (TableViewItem item : moduleList) {
                item.getCheckBox().setSelected(true);
            }
            nightWatcher = watcherServiceInitializr.initializeWatchService(WatcherServiceInitializr.listOfModules());
            smci = new SmartModuleCompiler(watcherServiceInitializr.listOfModules());
            commandLineArea.setText(commandLineArea.getText() + "\nAll modules are under watching!");

        } else {
            List<File> fileList = new ArrayList<>();
            for (TableViewItem item : getSelectedModuleList()) {
                if (item.getCheckBox().isSelected()) {
                    fileList.add(item.getFile());
                    commandLineArea.setText(commandLineArea.getText() + "\n" + item.getFileName() + " is under watching");
                }
            }
            nightWatcher = watcherServiceInitializr.initializeWatchService(fileList);
            smci = new SmartModuleCompiler(fileList);
        }
    }

    private ObservableList<TableViewItem> getSelectedModuleList() {
        if (moduleList == null || moduleList.isEmpty()) {
            return null;
        }
        ObservableList<TableViewItem> selectedModuleList = FXCollections.observableArrayList();
        for (TableViewItem item : moduleList) {
            if (item.getCheckBox().isSelected()) {
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
            projectModule = new ArrayList<>();
            for (File dir : listOfModules) {
                CheckBox checkBox = new CheckBox();
                moduleList.add(new TableViewItem(dir, dir.getName(), checkBox));

                if(dir.getName().equals(GeneralEnumerationDefinitions.PriorityModules.BASE.toString())){
                    projectModule.add(new ModulePOJO(dir.getName(),dir,GeneralEnumerationDefinitions.PriorityModules.BASE));
                }else if(dir.getName().equals(GeneralEnumerationDefinitions.PriorityModules.COMMON.toString())){
                    projectModule.add(new ModulePOJO(dir.getName(),dir,GeneralEnumerationDefinitions.PriorityModules.COMMON));
                }else if(dir.getName().equals(GeneralEnumerationDefinitions.PriorityModules.ESB.toString())){
                    projectModule.add(new ModulePOJO(dir.getName(),dir,GeneralEnumerationDefinitions.PriorityModules.ESB));
                }else{
                    projectModule.add(new ModulePOJO(dir.getName(),dir,GeneralEnumerationDefinitions.PriorityModules.OTHER));
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

    private void writeIntro() {
        commandLineArea.setText(commandLineArea.getText() + "NightWatch was started to listening given path\n--------------------------------------------------------------------------------------------------------------");
    }
}
