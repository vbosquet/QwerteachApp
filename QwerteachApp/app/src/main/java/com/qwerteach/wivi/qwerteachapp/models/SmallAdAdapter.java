package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.AdTabFragment;

import java.util.ArrayList;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAdAdapter extends ArrayAdapter<SmallAd> {

    private AdTabFragment fragment;

    public SmallAdAdapter(Context context, ArrayList<SmallAd> smallAd, AdTabFragment fragment) {
        super(context, 0, smallAd);
        this.fragment = fragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final SmallAd smallAd = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_ad_tab_list_view, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.delete = (Button) convertView.findViewById(R.id.delete_small_ad_button);
            viewHolder.edit = (Button) convertView.findViewById(R.id.edit_small_ad_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText("Cours de " + smallAd.getTitle());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchOnDeleteButton(position);
            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchOnEditButton(position);
            }
        });

        return  convertView;
    }

    public static class ViewHolder {
        TextView title;
        Button delete;
        Button edit;
    }
}
