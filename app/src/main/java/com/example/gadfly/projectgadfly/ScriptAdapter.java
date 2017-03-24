package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

/**
 * Created by Linh Pham on 3/22/2017.
 */

public class ScriptAdapter extends ArrayAdapter<String> {
    public ScriptAdapter(Context context, ArrayList<String> script) {
        super(context, 0, script);
    }

    @Override
    public View getView(int position, View contextView, ViewGroup parent) {
        String script = getItem(position);
        if (contextView == null) {
            contextView = LayoutInflater.from(getContext()).inflate(R.layout.script, parent, false);
        }
        TextView scriptView = (TextView) contextView.findViewById(R.id.script);
        scriptView.setText(script);
        return contextView;
    }
}

