package com.forvm.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

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

        TextView Party = (TextView) convertView.findViewById(R.id.party);

        TextView Position = (TextView) convertView.findViewById(R.id.position);
        Position.setText(user.position);

        final ImageView Photo = (ImageView) convertView.findViewById(R.id.photo_url);

        // Populate the data into the template view using the data object
        Name.setText(user.name);


        Glide.with(getContext())
                .load(user.photo_url)
                .placeholder(R.drawable.person_outline)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(110,110)
                .into(Photo);

//        if (!user.photo_url.isEmpty()) {
//            Glide.with(getContext())
//                    .load(user.photo_url)
//                    .asBitmap()
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new BitmapImageViewTarget(Photo) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            Photo.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
//        } else {
//            Glide.with(getContext())
//                    .load(R.drawable.person_outline)
//                    .asBitmap()
//                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(new BitmapImageViewTarget(Photo) {
//                        @Override
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable =
//                                    RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            Photo.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
//        }

        Party.setText(user.party);

        if (user.party.contains("Democrat")) {
            Party.setTextColor(Color.BLUE);
        } else if (user.party.contains("Republic")) {
            Party.setTextColor(Color.RED);
        }

        //Passing the phone number to the call button and activate the
        // call activity in response to the button
        ImageButton btn = (ImageButton) convertView.findViewById(R.id.callButton);
        final TextView phoneNumber = (TextView) convertView.findViewById(R.id.phone);

        phoneNumber.setText(user.phone_number);

        if (user.phone_number.length() > 5) {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    String number = (String) phoneNumber.getText();
                    callIntent.setData(Uri.parse("tel:" + number));
                    getContext().startActivity(callIntent);
                }
            });
        } else {
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "No phone number available", Toast.LENGTH_LONG).show();
                }
            });
        }

        return convertView;
    }
}
