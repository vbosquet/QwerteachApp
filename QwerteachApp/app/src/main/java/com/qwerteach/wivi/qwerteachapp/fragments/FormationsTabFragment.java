package com.qwerteach.wivi.qwerteachapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.qwerteach.wivi.qwerteachapp.R;

/**
 * Created by wivi on 26/10/16.
 */

public class FormationsTabFragment extends Fragment {

    EditText professionEditText;
    EditText schoolEditText;
    EditText levelEditText;
    Button button;

    public static FormationsTabFragment newInstance() {
        FormationsTabFragment formationsTabFragment = new FormationsTabFragment();
        return formationsTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_formations_tab, container, false);
    }

}
