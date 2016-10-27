package com.qwerteach.wivi.qwerteachapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qwerteach.wivi.qwerteachapp.R;

/**
 * Created by wivi on 26/10/16.
 */

public class AnnoncesTabFragment extends Fragment {

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
        return  inflater.inflate(R.layout.fragment_annonces_tab, container, false);
    }
}
