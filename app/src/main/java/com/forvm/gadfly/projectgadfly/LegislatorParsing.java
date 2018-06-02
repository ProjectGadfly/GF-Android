package com.forvm.gadfly.projectgadfly;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forvm.gadfly.projectgadfly.adapter.RepresentativesRecyclerAdapter;
import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Representative;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atsymbol link LegislatorParsing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {atlink LegislatorParsing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegislatorParsing extends Fragment {

    public LegislatorParsing() {
    }
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private RepresentativesRecyclerAdapter representativesRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_show_reps, container, false);
        RecyclerView recyclerView = v.findViewById(R.id.repsRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        initRepsList(recyclerView);
        return v;
    }

    private void initRepsList(final RecyclerView recyclerView) {
        new Thread() {
            @Override
            public void run() {
                final List<Representative> cities =
                        AppDatabase.getAppDatabase(context).representativeDAO().getAll();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        representativesRecyclerAdapter = new RepresentativesRecyclerAdapter(cities, context);
                        recyclerView.setAdapter(representativesRecyclerAdapter);
                    }
                });
            }
        }.start();
    }




    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.your_representatives);
    }

}