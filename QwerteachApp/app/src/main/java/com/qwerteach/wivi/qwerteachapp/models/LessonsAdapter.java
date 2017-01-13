package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.MyLessonsListViewFragment;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by wivi on 8/12/16.
 */

public class LessonsAdapter extends ArrayAdapter<Lesson> {

    Context context;
    MyLessonsListViewFragment fragment;

    public LessonsAdapter(Context context, ArrayList<Lesson> lessons, MyLessonsListViewFragment fragment) {
        super(context, 0, lessons);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Lesson lesson = getItem(position);
        LessonsAdapter.ViewHolder viewHolder;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = preferences.getString("userId", "");
        String timeStart = lesson.getTimeStart();

        if(convertView == null) {
            viewHolder = new LessonsAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.lesson_list_view, parent, false);

            viewHolder.lessonTitle = (TextView) convertView.findViewById(R.id.lesson_title_text_view);
            viewHolder.lessonStartTime = (TextView) convertView.findViewById(R.id.lesson_start_time_text_view);
            viewHolder.lessonDuration = (TextView) convertView.findViewById(R.id.lesson_duration_text_view);
            viewHolder.lessonStatus = (TextView) convertView.findViewById(R.id.lesson_status_text_view);
            viewHolder.lessonPrice = (TextView) convertView.findViewById(R.id.lesson_price_text_view);
            viewHolder.lessonCancel = (Button) convertView.findViewById(R.id.cancel_lesson_button);
            viewHolder.lessonUpdate = (Button) convertView.findViewById(R.id.update_lesson_button);
            viewHolder.lessonAccept = (Button) convertView.findViewById(R.id.accept_lesson_button);
            viewHolder.lessonRefuse = (Button) convertView.findViewById(R.id.refuse_lesson_button);
            viewHolder.positiveReviewButton = (Button) convertView.findViewById(R.id.yes_button);
            viewHolder.negativeReviewButton = (Button) convertView.findViewById(R.id.no_button);
            viewHolder.reviewButton = (Button) convertView.findViewById(R.id.review_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LessonsAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.lessonTitle.setText("Cours de " + lesson.getTopicTitle() +  " avec " +
                lesson.getUserFirstName() + " " + lesson.getUserLastName());
        viewHolder.lessonStartTime.setText("Prévu le " + lesson.getDate(timeStart) + " à " + lesson.getTime(timeStart));
        viewHolder.lessonDuration.setText("Durée : " + lesson.getDuration());
        viewHolder.lessonPrice.setText("Prix : " + lesson.getPrice() + " €");

        viewHolder.lessonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchCancelLessonButton(lesson.getLessonId());
            }
        });

        viewHolder.lessonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchRefuseLessonButton(lesson.getLessonId());
            }
        });

        viewHolder.lessonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchUpdateLessonButton(lesson);
            }
        });

        viewHolder.lessonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchAcceptLessonButton(lesson.getLessonId());
            }
        });

        viewHolder.positiveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchPositiveReviewButton(lesson.getLessonId());
            }
        });

        viewHolder.negativeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchNegativeReviewButton(lesson.getLessonId());
            }
        });

        viewHolder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchReviewButton(lesson.getTeacherId());
            }
        });

        if (lesson.getStatus().equals("expired")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_expired_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(viewHolder);

        } else if (lesson.getStatus().equals("refused")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_refused_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(viewHolder);

        } else if (lesson.getStatus().equals("canceled")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_canceled_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(viewHolder);

        } else if (lesson.getStatus().equals("created")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_accepted_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayCancelButtonOnly(viewHolder);

        } else if (lesson.getStatus().equals("past") && !lesson.getPaymentStatus().equals("paid")
                && !lesson.getPaymentStatus().equals("disputed")
                && userId.equals(String.valueOf(lesson.getStudentId()))) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_past_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayPayTeacherButtons(viewHolder);

        } else if (lesson.getStatus().equals("past") && lesson.getPaymentStatus().equals("locked")
                && userId.equals(String.valueOf(lesson.getTeacherId()))) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_waiting_for_payment_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAllButtons(viewHolder);

        } else if (userId.equals(String.valueOf(lesson.getTeacherId()))
                && lesson.getStatus().equals("pending_teacher")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_to_accept);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayAcceptLessonButton(viewHolder);

        } else if (userId.equals(String.valueOf(lesson.getStudentId()))
                && lesson.getStatus().equals("pending_student")) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_to_accept);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayAcceptLessonButton(viewHolder);

        } else if (lesson.getPaymentStatus().equals("paid") && lesson.isReviewNeeded()) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText("Laissez un commentaire à "
                    + lesson.getUserFirstName() + " "+ lesson.getUserLastName());
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayReviewButton(viewHolder);

        } else if (lesson.getPaymentStatus().equals("disputed") && userId.equals(String.valueOf(lesson.getStudentId()))) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_disputed_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAllButtons(viewHolder);

        } else if (lesson.getPaymentStatus().equals("disputed") && userId.equals(String.valueOf(lesson.getTeacherId()))) {
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setText(R.string.lesson_waiting_for_payment_status);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAllButtons(viewHolder);

        } else if (lesson.getPaymentStatus().equals("paid") && !lesson.isReviewNeeded() && lesson.getStatus().equals("past")) {
            viewHolder.lessonStatus.setVisibility(View.GONE);
            removeAllButtons(viewHolder);

        } else {
            viewHolder.lessonStatus.setText(R.string.lesson_to_validate);
            viewHolder.lessonStatus.setVisibility(View.VISIBLE);
            viewHolder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAcceptLessonButton(viewHolder);

        }

        return convertView;
    }

    public static class ViewHolder {
        TextView lessonTitle;
        TextView lessonStartTime;
        TextView lessonDuration;
        TextView lessonStatus;
        TextView lessonPrice;
        Button lessonCancel;
        Button lessonUpdate;
        Button lessonAccept;
        Button lessonRefuse;
        Button positiveReviewButton;
        Button negativeReviewButton;
        Button reviewButton;
    }

    public static void displayAcceptLessonButton(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.VISIBLE);
        viewHolder.lessonRefuse.setVisibility(View.VISIBLE);
        viewHolder.lessonUpdate.setVisibility(View.VISIBLE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    public static void removeAllButtons(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    public static void removeAcceptLessonButton(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.VISIBLE);
        viewHolder.lessonUpdate.setVisibility(View.VISIBLE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    public static void displayCancelButtonOnly(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.VISIBLE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    public static void displayPayTeacherButtons(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.VISIBLE);
        viewHolder.negativeReviewButton.setVisibility(View.VISIBLE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    public static void displayReviewButton(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.VISIBLE);
    }
}