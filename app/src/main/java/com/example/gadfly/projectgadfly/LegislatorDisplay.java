package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;


public class LegislatorDisplay extends Fragment {
    View myView;
    ListView listView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.fragment_legislator_display, null );
        ListView listView = (ListView)myView.findViewById(R.id.listView);
        ArrayList<Legislators> arrayOfLegislators = new ArrayList<Legislators>();
        LegislatorAdapter adapter = new LegislatorAdapter(getActivity(), arrayOfLegislators);

        Legislators newUser = new Legislators("Nathan", "Pham" , "dssdgs@grinnell.edu", "email");
        Legislators newUser2 = new Legislators("Linh", "Pham", "phamlinh@grinnell.edu", "email");
        adapter.add(newUser);
        adapter.add(newUser2);

        listView.setAdapter(adapter);
        return myView;
    }
}
