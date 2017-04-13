package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.UpdateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.fragments.AdTabFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by wivi on 4/11/16.
 */

public class SmallAdAdapter extends RecyclerView.Adapter<SmallAdAdapter.ViewHolder> {

    private AdTabFragment fragment;
    private ArrayList<SmallAd> smallAds;
    private Context context;

    public SmallAdAdapter(Context context, ArrayList<SmallAd> smallAds, AdTabFragment fragment) {
        this.fragment = fragment;
        this.smallAds = smallAds;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_ad_tab_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new SmallAdAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SmallAd smallAd = smallAds.get(position);

        if (smallAd.getTopicGroupId() == 3) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.orange_list_view_item));
            Picasso.with(context).load(R.drawable.lettres_small).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 1) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.yellow_list_view_item));
            Picasso.with(context).load(R.drawable.maths_small_white).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 2) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.pink_list_view_item));
            Picasso.with(context).load(R.drawable.sciences_small).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 4) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.purple_light_list_view_item));
            Picasso.with(context).load(R.drawable.langues_small).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 5) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.green_list_view_item));
            Picasso.with(context).load(R.drawable.economie_small).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 6) {
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.blue_list_view_item));
            Picasso.with(context).load(R.drawable.informatique_small).into(holder.topicGroupIcon);
        } else if (smallAd.getTopicGroupId() == 7 || smallAd.getTopicGroupId() == 8 ){
            holder.smallAdLinearLayout.setBackground(context.getResources().getDrawable(R.drawable.red_list_view_item));
        }

        holder.title.setText(smallAd.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchOnEditButton(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return smallAds.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        LinearLayout smallAdLinearLayout;
        ImageView topicGroupIcon;

        public ViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            smallAdLinearLayout = (LinearLayout) itemView.findViewById(R.id.small_ad);
            topicGroupIcon = (ImageView) itemView.findViewById(R.id.topic_group_icon);
        }
    }
}
