package com.forvm.gadfly.projectgadfly;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by papak on 3/22/2017.
 * Create Home Fragment from the home_fragment xml layout in response to the Home button
 */

public class HomeFragment extends Fragment {
    private View v;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.home_fragment, container, false);
        return v;
    }

    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        final ImageView mainImage = v.findViewById(R.id.mainImage);
//        mainImage.setBackgroundResource(R.drawable.anim);
//        mainImage.post(new Runnable() {
//            @Override
//            public void run() {
//                AnimationDrawable frameAnimation =
//                        (AnimationDrawable) mainImage.getBackground();
//                frameAnimation.start();
//            }
//        });

    }
}
