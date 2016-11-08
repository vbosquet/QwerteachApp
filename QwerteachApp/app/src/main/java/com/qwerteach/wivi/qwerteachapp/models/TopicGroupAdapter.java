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

public class TopicGroupAdapter extends ArrayAdapter<TopicGroup> {

    Context context;
    ArrayList<TopicGroup> topicGroups;

    public TopicGroupAdapter(Context context, int simple_spinner_item, ArrayList<TopicGroup> topicGroups) {
        super(context, simple_spinner_item, topicGroups);
        this.context = context;
        this.topicGroups = topicGroups;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setTextColor(Color.BLACK);
        textView.setText(topicGroups.get(position).getTopicGroupTitle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setTextColor(Color.BLACK);
        tv.setText(topicGroups.get(position).getTopicGroupTitle());
        return v;
    }
}
