package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wivi on 23/12/16.
 */

public class MessageAdapter extends ArrayAdapter<Message> {

    public MessageAdapter(Context context, ArrayList<Message> messages) {
        super(context, 0, messages);
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
            viewHolder.messageDate = (TextView) convertView.findViewById(R.id.message_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (MessageAdapter.ViewHolder) convertView.getTag();
        }

        Date newDate = getDate(message.getCreationDate());
        String time = getTime(newDate);

        viewHolder.messageContent.setText(message.getBody());
        viewHolder.messageDate.setText("Il y a " + time);

        return convertView;
    }

    public static class ViewHolder {
        TextView messageContent;
        TextView messageDate;
    }

    public Date getDate(String dateToFormat) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            date = format.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;

    }

    public String getTime(Date oldDate) {
        Date currentDate = new Date();

        long diff = currentDate.getTime() - oldDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (minutes < 60) {
            return minutes + " minute(s)";
        } else if(hours < 24) {
            return hours + " heure(s)";
        }

        return days + "jour(s)";
    }
}
