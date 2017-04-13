package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.common.Common;
import com.qwerteach.wivi.qwerteachapp.fragments.MyLessonsListViewFragment;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by wivi on 8/12/16.
 */

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {

    private Context context;
    private MyLessonsListViewFragment fragment;
    private ArrayList<Lesson> lessons;
    private boolean isClicked = true;

    public LessonsAdapter(Context context, ArrayList<Lesson> lessons, MyLessonsListViewFragment fragment) {
        this.lessons = lessons;
        this.context = context;
        this.fragment = fragment;
        setHasStableIds(true);
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Lesson lesson = lessons.get(position);
        final String timeStart = lesson.getTimeStart();
        final List<Payment> payments = lesson.getPayments();
        Date oldDate = Common.getDate(timeStart);
        PrettyTime p = new PrettyTime(new Locale("fr"));
        String timeAgo = "<font color='#501dd4'>" + p.format(oldDate) + "</font>";
        String lessonTopic = "<font color='#501dd4'>" + lesson.getTopicTitle() + "</font>";
        String userName = "<font color='#501dd4'>" + lesson.getUserName() + "</font>";

        holder.lessonTitle.setText(Html.fromHtml(lesson.getDuration() + " de " + lessonTopic +  " avec " + userName));
        holder.lessonStartTime.setText(Html.fromHtml("Prévu " + timeAgo +  " (le "+ lesson.getDate(timeStart) + " à " + lesson.getTime(timeStart) + ")"));
        Picasso.with(context).load(lesson.getAvatar()).resize(150, 150).centerCrop().into(holder.lessonOtherAvatar);

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

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked) {
                    isClicked = false;
                    holder.detailsLinearLayout.setVisibility(View.VISIBLE);
                    for (int i = 0; i < payments.size(); i++) {
                        TextView id = new TextView(context);
                        TextView price = new TextView(context);
                        TextView status = new TextView(context);
                        id.setText(String.valueOf(payments.get(i).getPaymentId()));
                        id.setTextColor(context.getResources().getColor(R.color.text_grey));
                        price.setText(String.valueOf(payments.get(i).getPrice()));
                        price.setTextColor(context.getResources().getColor(R.color.text_grey));
                        status.setText(payments.get(i).getStatus());
                        status.setTextColor(context.getResources().getColor(R.color.text_grey));
                        holder.idLinearLayout.addView(id);
                        holder.priceLinearLayout.addView(price);
                        holder.statusLinearLayout.addView(status);
                    }
                } else {
                    isClicked = true;
                    holder.detailsLinearLayout.setVisibility(View.GONE);
                    holder.idLinearLayout.removeAllViews();
                    holder.priceLinearLayout.removeAllViews();
                    holder.statusLinearLayout.removeAllViews();
                }
            }
        });

        switch (lesson.getStatus()) {
            case "expired":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_expired_status);
                removeAllButtons(holder);

                break;
            case "refused":
                holder.lessonTitle.setText(lesson.getDuration() + " de " + lesson.getTopicTitle() +  " avec " + lesson.getUserName());
                holder.lessonTitle.setPaintFlags(holder.lessonTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.lessonStartTime.setText("Prévu " +  p.format(oldDate) +  " (le "+ lesson.getDate(timeStart) + " à " + lesson.getTime(timeStart) + ")");
                holder.lessonStartTime.setPaintFlags(holder.lessonStartTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_refused_status);
                holder.detailsButton.setTextColor(context.getResources().getColor(R.color.text_grey));
                removeAllButtons(holder);

                break;
            case "canceled":
                holder.lessonTitle.setText(lesson.getDuration() + " de " + lesson.getTopicTitle() +  " avec " + lesson.getUserName());
                holder.lessonTitle.setPaintFlags(holder.lessonTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.lessonStartTime.setText("Prévu " +  p.format(oldDate) +  " (le "+ lesson.getDate(timeStart) + " à " + lesson.getTime(timeStart) + ")");
                holder.lessonStartTime.setPaintFlags(holder.lessonStartTime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_canceled_status);
                holder.detailsButton.setTextColor(context.getResources().getColor(R.color.text_grey));
                removeAllButtons(holder);

                break;
            case "accepted":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_accepted_status);
                displayCancelButtonOnly(holder);

                break;
            case "pay":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_past_status);
                displayPayTeacherButtons(holder);

                break;
            case "past&paid":
                holder.lessonStatus.setVisibility(View.GONE);
                removeAllButtons(holder);

                break;
            case "confirm":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_to_accept);
                displayAcceptLessonButton(holder);

                break;
            case "review":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText("Laissez un commentaire à " + lesson.getUserName());
                displayReviewButton(holder);

                break;
            case "disputed":
                holder.lessonStatus.setVisibility(View.VISIBLE);
                holder.lessonStatus.setText(R.string.lesson_disputed_status);
                removeAllButtons(holder);

                break;
            case "past":
                holder.lessonStatus.setVisibility(View.GONE);
                removeAllButtons(holder);

                break;
            case "waiting":
                holder.lessonStatus.setText(R.string.lesson_to_validate);
                holder.lessonStatus.setVisibility(View.VISIBLE);
                displayCancelButtonOnly(holder);
                break;
        }

        if (lesson.getTopicGroupId() == 3) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.orange_list_view_item));
            }
            Picasso.with(context).load(R.drawable.lettres_small).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 1) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.yellow_list_view_item));
            }
            Picasso.with(context).load(R.drawable.maths_small_white).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 2) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.pink_list_view_item));
            }
            Picasso.with(context).load(R.drawable.sciences_small).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 4) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.purple_light_list_view_item));
            }
            Picasso.with(context).load(R.drawable.langues_small).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 5) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.green_list_view_item));
            }
            Picasso.with(context).load(R.drawable.economie_small).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 6) {
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.blue_list_view_item));
            }
            Picasso.with(context).load(R.drawable.informatique_small).resize(150, 150).centerCrop().into(holder.topicGroupIcon);
        } else if (lesson.getTopicGroupId() == 7 || lesson.getTopicGroupId() == 8 ){
            if (lesson.getStatus().equals("refused") || lesson.getStatus().equals("canceled")) {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.grey_list_view_item));
            } else {
                holder.topicGroupIcon.setBackground(context.getResources().getDrawable(R.drawable.red_list_view_item));
            }
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
        TextView lessonTitle, lessonStatus, lessonStartTime;
        Button lessonCancel, lessonUpdate, lessonAccept, lessonRefuse, positiveReviewButton, negativeReviewButton, reviewButton, detailsButton;
        ImageView lessonOtherAvatar, topicGroupIcon;
        LinearLayout detailsLinearLayout, idLinearLayout, priceLinearLayout, statusLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            lessonTitle = (TextView) itemView.findViewById(R.id.lesson_title_text_view);
            lessonStartTime = (TextView) itemView.findViewById(R.id.lesson_start_time_text_view);
            lessonStatus = (TextView) itemView.findViewById(R.id.lesson_status_text_view);
            lessonCancel = (Button) itemView.findViewById(R.id.cancel_lesson_button);
            lessonUpdate = (Button) itemView.findViewById(R.id.update_lesson_button);
            lessonAccept = (Button) itemView.findViewById(R.id.accept_lesson_button);
            lessonRefuse = (Button) itemView.findViewById(R.id.refuse_lesson_button);
            positiveReviewButton = (Button) itemView.findViewById(R.id.yes_button);
            negativeReviewButton = (Button) itemView.findViewById(R.id.no_button);
            reviewButton = (Button) itemView.findViewById(R.id.review_button);
            lessonOtherAvatar = (ImageView) itemView.findViewById(R.id.lesson_other_avatar);
            topicGroupIcon = (ImageView) itemView.findViewById(R.id.topic_group_icon);
            detailsButton = (Button) itemView.findViewById(R.id.details_button);
            detailsLinearLayout = (LinearLayout) itemView.findViewById(R.id.details_linear_layout);
            idLinearLayout = (LinearLayout) itemView.findViewById(R.id.id_linear_layout);
            priceLinearLayout = (LinearLayout) itemView.findViewById(R.id.price_linear_layout);
            statusLinearLayout = (LinearLayout) itemView.findViewById(R.id.status_linear_layout);
        }
    }
}
