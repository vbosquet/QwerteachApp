package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.MyNotificationsActivity;
import com.qwerteach.wivi.qwerteachapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * Created by wivi on 18/06/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    List<Notification> notifications;
    Context context;

    public NotificationAdapter(List<Notification> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new NotificationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.ViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        holder.notificationSubject.setText(Html.fromHtml(notification.getSubject()), TextView.BufferType.SPANNABLE);
        holder.notificationDate.setText("Le " + notification.getDate() + " à " + notification.getTime());
        Picasso.with(context).load(notification.getAvatar()).resize(150, 150).centerCrop().into(holder.userAvatar);

        if (notification.getNotification_type() != null) {
            if (Objects.equals(notification.getNotification_type(), "Lesson")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((MyNotificationsActivity)context).seeLessonDetails();
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return notifications.size();
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

        ImageView userAvatar;
        TextView notificationSubject, notificationDate;

        public ViewHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            notificationSubject = (TextView) itemView.findViewById(R.id.notification_subject);
            notificationDate = (TextView) itemView.findViewById(R.id.notification_date);

        }
    }
}
