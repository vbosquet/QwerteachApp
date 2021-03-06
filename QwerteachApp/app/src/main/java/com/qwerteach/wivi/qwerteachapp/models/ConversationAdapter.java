package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.common.Common;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Conversation conversation = conversations.get(position);

        String lastMessage = conversation.getLastMessage().getBody();
        String dateToFormat = conversation.getLastMessage().getCreationDate();
        Date oldDate = Common.getDate(dateToFormat);

        holder.body.setText(lastMessage);
        holder.creationDate.setReferenceTime(oldDate.getTime());
        if (conversation.getUser() != null) {
            holder.recipient.setText(conversation.getUser().getFirstName());
            Picasso.with(context).load(conversation.getUser().getAvatarUrl())
                    .resize(150, 150).centerCrop().into(holder.avatar);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.didTouchConversation(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return conversations.size();
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
        TextView recipient, body;
        ImageView avatar;
        RelativeTimeTextView creationDate;

        public ViewHolder(View itemView) {
            super(itemView);

            recipient = (TextView) itemView.findViewById(R.id.recipient);
            body = (TextView) itemView.findViewById(R.id.body);
            creationDate = (RelativeTimeTextView) itemView.findViewById(R.id.creation_date);
            avatar = (ImageView) itemView.findViewById(R.id.user_avatar);
        }
    }

    public interface Callback {
        void didTouchConversation(int index);
    }
}
