package com.forvm.gadfly.projectgadfly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atsymbol link LegislatorParsing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {atlink LegislatorParsing#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LegislatorParsing extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public LegislatorParsing() {
    }

    ArrayList<Representatives> arrayOfUsers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        String text = getArguments().getString("json");
        String address = getArguments().getString("address");
        JSONObject jsonObject = null;
//        JSONArray jsonArray = null;

        try {
//            jsonArray = new JSONArray(text);
            jsonObject = new JSONObject(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayOfUsers = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("Results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
//                JSONObject j = jsonArray.getJSONObject(i);
//                JSONArray officeA = j.getJSONArray("offices");
//                JSONObject office = officeA.getJSONObject(0);
//                arrayOfUsers.add(new Representatives(j.getString("full_name"),
//                        office.getString("phone"), j.getString("photo_url")));
                JSONObject api_object = jsonArray.getJSONObject(i);
                JSONArray tags = api_object.getJSONArray("tags");
                arrayOfUsers.add(
                        new Representatives(
                                api_object.getString("name"),
                                api_object.getString("phone"),
                                api_object.getString("picURL"),
                                api_object.getString("party"),
                                getPosition(tags)));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        DataHolder.getInstance().setData(arrayOfUsers);
        // Create the adapter to convert the array to views
        RepsAdapter adapter = new RepsAdapter(getActivity(), arrayOfUsers);

        // Attach the adapter to a ListView
        ListView listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        return v;
    }

    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.your_representatives);
    }


    public String getPosition(JSONArray tags) throws JSONException {
        StringBuilder position = new StringBuilder();
        for (int i = tags.length() -1; i >= 0; i--) {
            if (tags.get(i).toString().equalsIgnoreCase("1")) {
                position.append("Federal");
            } else if (tags.get(i).toString().equalsIgnoreCase("2")) {
                position.append("State");
            } else if (tags.get(i).toString().equalsIgnoreCase("3")) {
                position.append("Senator");
            }else if (tags.get(i).toString().equalsIgnoreCase("4")) {
                position.append("Representative");
            }
            position.append(" ");
        }
        return position.toString();
    }

}

class DataHolder {
    private ArrayList<Representatives> data;

    public ArrayList<Representatives> getData() {
        return data;
    }
    public void setData(ArrayList<Representatives> data) {
        this.data = data;
    }
    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }
}
