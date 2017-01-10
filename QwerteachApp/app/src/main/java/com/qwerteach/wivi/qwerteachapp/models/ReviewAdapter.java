package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;

import java.util.ArrayList;

/**
 * Created by wivi on 9/01/17.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    public ReviewAdapter(Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        final Review review = getItem(position);
        ReviewAdapter.ViewHolder viewHolder;
        String creationDate = review.getCreationDate();

        if(convertView == null) {

            viewHolder = new ReviewAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.comment_list_view, parent, false);
            viewHolder.senderFirstName = (TextView) convertView.findViewById(R.id.sender_first_name);
            viewHolder.sendingDate = (TextView) convertView.findViewById(R.id.sending_date);
            viewHolder.reviewText = (TextView) convertView.findViewById(R.id.review_text);
            viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ReviewAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.senderFirstName.setText(review.getSenderFirstName());
        viewHolder.sendingDate.setText(review.getMonth(creationDate) + " " + review.getYear(creationDate));
        viewHolder.reviewText.setText(review.getReviewText());
        viewHolder.ratingBar.setRating(review.getNote());

        return convertView;
    }


    public static class ViewHolder {
        TextView senderFirstName;
        TextView sendingDate;
        TextView reviewText;
        RatingBar ratingBar;

    }
}
