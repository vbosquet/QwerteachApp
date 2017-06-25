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

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.HistoryLessonsTabFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by wivi on 8/12/16.
 */

public class HistoryLessonsAdapter extends RecyclerView.Adapter<HistoryLessonsAdapter.ViewHolder> {

    private List<Lesson> lessons;
    private Context context;
    private HistoryLessonsTabFragment fragment;

    public HistoryLessonsAdapter(List<Lesson> lessons, Context context, HistoryLessonsTabFragment fragment) {
        this.lessons = lessons;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new HistoryLessonsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Lesson lesson = lessons.get(position);
        String userName = "<font color='#22de80'>" + lesson.getUserName() + "</font>";

        Picasso.with(context).load(lesson.getAvatar()).resize(150, 150).centerCrop().into(holder.userAvatar);
        holder.lessonTopic.setText(Html.fromHtml("Cours de " + lesson.getTopicTitle() + " avec " + userName));
        holder.lessonDate.setText("Le " + lesson.getDate() + " à " + lesson.getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.seeLessonDetails(position);
            }
        });

        switch (lesson.getStatus()) {
            case "expired":
                holder.lessonStatus.setText("Expiré");
                break;
            case "canceled":
                holder.lessonStatus.setText("Annulé");
                break;
            case "refused":
                holder.lessonStatus.setText("Refusé");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return lessons.size();
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
        TextView lessonDate, lessonTopic, lessonStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            lessonDate = (TextView) itemView.findViewById(R.id.lesson_date);
            lessonTopic = (TextView) itemView.findViewById(R.id.lesson_topic);
            lessonStatus = (TextView) itemView.findViewById(R.id.lessons_status);

        }
    }
}
