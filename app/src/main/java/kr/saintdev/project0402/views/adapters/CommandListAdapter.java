package kr.saintdev.project0402.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.saintdev.project0402.R;
import kr.saintdev.project0402.models.datas.objects.CommandItem;

/**
 * Copyright (c) 2015-2018 Saint software All rights reserved.
 *
 * @Date 2018-05-30
 */

public class CommandListAdapter extends BaseAdapter {
    ArrayList<CommandItem> commandItems = new ArrayList<>();

    public void setItem(ArrayList<CommandItem> items) {
        this.commandItems = items;
    }

    public void addItem(CommandItem item) {
        this.commandItems.add(item);
    }

    public void clear() {
        this.commandItems.clear();
    }

    @Override
    public int getCount() {
        return commandItems.size();
    }

    @Override
    public CommandItem getItem(int position) {
        return commandItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater =
                    (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_command_item, null);
        }

        TextView title = convertView.findViewById(R.id.item_title);
        ImageView deleteButton = convertView.findViewById(R.id.item_delete);

        CommandItem item = commandItems.get(position);
        View.OnClickListener listener = item.getClickListener();
        if(listener != null) {
            deleteButton.setOnClickListener(listener);
            deleteButton.setTag(item.getCommandId());
        }

        title.setText(item.getCommand());

        return convertView;
    }
}
