package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wivi on 7/11/16.
 */

public class TopicAdapter extends ArrayAdapter<Topic> {
    Context context;
    ArrayList<Topic> topics;

    public TopicAdapter(Context context, int simple_spinner_item, ArrayList<Topic> topics) {
        super(context, simple_spinner_item, topics);
        this.context = context;
        this.topics = topics;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setText(topics.get(position).getTopicTitle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setTextColor(Color.BLACK);
        tv.setText(topics.get(position).getTopicTitle());
        return v;
    }
}
