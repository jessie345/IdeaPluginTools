package com.example.plugin.beans;

import com.example.plugin.Constant;

public class DropdownItem {
    private String text;
    //item功能类型：0-普通打包命令 1-同步命令
    private int commandType;
    private boolean isRunning;

    public DropdownItem(String text, boolean isRunning) {
        this.text = text;
        this.isRunning = isRunning;
        this.commandType = Constant.COMMAND_TYPE_BUILD;
    }

    public DropdownItem(String text, boolean isRunning, int commandType) {
        this.text = text;
        this.isRunning = isRunning;
        this.commandType = commandType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getCommandType() {
        return commandType;
    }

    public void setCommandType(int commandType) {
        this.commandType = commandType;
    }

}
