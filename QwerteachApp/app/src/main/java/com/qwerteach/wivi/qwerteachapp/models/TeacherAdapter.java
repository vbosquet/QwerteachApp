package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 17/11/16.
 */

public class TeacherAdapter extends ArrayAdapter<Teacher> {

    public TeacherAdapter(Context context, ArrayList<Teacher> teacher) {
        super(context, 0, teacher);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Teacher teacher = getItem(position);
        ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.teacher_list_view, parent, false);
            viewHolder.teacherName = (TextView) convertView.findViewById(R.id.teacher_name_text_view);
            viewHolder.description = (TextView) convertView.findViewById(R.id.teach_description_text_view);
            viewHolder.materialCourseNames = (TextView) convertView.findViewById(R.id.teacher_course_material_names_text_view);
            viewHolder.minPrice = (TextView) convertView.findViewById(R.id.teacher_min_price);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            viewHolder.numberOfReviews = (TextView) convertView.findViewById(R.id.number_of_reviews);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.teacherName.setText(teacher.getFirstName() + " " + teacher.getLastName());
        String text = teacher.getDescription();
        text = text.replace("\\n\\n", "");
        text = text.replace("<p>", "");
        text= text.replace("</p>", "<br>");
        viewHolder.description.setText(Html.fromHtml(text), TextView.BufferType.SPANNABLE);
        viewHolder.materialCourseNames.setText(teacher.getTopicTitleList());
        viewHolder.minPrice.setText(teacher.getMinPrice() + " €/h");
        viewHolder.ratingBar.setRating(teacher.getRating());
        viewHolder.numberOfReviews.setText(teacher.getNumberOfReviews() + " commentaire(s)");

        return convertView;
    }

    public static class ViewHolder {
        TextView teacherName;
        TextView description;
        TextView materialCourseNames;
        TextView minPrice;
        TextView numberOfReviews;
        RatingBar ratingBar;
    }
}
