package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wivi on 22/12/16.
 */

public class ConversationAdapter extends ArrayAdapter<Conversation> {

    public ConversationAdapter(Context context, ArrayList<Conversation> conversations) {
        super(context, 0, conversations);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Conversation conversation = getItem(position);
        ConversationAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ConversationAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.conversation_list_view, parent, false);
            viewHolder.recipient = (TextView) convertView.findViewById(R.id.recipient);
            viewHolder.body = (TextView) convertView.findViewById(R.id.body);
            viewHolder.creationDate = (TextView) convertView.findViewById(R.id.creation_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ConversationAdapter.ViewHolder) convertView.getTag();
        }

        ArrayList<Message> messages = conversation.getMessages();
        String lastMessage = messages.get(messages.size() - 1).getBody();

        String dateToFormat = messages.get(messages.size() - 1).getCreationDate();
        Date newDate = getDate(dateToFormat);
        String time = getTime(newDate);

        viewHolder.recipient.setText(conversation.getTeacher().getFirstName());
        viewHolder.body.setText(lastMessage);
        viewHolder.creationDate.setText("Il y a " + time);

        return convertView;
    }

    public static class ViewHolder {
        TextView recipient;
        TextView body;
        TextView creationDate;
    }

    public Date getDate(String dateToFormat) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
