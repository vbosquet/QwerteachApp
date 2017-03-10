package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
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

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {

    private Context context;
    private MyLessonsListViewFragment fragment;
    private ArrayList<Lesson> lessons;

    public LessonsAdapter(Context context, ArrayList<Lesson> lessons, MyLessonsListViewFragment fragment) {
        this.lessons = lessons;
        this.context = context;
        this.fragment = fragment;
    }

    private static void displayAcceptLessonButton(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.VISIBLE);
        viewHolder.lessonRefuse.setVisibility(View.VISIBLE);
        viewHolder.lessonUpdate.setVisibility(View.VISIBLE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    private static void removeAllButtons(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    private static void displayCancelButtonOnly(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.VISIBLE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    private static void displayPayTeacherButtons(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.VISIBLE);
        viewHolder.negativeReviewButton.setVisibility(View.VISIBLE);
        viewHolder.reviewButton.setVisibility(View.GONE);
    }

    private static void displayReviewButton(ViewHolder viewHolder) {
        viewHolder.lessonCancel.setVisibility(View.GONE);
        viewHolder.lessonAccept.setVisibility(View.GONE);
        viewHolder.lessonRefuse.setVisibility(View.GONE);
        viewHolder.lessonUpdate.setVisibility(View.GONE);
        viewHolder.positiveReviewButton.setVisibility(View.GONE);
        viewHolder.negativeReviewButton.setVisibility(View.GONE);
        viewHolder.reviewButton.setVisibility(View.VISIBLE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Lesson lesson = lessons.get(position);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userId = preferences.getString("userId", "");
        String timeStart = lesson.getTimeStart();

        holder.lessonTitle.setText("Cours de " + lesson.getTopicTitle() +  " avec " + lesson.getUserName());
        holder.lessonStartTime.setText("Prévu le " + lesson.getDate(timeStart) + " à " + lesson.getTime(timeStart));
        holder.lessonDuration.setText("Durée : " + lesson.getDuration());
        holder.lessonPrice.setText("Prix : " + lesson.getPrice() + " €");

        holder.lessonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchCancelLessonButton(position);
            }
        });

        holder.lessonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchRefuseLessonButton(position);
            }
        });

        holder.lessonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchUpdateLessonButton(lesson);
            }
        });

        holder.lessonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchAcceptLessonButton(position);
            }
        });

        holder.positiveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchPositiveReviewButton(position);
            }
        });

        holder.negativeReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchNegativeReviewButton(position);
            }
        });

        holder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchReviewButton(position);
            }
        });

        if (lesson.getStatus().equals("expired")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_expired_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("refused")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_refused_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("canceled")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_canceled_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.red));
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("accepted")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_accepted_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayCancelButtonOnly(holder);

        } else if (lesson.getStatus().equals("pay")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_past_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayPayTeacherButtons(holder);

        } else if (lesson.getStatus().equals("past&paid")) {
            holder.lessonStatus.setVisibility(View.GONE);
            removeAllButtons(holder);

        } else if (userId.equals(String.valueOf(lesson.getTeacherId()))
                && lesson.getStatus().equals("confirm")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_to_accept);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayAcceptLessonButton(holder);

        } else if (userId.equals(String.valueOf(lesson.getStudentId()))
                && lesson.getStatus().equals("confirm")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_to_accept);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayAcceptLessonButton(holder);

        } else if (lesson.getStatus().equals("review")) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText("Laissez un commentaire à " + lesson.getUserName());
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.green));
            displayReviewButton(holder);

        } else if (lesson.getStatus().equals("disputed") && userId.equals(String.valueOf(lesson.getStudentId()))) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_disputed_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("disputed") && userId.equals(String.valueOf(lesson.getTeacherId()))) {
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setText(R.string.lesson_waiting_for_payment_status);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("past")) {
            holder.lessonStatus.setVisibility(View.GONE);
            removeAllButtons(holder);

        } else if (lesson.getStatus().equals("waiting")){
            holder.lessonStatus.setText(R.string.lesson_to_validate);
            holder.lessonStatus.setVisibility(View.VISIBLE);
            holder.lessonStatus.setTextColor(context.getResources().getColor(R.color.orange));
            displayCancelButtonOnly(holder);

        }

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView lessonTitle, lessonStartTime, lessonDuration, lessonStatus, lessonPrice;
        Button lessonCancel, lessonUpdate, lessonAccept, lessonRefuse, positiveReviewButton, negativeReviewButton, reviewButton;

        public ViewHolder(View itemView) {
            super(itemView);

            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_title_text_view);
            lessonStartTime = (TextView) itemView.findViewById(R.id.lesson_start_time_text_view);
            lessonDuration = (TextView) itemView.findViewById(R.id.lesson_duration_text_view);
            lessonStatus = (TextView) itemView.findViewById(R.id.lesson_status_text_view);
            lessonPrice = (TextView) itemView.findViewById(R.id.lesson_price_text_view);
            lessonCancel = (Button) itemView.findViewById(R.id.cancel_lesson_button);
            lessonUpdate = (Button) itemView.findViewById(R.id.update_lesson_button);
            lessonAccept = (Button) itemView.findViewById(R.id.accept_lesson_button);
            lessonRefuse = (Button) itemView.findViewById(R.id.refuse_lesson_button);
            positiveReviewButton = (Button) itemView.findViewById(R.id.yes_button);
            negativeReviewButton = (Button) itemView.findViewById(R.id.no_button);
            reviewButton = (Button) itemView.findViewById(R.id.review_button);
        }
    }
}
