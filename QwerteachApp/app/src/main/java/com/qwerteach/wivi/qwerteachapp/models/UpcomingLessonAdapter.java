package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wivi on 29/12/16.
 */

public class UpcomingLessonAdapter extends RecyclerView.Adapter<UpcomingLessonAdapter.ViewHolder> {

    private List<Lesson> lessons;
    private LayoutInflater inflater;

    public UpcomingLessonAdapter(List<Lesson> lessons, Context context) {
        this.lessons = lessons;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.upcoming_lesson_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new UpcomingLessonAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Lesson lesson = lessons.get(position);

        String lessonTopic = "<font color='#E166E1'>" + lesson.getTopicTitle() + "</font>";
        String userName = "<font color='#E166E1'>" + lesson.getUserName() + "</font>";

        holder.lessonTopic.setText(Html.fromHtml(lesson.getTime(lesson.getTimeStart()) + " " + lessonTopic));
        holder.lessonTeacher.setText(Html.fromHtml("avec " + userName));
        holder.lessonDuration.setText(lesson.getDuration());
        holder.lessonDay.setText(lesson.getDay(lesson.getTimeStart()));
        holder.lessonMonth.setText(lesson.getMonth(lesson.getTimeStart()));

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lessonTopic, lessonTeacher, lessonDuration, lessonDay, lessonMonth;

        public ViewHolder(View itemView) {
            super(itemView);
            lessonTopic = (TextView) itemView.findViewById(R.id.lesson_topic_text_view);
            lessonTeacher = (TextView) itemView.findViewById(R.id.lesson_teacher_text_view);
            lessonDuration = (TextView) itemView.findViewById(R.id.lesson_duration_text_view);
            lessonDay = (TextView) itemView.findViewById(R.id.lessson_day_text_view);
            lessonMonth = (TextView) itemView.findViewById(R.id.lesson_month_text_view);
        }
    }
}
