package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 29/12/16.
 */

public class UpcomingLessonAdapter extends ArrayAdapter<Lesson> {

    public UpcomingLessonAdapter(Context context, ArrayList<Lesson> lessons) {
        super(context, 0, lessons);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Lesson lesson = getItem(position);
        UpcomingLessonAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new UpcomingLessonAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.upcoming_lesson_list_view, parent, false);
            viewHolder.lessonTopic = (TextView) convertView.findViewById(R.id.lesson_topic_text_view);
            viewHolder.lessonTeacher = (TextView) convertView.findViewById(R.id.lesson_teacher_text_view);
            viewHolder.lessonDuration = (TextView) convertView.findViewById(R.id.lesson_duration_text_view);
            viewHolder.lessonDay = (TextView) convertView.findViewById(R.id.lessson_day_text_view);
            viewHolder.lessonMonth = (TextView) convertView.findViewById(R.id.lesson_month_text_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (UpcomingLessonAdapter.ViewHolder) convertView.getTag();
        }

        String lessonTopic = "<font color='#E166E1'>" + lesson.getTopicTitle() + "</font>";
        String userFirstName = "<font color='#E166E1'>" + lesson.getUserFirstName() + "</font>";

        viewHolder.lessonTopic.setText(Html.fromHtml(lesson.getTime(lesson.getTimeStart()) + " " + lessonTopic));
        viewHolder.lessonTeacher.setText(Html.fromHtml("avec " + userFirstName));
        viewHolder.lessonDuration.setText(lesson.getDuration());
        viewHolder.lessonDay.setText(lesson.getDay(lesson.getTimeStart()));
        viewHolder.lessonMonth.setText(lesson.getMonth(lesson.getTimeStart()));

        return convertView;
    }

    public static class ViewHolder {
        TextView lessonTopic;
        TextView lessonTeacher;
        TextView lessonDuration;
        TextView lessonDay;
        TextView lessonMonth;
    }
}
