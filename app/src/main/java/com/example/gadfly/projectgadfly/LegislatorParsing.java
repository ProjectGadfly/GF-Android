package com.example.gadfly.projectgadfly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atsymbol link LegislatorParsing.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LegislatorParsing#newInstance} factory method to
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
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LegislatorParsing.
     */
    // TODO: Rename and change types and number of parameters
    public static LegislatorParsing newInstance(String param1, String param2) {
        LegislatorParsing fragment = new LegislatorParsing();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ArrayList<Representatives> arrayOfUsers;
    //Create
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_blank, container, false);
        String text = getArguments().getString("json");
        String address = getArguments().getString("address");
        TextView t = (TextView) v.findViewById(R.id.jsonPlace);
        t.setText(address);
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        arrayOfUsers = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {

            try {
                JSONObject j = jsonArray.getJSONObject(i);
                JSONArray officeA = j.getJSONArray("offices");
                JSONObject office = officeA.getJSONObject(0);
                arrayOfUsers.add(new Representatives(j.getString("full_name"),
                        office.getString("phone"), j.getString("photo_url")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Create the adapter to convert the array to views
        RepsAdapter adapter = new RepsAdapter(getActivity(), arrayOfUsers);

        // Attach the adapter to a ListView
        ListView listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(adapter);
        return v;
    }

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
class DataHolder {
    private ArrayList<Representatives> data;
    public ArrayList<Representatives> getData() {return data;}
    public void setData(ArrayList<Representatives> data) {this.data = data;}
    private static final DataHolder holder = new DataHolder();
    public static DataHolder getInstance() {return holder;}
}
