package com.emrullah.nightwatch.Base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationInitializer extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("MainFrame.fxml"));

        primaryStage.setTitle("NightWatcher Service");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

}