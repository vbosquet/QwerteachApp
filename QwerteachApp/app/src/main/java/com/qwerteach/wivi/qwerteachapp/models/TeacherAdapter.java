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
    private Context context;
    private ISearcTeacher callback;

    public TeacherAdapter(Context context, ArrayList<Teacher> teachers, ISearcTeacher callback) {
        this.teachers = teachers;
        this.context = context;
        this.callback = callback;
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
        holder.minPrice.setText(teacher.getMinPrice() + " €/h");
        holder.ratingBar.setRating(teacher.getRating());
        holder.numberOfReviews.setText(teacher.getNumberOfReviews() + " commentaire(s)");

        Picasso.with(context)
                .load(teacher.getUser().getAvatarUrl())
                .resize(1800, 1800).centerInside()
                .into(holder.teacherAvatar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onClicked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return teachers.size();
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
        TextView teacherName, minPrice, numberOfReviews;
        RatingBar ratingBar;
        ImageView teacherAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            teacherName = (TextView) itemView.findViewById(R.id.teacher_name_text_view);
            minPrice = (TextView) itemView.findViewById(R.id.teacher_min_price);
            ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
            numberOfReviews = (TextView) itemView.findViewById(R.id.number_of_reviews);
            teacherAvatar = (ImageView) itemView.findViewById(R.id.teacher_avatar);
        }
    }

    public interface ISearcTeacher {
        void onClicked(int position);
    }
}
