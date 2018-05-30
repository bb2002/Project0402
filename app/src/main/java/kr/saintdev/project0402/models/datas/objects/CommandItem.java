package kr.saintdev.project0402.models.datas.objects;

import android.view.View;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class CommandItem {
    private String commandId = null;
    private String command = null;
    private View.OnClickListener clickListener = null;

    public CommandItem(String commandId, String command, View.OnClickListener clickListener) {
        this.commandId = commandId;
        this.command = command;
        this.clickListener = clickListener;
    }

    public String getCommandId() {
        return commandId;
    }

    public String getCommand() {
        return command;
    }

    public View.OnClickListener getClickListener() {
        return clickListener;
    }
}
