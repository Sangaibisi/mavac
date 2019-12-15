package com.emrullah.nightwatch.Model;

import javafx.scene.control.CheckBox;

import java.io.File;

public class TableView {

    private String path;
    private CheckBox checkBox;

    public TableView(String path, CheckBox checkBox) {
        this.path = path;
        this.checkBox = checkBox;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
