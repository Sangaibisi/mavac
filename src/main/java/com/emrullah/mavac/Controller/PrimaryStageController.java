package com.emrullah.mavac.Controller;

import com.emrullah.mavac.Base.ApplicationInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PrimaryStageController {

    private ApplicationInitializer mainApp;
    private static Logger logger;

    public void setMainApp(ApplicationInitializer mainApp) {
        this.mainApp = mainApp;
        logger = LogManager.getContext().getLogger(getClass().getName());
    }
}
