package com.forvm.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atlink ScanResult.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {atlink ScanResult#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanResult extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public ScanResult() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanResult.
     */
    // TODO: Rename and change types and number of parameters
    public static ScanResult newInstance(String param1, String param2) {
        ScanResult fragment = new ScanResult();
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

    private ProgressDialog progressDialog;
    private String jsonString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_result, container, false);
        TextView scanResultTitle = (TextView) view.findViewById(R.id.scanResultTitle);
        TextView scanResultContent = (TextView) view.findViewById(R.id.scanResultContent);
        TextView scanResultTags = (TextView) view.findViewById(R.id.scanResultTags);
        ListView listView = (ListView) view.findViewById(R.id.resultList);

      ArrayList<Representatives> arrayOfUsers = DataHolder.getInstance().getData();
        try {
            Object result = new ScanResultJsonTask().execute(getArguments().getString("scanContent")).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        RepsAdapter repsAdapter = new RepsAdapter(getActivity(), arrayOfUsers);
        listView.setAdapter(repsAdapter);
        if (getArguments()!= null) {
            //            scanResultView.setText(getArguments().getString("scanContent"));
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONObject script = jsonObject.getJSONObject("Script");
                scanResultTitle.setText(script.getString("title"));
                scanResultContent.setText(script.getString("content"));
                scanResultTags.setText(getPosition(script.getJSONArray("tags")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            getFragmentManager().popBackStackImmediate();
        }

        return view;

    }

    public String getPosition(JSONArray tags) throws JSONException {
        StringBuilder position = new StringBuilder();
        for (int i = 0; i < tags.length(); i++) {
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
        return "Applicable to: " + position.toString().toUpperCase();
    }

    /**
     * Parsing Json AsyncTask
     */
    public class ScanResultJsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getApplicationContext());
//            //Create a dialog when waiting for the activity to execute
//            progressDialog.setMessage("Please wait");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("APIKey", "v1key");
                connection.setConnectTimeout(5000);

                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();
                return jsonString;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Snackbar.make(getActivity().getWindow().findViewById(R.id.legislator_page),
                        R.string.server_connection_error,
                        Snackbar.LENGTH_LONG)
                        .show();
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            if (progressDialog.isShowing()){
//                progressDialog.dismiss();
//            }
        }
    }

}
