package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 8/12/16.
 */

public class LessonsAdapter extends ArrayAdapter<Lesson> {

    Context context;

    public LessonsAdapter(Context context, ArrayList<Lesson> lessons) {
        super(context, 0, lessons);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Lesson lesson = getItem(position);
        LessonsAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new LessonsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lesson_list_view, parent, false);
            viewHolder.lessonTitle = (TextView) convertView.findViewById(R.id.lesson_title_text_view);
            viewHolder.lessonStartTime = (TextView) convertView.findViewById(R.id.lesson_start_time_text_view);
            viewHolder.lessonDuration = (TextView) convertView.findViewById(R.id.lesson_duration_text_view);
            viewHolder.lessonExpiredStatus = (TextView) convertView.findViewById(R.id.lesson_expired_status_text_view);
            viewHolder.lessonPrice = (TextView) convertView.findViewById(R.id.lesson_price_text_view);
            viewHolder.lessonCancel = (Button) convertView.findViewById(R.id.cancel_lesson_button);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LessonsAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.lessonTitle.setText("Cours de " + lesson.getTopicTitle() +  " avec " +
                lesson.getTeacherFirstName() + " " + lesson.getTeacherLastName());

        viewHolder.lessonStartTime.setText("Prévu le " + lesson.getTimeStart());
        viewHolder.lessonDuration.setText("Durée : " + lesson.getDuration());
        viewHolder.lessonPrice.setText("Prix : " + lesson.getPrice() + " €");

        if (lesson.isExpired()) {
            viewHolder.lessonExpiredStatus.setText(R.string.lesson_expired_status);
            viewHolder.lessonExpiredStatus.setTextColor(Color.RED);
            viewHolder.lessonCancel.setVisibility(View.GONE);

        } else if (lesson.isCanceled()) {
            viewHolder.lessonExpiredStatus.setText(R.string.lesson_canceled_status);
            viewHolder.lessonExpiredStatus.setTextColor(Color.RED);
            viewHolder.lessonCancel.setVisibility(View.GONE);

        } else {
            viewHolder.lessonExpiredStatus.setText(R.string.lesson_to_validate);
            viewHolder.lessonExpiredStatus.setTextColor(context.getResources().getColor(R.color.green));
            viewHolder.lessonCancel.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public static class ViewHolder {
        TextView lessonTitle;
        TextView lessonStartTime;
        TextView lessonDuration;
        TextView lessonExpiredStatus;
        TextView lessonPrice;
        Button lessonCancel;
    }
}
