package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wivi on 27/01/17.
 */

public class LevelAdapter extends ArrayAdapter<Level> {

    private Context context;
    private ArrayList<Level> levels;

    public LevelAdapter(Context context, int simple_spinner_item, ArrayList<Level> levels) {
        super(context, simple_spinner_item, levels);
        this.context = context;
        this.levels = levels;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        if (levels.get(position).isNeedBeLevel()) {
            textView.setText(levels.get(position).getBeLevelName());
        } else {
            textView.setText(levels.get(position).getFrLevelName());
        }
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        if (levels.get(position).isNeedBeLevel()) {
            tv.setText(levels.get(position).getBeLevelName());
        } else {
            tv.setText(levels.get(position).getFrLevelName());
        }
        return v;
    }
}
