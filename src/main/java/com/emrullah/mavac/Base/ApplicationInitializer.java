package com.emrullah.mavac.Base;

import com.emrullah.mavac.Controller.PrimaryStageController;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 *
 * @author emrullah.yildirim
 * @since 21/02/2020
 * @version v1.0
 *
 */
public class ApplicationInitializer extends Application {

    private static Logger logger;
    private Stage primaryStage;
    private BorderPane mainWindowLayout;

    public static void main(String[] args) {
        logger = LogManager.getContext().getLogger(ApplicationInitializer.class.getName());
        logger.debug("------------------------------");
        logger.debug("Application starting");
        logger.error("asdasd");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{


        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("NightWatcher Service");
        this.primaryStage.setResizable(false);
        this.primaryStage.centerOnScreen();
        this.primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("crow.ico")));

        initMainWindowLayout();
    }

    private void initMainWindowLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("PrimaryStage.fxml"));
            Parent root = (Parent) loader.load();
            PrimaryStageController controller = loader.getController();

            controller.setMainApp(this);

            this.primaryStage.setScene(new Scene(root, 1024, 768));



            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
