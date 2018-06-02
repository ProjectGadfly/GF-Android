package com.forvm.gadfly.projectgadfly.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forvm.gadfly.projectgadfly.R;
import com.forvm.gadfly.projectgadfly.SearchScriptActivity;
import com.forvm.gadfly.projectgadfly.data.Ticket;

import java.util.List;

public class TicketRecyclerAdapter extends RecyclerView.Adapter<TicketRecyclerAdapter.ViewHolder> {

    private final List<Ticket> tickets;
    private final Context context;

    public TicketRecyclerAdapter(List<Ticket> ticketList, Context context) {
        tickets = ticketList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_row, parent,
                false);
        return new ViewHolder(viewRow);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Ticket currentTicket = tickets.get(holder.getAdapterPosition());
        holder.tvTicketTitle.setText(currentTicket.getTitle());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("ticket", currentTicket.getTicket());
                Intent intent = new Intent(context, SearchScriptActivity.class);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public void deleteTicket(Ticket ticket) {
        tickets.remove(ticket);
        notifyDataSetChanged();
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
        notifyDataSetChanged();
    }

    public void deleteAllItems() {
        tickets.clear();
        notifyDataSetChanged();
    }

    private int findItemIndexbyItemId(long itemId) {
        for (int i = 0; i < tickets.size(); i++) {
            if (tickets.get(i).getId() == itemId) {
                return i;
            }
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvTicketTitle;

        ViewHolder(final View itemView) {
            super(itemView);

            tvTicketTitle = itemView.findViewById(R.id.tvTicketTitle);
        }
    }
}