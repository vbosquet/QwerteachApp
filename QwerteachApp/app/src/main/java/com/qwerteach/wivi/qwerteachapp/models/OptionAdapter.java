package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wivi on 30/01/17.
 */

public class OptionAdapter extends ArrayAdapter<String> {

    private Context context;
    private ArrayList<String> options;

    public OptionAdapter(Context context, int simple_spinner_item, ArrayList<String> options) {
        super(context, simple_spinner_item, options);
        this.context = context;
        this.options = options;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        textView.setText(options.get(position));
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setTextColor(Color.BLACK);
        tv.setText(options.get(position));
        return v;
    }
}
