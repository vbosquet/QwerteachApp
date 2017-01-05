package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 3/01/17.
 */

public class ToDoListAdapter extends ArrayAdapter<Lesson> {

    Context context;

    private ILessonManagementButtons callback;

    public ToDoListAdapter(Context context, ArrayList<Lesson> lessons) {
        super(context, 0, lessons);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Lesson lesson = getItem(position);
        ToDoListAdapter.ViewHolder viewHolder;
        String timeStart = lesson.getTimeStart();

        if(convertView == null) {
            viewHolder = new ToDoListAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.to_do_list_list_view, parent, false);
            viewHolder.toDoTitle = (TextView) convertView.findViewById(R.id.to_do_title);
            viewHolder.lessonDetails = (TextView) convertView.findViewById(R.id.lesson_details_text_view);
            viewHolder.lessonsDate = (TextView) convertView.findViewById(R.id.lesson_date_text_view);
            viewHolder.lessonUpdate = (Button) convertView.findViewById(R.id.update_lesson_button);
            viewHolder.lessonAccept = (Button) convertView.findViewById(R.id.accept_lesson_button);
            viewHolder.lessonRefuse = (Button) convertView.findViewById(R.id.refuse_lesson_button);
            viewHolder.positiveFeedBackButton = (Button) convertView.findViewById(R.id.positive_feedback_putton);
            viewHolder.negativeFeedBackButton = (Button) convertView.findViewById(R.id.negative_feedback_button);
            viewHolder.lessonManagementButtons = (LinearLayout) convertView.findViewById(R.id.lesson_management_buttons);
            viewHolder.feedBackButtons = (LinearLayout) convertView.findViewById(R.id.feed_back_buttons);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ToDoListAdapter.ViewHolder) convertView.getTag();
        }

        String lessonTopic = "<font color='#3F51B5'>" + lesson.getTopicTitle() + "</font>";
        String firstName = "<font color='#3F51B5'>" + lesson.getUserFirstName() + "</font>";
        String lastName = "<font color='#3F51B5'>" + lesson.getUserLastName() + "</font>";

        viewHolder.lessonDetails.setText(Html.fromHtml(lesson.getDuration() + " de " + lessonTopic
                + " avec " + firstName + " " + lastName));
        viewHolder.lessonsDate.setText("Le " + lesson.getDate(timeStart)
                + " à " + lesson.getTime(timeStart));


        viewHolder.lessonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.didTouchAcceptLessonButton(lesson.getLessonId());
                }
            }
        });

        viewHolder.lessonRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.didTouchRefuseLessonButton(lesson.getLessonId());
                }

            }
        });

        viewHolder.lessonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.didTouchUpdateLessonButton(lesson);
                }
            }
        });

        viewHolder.positiveFeedBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.didTouchPositiveFeedBackButton(lesson.getLessonId());
            }
        });

        viewHolder.negativeFeedBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.didTouchNegativeFeedBackButton(lesson.getLessonId());
            }
        });


        if (lesson.getStatus().equals("past")) {
            viewHolder.toDoTitle.setText(R.string.asf_for_feed_back_text_view);
            viewHolder.lessonManagementButtons.setVisibility(View.GONE);
            viewHolder.feedBackButtons.setVisibility(View.VISIBLE);

        } else if (lesson.getStatus().equals("pending_student")
                || lesson.getStatus().equals("pending_teacher")) {
            viewHolder.toDoTitle.setText(lesson.getUserFirstName() + " vous a fait une demande de cours");
            viewHolder.lessonManagementButtons.setVisibility(View.VISIBLE);
            viewHolder.feedBackButtons.setVisibility(View.GONE);
        }


        return convertView;
    }

    public static class ViewHolder {
        TextView toDoTitle;
        TextView lessonDetails;
        TextView lessonsDate;
        Button lessonUpdate;
        Button lessonAccept;
        Button lessonRefuse;
        Button positiveFeedBackButton;
        Button negativeFeedBackButton;
        LinearLayout lessonManagementButtons;
        LinearLayout feedBackButtons;
    }

    public void setCallback(ILessonManagementButtons callback) {
        this.callback = callback;
    }

    public interface ILessonManagementButtons {
        void didTouchRefuseLessonButton(int lessonId);
        void didTouchAcceptLessonButton(int lessonId);
        void didTouchUpdateLessonButton(Lesson lesson);
        void didTouchPositiveFeedBackButton(int lessonId);
        void didTouchNegativeFeedBackButton(int lessonId);
    }
}
