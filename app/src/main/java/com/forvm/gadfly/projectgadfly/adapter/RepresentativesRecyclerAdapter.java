package com.forvm.gadfly.projectgadfly.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.forvm.gadfly.projectgadfly.R;
import com.forvm.gadfly.projectgadfly.data.Representative;

import java.util.List;

public class RepresentativesRecyclerAdapter extends RecyclerView.Adapter<RepresentativesRecyclerAdapter.ViewHolder> {

    private final List<Representative> representativeList;
    private final Context context;

    public RepresentativesRecyclerAdapter(List<Representative> representatives, Context context) {
        representativeList = representatives;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.representative_row, parent,
                false);
        return new ViewHolder(viewRow);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Representative currentRep = representativeList.get(holder.getAdapterPosition());
        Glide.with(context)
                .load(currentRep.getPicURL())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.person_outline)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(110, 110))
                .into(holder.ivRepImage);

        holder.tvRepName.setText(currentRep.getName());
        holder.tvRepPhoneNumber.setText(currentRep.getPhone());
        holder.tvRepParty.setText(currentRep.getParty());
        holder.tvRepParty.setTextColor(getColorFromParty(currentRep));
        holder.repCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentRep.getPhone() != null) {
                    makePhoneCall(currentRep.getPhone());
                } else {
                    Toast.makeText(context, "Sorry, no phone number available!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private int getColorFromParty(Representative currentRep) {
        if (currentRep.getParty().equals("Democratic")) {
            return Color.BLUE;
        } else if (currentRep.getParty().equals("Republican")) {
            return Color.RED;
        }
        return Color.BLACK;
    }

    public void makePhoneCall(String phoneNumber) {
        if (phoneNumber.length() > 5) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(callIntent);
        } else {
            Toast.makeText(context, "No phone number available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return representativeList.size();
    }

    public void addCity(Representative representative) {
        representativeList.add(representative);
        notifyDataSetChanged();
    }

    public void deleteAllItems() {
        representativeList.clear();
        notifyDataSetChanged();
    }

    private int findItemIndexbyItemId(long itemId) {
        for (int i = 0; i < representativeList.size(); i++) {
            if (representativeList.get(i).getRepID() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivRepImage;
        private final TextView tvRepName;
        private final TextView tvRepPhoneNumber;
        private final TextView tvRepParty;
        //        private final TextView tvRepPostition;
        private final Button repCallButton;

        ViewHolder(final View itemView) {
            super(itemView);

            ivRepImage = itemView.findViewById(R.id.ivRepImage);
            tvRepName = itemView.findViewById(R.id.tvRepName);
            tvRepPhoneNumber = itemView.findViewById(R.id.tvRepPhoneNumber);
            tvRepParty = itemView.findViewById(R.id.tvRepParty);
//            tvRepPostition = itemView.findViewById(R.id.tvRepPosition);
            repCallButton = itemView.findViewById(R.id.callButton);

        }
    }
}