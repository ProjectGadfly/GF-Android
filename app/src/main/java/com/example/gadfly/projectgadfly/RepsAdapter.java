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
 * Created by papak on 3/21/2017.
 */

public class RepsAdapter extends ArrayAdapter<Representatives> {
        public RepsAdapter(Context context, ArrayList<Representatives> reps) {
            super(context, 0, reps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Representatives user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_rep, parent, false);
            }
            // Lookup view for data population
            TextView Name = (TextView) convertView.findViewById(R.id.rep_name);
            TextView District = (TextView) convertView.findViewById(R.id.district);
            TextView State = (TextView) convertView.findViewById(R.id.state);
            TextView Phone = (TextView) convertView.findViewById(R.id.phone);
            TextView Email = (TextView) convertView.findViewById(R.id.email);
            TextView Party = (TextView) convertView.findViewById(R.id.party);
            ImageView Photo = (ImageView) convertView.findViewById(R.id.photo_url);
            // Populate the data into the template view using the data object
            Name.setText(user.name);
            District.setText(user.district);
            State.setText(user.state);
            Phone.setText(user.phone_number);
            Email.setText(user.email);
            Party.setText(user.party);
//            Photo.setImageDrawable(R.drawable.rep1);
            Glide.with(getContext())
                    .load(user.photo_url)
                    .override(200,400)
                    .into(Photo);
            // Return the completed view to render on screen
            return convertView;
        }
}
