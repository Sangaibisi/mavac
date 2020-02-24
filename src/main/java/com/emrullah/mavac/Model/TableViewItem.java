package com.emrullah.mavac.Model;

import javafx.scene.control.CheckBox;

import java.io.File;

public class TableViewItem {

    private File file;
    private String fileName;
    private CheckBox checkBox;

    public TableViewItem(File file, String fileName, CheckBox checkBox) {
        this.fileName = fileName;
        this.checkBox = checkBox;
        this.file=file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}
