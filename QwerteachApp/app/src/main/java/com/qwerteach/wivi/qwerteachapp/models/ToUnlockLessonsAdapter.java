package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.DashboardFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by wivi on 5/06/17.
 */

public class ToUnlockLessonsAdapter extends RecyclerView.Adapter<ToUnlockLessonsAdapter.ViewHolder> {

    private List<Lesson> lessons;
    private Context context;
    private DashboardFragment fragment;

    public ToUnlockLessonsAdapter(List<Lesson> lessons, Context context, DashboardFragment fragment) {
        this.lessons = lessons;
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ToUnlockLessonsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Lesson lesson = lessons.get(position);
        String userName = "<font color='#22de80'>" + lesson.getUserName() + "</font>";
        String topicTitle = "<font color='#22de80'>" + lesson.getTopicTitle() + "</font>";

        Picasso.with(context).load(lesson.getAvatar()).resize(150, 150).centerCrop().into(holder.userAvatar);
        holder.lessonTopic.setText("Le " + lesson.getDate() + " à " + lesson.getTime());
        holder.lessonDate.setText(Html.fromHtml("Comment s'est passé votre cours de " + topicTitle + " avec " + userName + " ?"));
        holder.lessonStatus.setVisibility(View.GONE);
        holder.toUnlockButtons.setVisibility(View.VISIBLE);

        holder.payTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchPayTeacherButton(lesson.getLessonId(), lesson.getTeacherId());
            }
        });

        holder.lockPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchLockPayment(lesson.getLessonId());
            }
        });

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
        LinearLayout toUnlockButtons;
        Button payTeacherButton, lockPaymentButton;

        public ViewHolder(View itemView) {
            super(itemView);

            userAvatar = (ImageView) itemView.findViewById(R.id.user_avatar);
            lessonDate = (TextView) itemView.findViewById(R.id.lesson_date);
            lessonTopic = (TextView) itemView.findViewById(R.id.lesson_topic);
            lessonStatus = (TextView) itemView.findViewById(R.id.lessons_status);
            toUnlockButtons = (LinearLayout) itemView.findViewById(R.id.to_unlock_lessons_buttons_management);
            payTeacherButton = (Button) itemView.findViewById(R.id.pay_teacher_button);
            lockPaymentButton = (Button) itemView.findViewById(R.id.lock_payment_button);

        }
    }
}
