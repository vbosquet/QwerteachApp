package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 23/12/16.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    Context context;

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final Message message = getItem(position);
        MessageAdapter.ViewHolder viewHolder;

        if(convertView == null) {

            viewHolder = new MessageAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.message_list_view, parent, false);
            viewHolder.messageContent = (TextView) convertView.findViewById(R.id.message_content);
            viewHolder.messageLinearLayout = (LinearLayout) convertView.findViewById(R.id.message_linear_layout);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.messageContent.setText(message.getBody());

        if (message.isMine()) {
            viewHolder.messageContent.setBackgroundResource(R.drawable.message_out_border);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.messageLinearLayout.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageLinearLayout.setLayoutParams(layoutParams);
        } else {
            viewHolder.messageContent.setBackgroundResource(R.drawable.message_in_border);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.messageLinearLayout.getLayoutParams();
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            viewHolder.messageLinearLayout.setLayoutParams(layoutParams);
        }

        return convertView;
    }


    public static class ViewHolder {
        TextView messageContent;
        LinearLayout messageLinearLayout;
    }
}
