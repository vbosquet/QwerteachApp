package com.qwerteach.wivi.qwerteachapp.models;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.DashboardFragment;

import java.util.List;

/**
 * Created by wivi on 3/01/17.
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ViewHolder> {

    private DashboardFragment fragment;
    private List<Lesson> lessons;

    public ToDoListAdapter(List<Lesson> lessons, DashboardFragment fragment) {
        this.lessons = lessons;
        this.fragment = fragment;
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.to_do_list_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ToDoListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Lesson lesson = lessons.get(position);
        String timeStart = lesson.getTimeStart();

        String lessonTopic = "<font color='#3F51B5'>" + lesson.getTopicTitle() + "</font>";
        String userName = "<font color='#3F51B5'>" + lesson.getUserName() + "</font>";

        holder.lessonDetails.setText(Html.fromHtml(lesson.getDuration() + " de " + lessonTopic + " avec " + userName));
        holder.lessonsDate.setText("Le " + lesson.getDate(timeStart)
                + " à " + lesson.getTime(timeStart));


        /*holder.lessonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchAcceptLessonButton(position);
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

        holder.positiveFeedBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchPositiveFeedBackButton(position);
            }
        });

        holder.negativeFeedBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchNegativeFeedBackButton(position);
            }
        });*/


        if (lesson.getStatus().equals("pay")) {
            holder.toDoTitle.setText(R.string.asf_for_feed_back_text_view);
            holder.lessonManagementButtons.setVisibility(View.GONE);
            holder.feedBackButtons.setVisibility(View.VISIBLE);

        } else if (lesson.getStatus().equals("confirm")) {
            holder.toDoTitle.setText(lesson.getUserName() + " vous a fait une demande de cours");
            holder.lessonManagementButtons.setVisibility(View.VISIBLE);
            holder.feedBackButtons.setVisibility(View.GONE);
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

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView toDoTitle, lessonDetails, lessonsDate;
        Button lessonUpdate, lessonAccept, lessonRefuse, positiveFeedBackButton, negativeFeedBackButton;
        LinearLayout lessonManagementButtons, feedBackButtons;

        public ViewHolder(View itemView) {
            super(itemView);

            toDoTitle = (TextView) itemView.findViewById(R.id.to_do_title);
            lessonDetails = (TextView) itemView.findViewById(R.id.lesson_details_text_view);
            lessonsDate = (TextView) itemView.findViewById(R.id.lesson_date_text_view);
            lessonUpdate = (Button) itemView.findViewById(R.id.update_lesson_button);
            lessonAccept = (Button) itemView.findViewById(R.id.accept_lesson_button);
            lessonRefuse = (Button) itemView.findViewById(R.id.refuse_lesson_button);
            positiveFeedBackButton = (Button) itemView.findViewById(R.id.positive_feedback_putton);
            negativeFeedBackButton = (Button) itemView.findViewById(R.id.negative_feedback_button);
            lessonManagementButtons = (LinearLayout) itemView.findViewById(R.id.lesson_management_buttons);
            feedBackButtons = (LinearLayout) itemView.findViewById(R.id.feed_back_buttons);
        }
    }
}
