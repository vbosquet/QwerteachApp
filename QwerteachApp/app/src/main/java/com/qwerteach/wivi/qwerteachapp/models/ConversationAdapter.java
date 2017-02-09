package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by wivi on 22/12/16.
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private List<Conversation> conversations;
    private Callback callback;
    private Context context;

    public ConversationAdapter(Context context, List<Conversation> conversations, Callback callback) {
        this.conversations = conversations;
        this.callback = callback;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ConversationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Conversation conversation = conversations.get(position);

        List<Message> messages = conversation.getMessages();
        String lastMessage = messages.get(messages.size() - 1).getBody();

        String dateToFormat = messages.get(messages.size() - 1).getCreationDate();
        Date newDate = getDate(dateToFormat);
        String time = getTime(newDate);

        holder.recipient.setText(conversation.getUser().getFirstName());
        holder.body.setText(lastMessage);
        holder.creationDate.setText("Il y a " + time);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.didTouchConversation(position);
            }
        });

        Picasso.with(context).load(conversation.getUser().getAvatarUrl()).resize(150, 150).centerCrop().into(holder.avatar);

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipient, body, creationDate;
        ImageView avatar;

        public ViewHolder(View itemView) {
            super(itemView);

            recipient = (TextView) itemView.findViewById(R.id.recipient);
            body = (TextView) itemView.findViewById(R.id.body);
            creationDate = (TextView) itemView.findViewById(R.id.creation_date);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
        }
    }

    public static Date getDate(String dateToFormat) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = null;
        try {
            date = format.parse(dateToFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;

    }

    public static String getTime(Date oldDate) {
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

        return days + " jour(s)";
    }

    public interface Callback {
        void didTouchConversation(int index);
    }
}
