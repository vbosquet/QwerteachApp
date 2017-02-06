package com.qwerteach.wivi.qwerteachapp.models;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.fragments.DashboardFragment;

import java.util.List;

/**
 * Created by wivi on 4/01/17.
 */

public class TeacherToReviewAdapter extends RecyclerView.Adapter<TeacherToReviewAdapter.ViewHolder> {

    private DashboardFragment fragment;
    private List<User> teachers;

    public TeacherToReviewAdapter(List<User> teachers, DashboardFragment fragment) {
        this.teachers = teachers;
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_to_review_list_view, parent, false);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new TeacherToReviewAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final User user = teachers.get(position);

        holder.title.setText("Recommandez-vous " + user.getFirstName() + " ?");
        holder.reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.didTouchTeacherReviewButton(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        Button reviewButton;

        public ViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title_text_view);
            reviewButton = (Button) itemView.findViewById(R.id.review_button);
        }
    }

}
