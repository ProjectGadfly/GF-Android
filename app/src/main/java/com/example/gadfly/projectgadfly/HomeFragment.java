package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

/**
 * Created by papak on 3/22/2017.
 * Create Home Fragment from the home_fragment xml layout in response to the Home button
 */

public class HomeFragment extends Fragment {
    private View v;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sharedPreferences = getContext().getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        v = inflater.inflate(R.layout.home_fragment, container, false);
        PlacesAutocompleteTextView placeText = (PlacesAutocompleteTextView) v.findViewById(R.id.places_autocomplete);

        placeText.setOnPlaceSelectedListener(new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        String text = place.description;
                        text = text.replaceAll(" ", "+");
                        if (!text.isEmpty() && isConnected()) {
                            editor.putString("address_field", text);
                            editor.putBoolean("have_address", true);
                            editor.apply();
                            Intent intent = new Intent(getContext(), LegislativeActivity.class);
                            intent.putExtra("url", "https://ourapi/" + text);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }
                }
        );


        EditText editText = (EditText) v.findViewById(R.id.places_autocomplete);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // clickAction(v);

                    View contentView = getActivity().findViewById(android.R.id.content);
                    View parentView = v.getRootView();
                    PlacesAutocompleteTextView placesTextView = (PlacesAutocompleteTextView) parentView.findViewById(R.id.places_autocomplete);
                    String text = placesTextView.getText().toString();
                    text = text.replaceAll(" ", "+");
                    if (!text.isEmpty() && isConnected()) {
                        editor.putString("address_field", text);
                        editor.putBoolean("have_address", true);
                        editor.apply();
                        Intent intent = new Intent(getContext(), LegislativeActivity.class);
                        intent.putExtra("url", "https://ourapi/" + text);
                        startActivity(intent);
                        getActivity().finish();

                    } else {
                        if (text.isEmpty()) {
                            Toast.makeText(getContext(), "Please enter your address", Toast.LENGTH_LONG).show();
                        }
                        if (!isConnected()) {
                            Toast toast = Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG);
                            toast.show();
                            Snackbar.make(contentView, R.string.no_internet, Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                    return true;

                } else {
                    // it is not an Enter key - let others handle the event
                    return false;
                }
            }

        });
        return v;
    }
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle("Project Gadfly");
    }
}
