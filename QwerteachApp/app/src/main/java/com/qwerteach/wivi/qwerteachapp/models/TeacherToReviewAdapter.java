package com.qwerteach.wivi.qwerteachapp.models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.DashboardFragment;

import java.util.ArrayList;

/**
 * Created by wivi on 4/01/17.
 */

public class TeacherToReviewAdapter extends ArrayAdapter<Teacher> {

    private DashboardFragment fragment;

    public TeacherToReviewAdapter(Context context, ArrayList<Teacher> teachers, DashboardFragment fragment) {
        super(context, 0, teachers);
        this.fragment = fragment;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Teacher teacher = getItem(position);
        TeacherToReviewAdapter.ViewHolder viewHolder;

        if(convertView == null) {
            viewHolder = new TeacherToReviewAdapter.ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.teacher_to_review_list_view, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title_text_view);
            viewHolder.reviewButton = (Button) convertView.findViewById(R.id.review_button);
        } else {
            viewHolder = (TeacherToReviewAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText("Recommandez-vous " + teacher.getFirstName() + " ?");
        viewHolder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchTeacherReviewButton(teacher.getTeacherId());
            }
        });


        return convertView;
    }

    public static class ViewHolder {
        TextView title;
        Button reviewButton;
    }

}
