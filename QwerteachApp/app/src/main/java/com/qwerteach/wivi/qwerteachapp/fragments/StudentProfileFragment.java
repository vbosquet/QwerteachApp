package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qwerteach.wivi.qwerteachapp.R;
import com.qwerteach.wivi.qwerteachapp.models.User;

/**
 * Created by wivi on 12/01/17.
 */

public class StudentProfileFragment extends Fragment {

    View view;
    TextView firstNameAndLastNameTextView, ageTextView, occupationTextView, descriptionTextView;
    User user;

    public static StudentProfileFragment newInstance() {
        StudentProfileFragment studentProfileFragment = new StudentProfileFragment();
        return studentProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_student_profile, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable("student");
        }

        firstNameAndLastNameTextView = (TextView) view.findViewById(R.id.firstname_and_lastanme_text_view);
        ageTextView = (TextView) view.findViewById(R.id.age_text_view);
        occupationTextView = (TextView) view.findViewById(R.id.occupation_text_view);
        descriptionTextView = (TextView) view.findViewById(R.id.description_text_view);

        displayStudentProfileInfos();

        return  view;
    }

    public void  displayStudentProfileInfos() {
        firstNameAndLastNameTextView.setText(user.getFirstName() + " " + user.getLastName());
        ageTextView.setText(user.getAge() + " ans");
        occupationTextView.setText(user.getOccupation());
        descriptionTextView.setText(user.getDescription());
    }
}
