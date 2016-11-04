package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAdAdapter extends ArrayAdapter<SmallAd> {

    public SmallAdAdapter(Context context, ArrayList<SmallAd> smallAd) {
        super(context, 0, smallAd);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SmallAd smallAd = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_ad_tab_list_view, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(smallAd.getTitle());

        return  convertView;
    }

    public static class ViewHolder {
        TextView title;
    }
}
