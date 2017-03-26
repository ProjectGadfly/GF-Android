package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
        final TextView Name = (TextView) convertView.findViewById(R.id.rep_name);
        ImageView Photo = (ImageView) convertView.findViewById(R.id.photo_url);
        // Populate the data into the template view using the data object
        Context context = getContext();
        final PackageManager m = context.getPackageManager();
        Name.setText(user.name);
        Glide.with(getContext())
                .load(user.photo_url)
                .placeholder(R.drawable.person_outline)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(120,120)
                .into(Photo);

        Button btn = (Button) convertView.findViewById(R.id.callButton);
        final TextView phoneNumber = (TextView) convertView.findViewById(R.id.phone);
        phoneNumber.setText(user.phone_number);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                String number = (String) phoneNumber.getText();
                callIntent.setData(Uri.parse("tel:" + number));
                getContext().startActivity(callIntent);
            }
        });
        return convertView;
    }
}
