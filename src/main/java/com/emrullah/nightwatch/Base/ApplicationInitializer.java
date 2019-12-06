package com.emrullah.nightwatch.Base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ApplicationInitializer extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/MainFrame.fxml"));

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1024, 768));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}