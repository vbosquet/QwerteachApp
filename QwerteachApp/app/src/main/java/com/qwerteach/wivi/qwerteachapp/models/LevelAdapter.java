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
        textView.setTextColor(Color.BLACK);
        textView.setText(levels.get(position).getLevelName());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setTextColor(Color.BLACK);
        tv.setText(levels.get(position).getLevelName());
        return v;
    }
}
