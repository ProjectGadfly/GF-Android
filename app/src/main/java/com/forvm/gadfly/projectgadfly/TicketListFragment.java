package com.forvm.gadfly.projectgadfly;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forvm.gadfly.projectgadfly.adapter.TicketRecyclerAdapter;
import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Ticket;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 */
public class TicketListFragment extends Fragment {

    private SharedPreferences pref;
    private RecyclerView recyclerView;
    private TicketRecyclerAdapter ticketRecyclerAdapter;
    private List<Ticket> tickets;

    public TicketListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket_list, container, false);

        recyclerView = view.findViewById(R.id.ticketsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            initTicketList(recyclerView, context);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Your Scripts");
    }

    private void initTicketList(final RecyclerView recyclerView, final Context context) {
        Thread t = new Thread() {
            @Override
            public void run() {
                tickets = AppDatabase.getAppDatabase(recyclerView.getContext()).ticketDAO().getAll();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.setAdapter(new TicketRecyclerAdapter(tickets, context));
                        if (tickets.isEmpty()) {
                            Snackbar.make(getView(), "No tickets", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        t.start();
    }
}
