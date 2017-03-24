package com.example.gadfly.projectgadfly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RepresentativesDisplay extends Fragment {

    View v;
    ListView lv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.list_view, container, false);

        String text = getArguments().getString("json");
        TextView t = (TextView) v.findViewById(R.id.jsonPlace);
        t.setText(text);

//        JSONArray jsonArray;
//        JSONObject jsonObject1;
//        JSONObject jsonObject2;
//        try {
//            jsonArray = new JSONArray(text);
//            jsonObject1 = jsonArray.getJSONObject(0);
//            jsonObject2 = jsonArray.getJSONObject(1);
//            t.setText(jsonObject1.getString("full_name"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

//        t.setText(jsonObject1.getString("full_name"));



        // Construct the data source
        ArrayList<Representatives> arrayOfUsers = new ArrayList<Representatives>();

        // Create the adapter to convert the array to views
        RepsAdapter adapter = new RepsAdapter(getActivity(), arrayOfUsers);

        // Attach the adapter to a ListView
        Representatives newRep = new Representatives("NathansayassaMe", "5357268368", "emailhere", "district11",
                "state272", "url1", "party2");
        Representatives newRep1 = new Representatives("Alice Me", "5357268368", "emailhere", "district11",
                "state272", "url1", "party2");
        adapter.add(newRep);
        adapter.add(newRep1);
        ListView listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        return v;
    }


}
