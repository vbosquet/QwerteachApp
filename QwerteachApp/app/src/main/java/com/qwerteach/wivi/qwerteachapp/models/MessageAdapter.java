package com.qwerteach.wivi.qwerteachapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 23/12/16.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new MessageAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.messageContent.setText(message.getBody());

        if (message.getMine()) {
            holder.messageContent.setBackgroundResource(R.drawable.message_out_border);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.messageLinearLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageLinearLayout.setLayoutParams(layoutParams);
        } else {
            holder.messageContent.setBackgroundResource(R.drawable.message_in_border);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.messageLinearLayout.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageLinearLayout.setLayoutParams(layoutParams);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageContent;
        LinearLayout messageLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            messageContent = (TextView) itemView.findViewById(R.id.message_content);
            messageLinearLayout = (LinearLayout) itemView.findViewById(R.id.message_linear_layout);
        }
    }
}
