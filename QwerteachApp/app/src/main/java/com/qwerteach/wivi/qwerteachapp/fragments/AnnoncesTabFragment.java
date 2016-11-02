package com.qwerteach.wivi.qwerteachapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qwerteach.wivi.qwerteachapp.CreateSmallAdActivity;
import com.qwerteach.wivi.qwerteachapp.R;

/**
 * Created by wivi on 26/10/16.
 */

public class AnnoncesTabFragment extends Fragment {

    FloatingActionButton floatingActionButton;
    View view;

    public static AnnoncesTabFragment newInstance() {
        AnnoncesTabFragment annoncesTabFragment = new AnnoncesTabFragment();
        return annoncesTabFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_annonces_tab, container, false);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floating_action_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.floating_action_button) {
                    Intent intent = new Intent(getContext(), CreateSmallAdActivity.class);
                    startActivity(intent);
                }
            }
        });

        return  view;
    }
}
