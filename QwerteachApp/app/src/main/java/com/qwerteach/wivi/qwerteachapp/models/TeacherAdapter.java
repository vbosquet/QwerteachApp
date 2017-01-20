package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by wivi on 17/11/16.
 */

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private ArrayList<Teacher> teachers;
    private MyClickListener listener;
    private Context context;

    public TeacherAdapter(ArrayList<Teacher> teachers, MyClickListener listener, Context context) {
        this.teachers = teachers;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_recycler_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Teacher teacher = teachers.get(position);

        holder.teacherName.setText(teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName());
        String text = teacher.getUser().getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("<p>", "");
        text= text.replace("</p>", "<br>");
        holder.description.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        holder.materialCourseNames.setText(teacher.getTopics());
        holder.minPrice.setText(teacher.getMinPrice() + " €/h");
        holder.ratingBar.setRating(teacher.getRating());
        holder.numberOfReviews.setText(teacher.getNumberOfReviews() + " commentaire(s)");
        holder.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClicked(position);
            }
        });

        Picasso.with(context)
                .load(teacher.getUser().getAvatarUrl())
                .resize(holder.teacherAvatar.getWidth(), 1000)
                .into(holder.teacherAvatar);

    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public interface MyClickListener {
        void onClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView teacherName, description, materialCourseNames, minPrice, numberOfReviews, readMore;
        RatingBar ratingBar;
        ImageView teacherAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            teacherName = (TextView) itemView.findViewById(R.id.teacher_name_text_view);
            description = (TextView) itemView.findViewById(R.id.teach_description_text_view);
            materialCourseNames = (TextView) itemView.findViewById(R.id.teacher_course_material_names_text_view);
            minPrice = (TextView) itemView.findViewById(R.id.teacher_min_price);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            numberOfReviews = (TextView) itemView.findViewById(R.id.number_of_reviews);
            readMore = (TextView) itemView.findViewById(R.id.read_more_text_view);
            teacherAvatar = (ImageView) itemView.findViewById(R.id.teacher_avatar);
        }
    }
}
