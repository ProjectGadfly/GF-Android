package com.forvm.gadfly.projectgadfly;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forvm.gadfly.projectgadfly.data.Script;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atlink ScanResult.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {atlink ScanResult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanResult extends Fragment {

    public ScanResult() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_result, container, false);
        TextView scanResultTitle = (TextView) view.findViewById(R.id.scanResultTitle);
        TextView scanResultContent = (TextView) view.findViewById(R.id.scanResultContent);
        TextView scanResultTags = (TextView) view.findViewById(R.id.scanResultTags);
        Bundle bundle = getArguments();

        if (getArguments() != null) {
            Script script = (Script) bundle.getSerializable("script");
            scanResultTitle.setText(script.getTitle());
            scanResultContent.setText(script.getContent());
            scanResultTags.setText(getPosition(script.getTags()));
        } else {
            getFragmentManager().popBackStackImmediate();
        }
        return view;
    }

    public String getPosition(List<Integer> tags) {
        StringBuilder position = new StringBuilder();
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).toString().equalsIgnoreCase("1")) {
                position.append("Federal");
            } else if (tags.get(i).toString().equalsIgnoreCase("2")) {
                position.append("State");
            } else if (tags.get(i).toString().equalsIgnoreCase("3")) {
                position.append("Senator");
            } else if (tags.get(i).toString().equalsIgnoreCase("4")) {
                position.append("Results");
            }
            position.append(" ");
        }
        return "Applicable to: " + position.toString().toUpperCase();
    }

    @Override
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Your Script");
    }

}
