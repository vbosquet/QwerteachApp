package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

/**
 * Created by wivi on 7/11/16.
 */

public class TopicGroupAdapter extends ArrayAdapter<TopicGroup> {

    private Context context;
    private ArrayList<TopicGroup> topicGroups;

    public TopicGroupAdapter(Context context, int simple_spinner_item, ArrayList<TopicGroup> topicGroups) {
        super(context, simple_spinner_item, topicGroups);
        this.context = context;
        this.topicGroups = topicGroups;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setText(topicGroups.get(position).getTopicGroupTitle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = super.getDropDownView(position, convertView, parent);
        TextView tv = ((TextView) v);
        tv.setText(topicGroups.get(position).getTopicGroupTitle());
        return v;
    }
}

