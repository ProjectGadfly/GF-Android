package com.example.gadfly.projectgadfly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by papak on 3/22/2017.
 * Create the About Us fragment which created based on the about_fragment layout
 * as a response to the About Us button
 */

public class AboutFragment extends Fragment {
    private View view;
    //Constructor for AboutFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.about_fragment, container, false);
        return view;
    }
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.about_us_title);
    }
}
