package com.example.gadfly.projectgadfly;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by papak on 3/22/2017.
 * Create the About Us fragment which created based on the about_fragment layout
 * as a response to the About Us button
 */

public class AboutFragment extends Fragment {

    private View view;
    //Constructor for AboutFragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         final String htmlText = "<body><h1>Project Gadfly</h1><p>The name Project Gadfly is a reference to Socrates’s comparison of an activist, " +
                 "a social transformer, to a gadfly which “stings” and “whips” a slow, unresponsive " +
                 "horse into a charge.<br>" +
                 "Project Gadfly is designed to remove barriers —not knowing who to call, or what to say—" +
                 " to entry for political action.\n" + "We provide a simple button that lets someone call their representative," +
                 " and on the same screen, see a sample script that someone else has uploaded via the website." +
                 "\n" +
                 "Anyone can upload a call script, and the website provides the ability to create a " +
                 "QR code for said script that can be sent or posted anywhere, thus allowing others " +
                 "to pull up the script directly.</p></body>";
        view = inflater.inflate(R.layout.about_fragment, container, false);
        TextView text = (TextView) view.findViewById(R.id.us_description);
        text.setText(Html.fromHtml(htmlText));
        return view;
    }
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.about_us_title);
    }
}
