package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wivi on 23/12/16.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Context context;

    public MessageAdapter(List<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
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

            holder.recipientAvatar.setVisibility(View.VISIBLE);
            holder.senderAvatar.setVisibility(View.GONE);
            Picasso.with(context).load(message.getAvatar()).resize(130, 130).centerCrop().into(holder.recipientAvatar);

        } else {
            holder.messageContent.setBackgroundResource(R.drawable.message_in_border);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.messageLinearLayout.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.messageLinearLayout.setLayoutParams(layoutParams);

            holder.senderAvatar.setVisibility(View.VISIBLE);
            holder.recipientAvatar.setVisibility(View.GONE);
            Picasso.with(context).load(message.getAvatar()).resize(130, 130).centerCrop().into(holder.senderAvatar);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageContent;
        LinearLayout messageLinearLayout;
        ImageView senderAvatar, recipientAvatar;

        public ViewHolder(View itemView) {
            super(itemView);

            messageContent = (TextView) itemView.findViewById(R.id.message_content);
            messageLinearLayout = (LinearLayout) itemView.findViewById(R.id.message_linear_layout);
            senderAvatar = (ImageView) itemView.findViewById(R.id.sender_avatar);
            recipientAvatar = (ImageView) itemView.findViewById(R.id.recipient_avatar);
        }
    }

}
