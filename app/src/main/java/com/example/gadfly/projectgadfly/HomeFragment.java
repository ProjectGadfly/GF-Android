package com.example.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

        placeText.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        String address = place.description;
                        address = address.replaceAll(" ", "+");
                        if (!address.isEmpty() && isConnected() && isAddressInUS(address)) {
                            editor.putString("address_field", address);
                            editor.putBoolean("have_address", true);
                            editor.apply();
                            Intent intent = new Intent(getContext(), LegislativeActivity.class);
                            intent.putExtra("url", R.string.get_reps_url + address);
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
                    View parentView = v.getRootView();
                    PlacesAutocompleteTextView placesTextView = (PlacesAutocompleteTextView) parentView.findViewById(R.id.places_autocomplete);
                    String address = placesTextView.getText().toString();
                    address = address.replaceAll(" ", "+");
                    if (!address.isEmpty() && isConnected() && isAddressInUS(address)) {
                        ProgressDialog progressDialog = new ProgressDialog(getContext());
                        //Create a dialog when waiting for the activity to execute
                        progressDialog.setMessage("Please wait");
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        editor.putString("address_field", address);
                        editor.putBoolean("have_address", true);
                        editor.apply();
                        Intent intent = new Intent(getContext(), LegislativeActivity.class);
                        intent.putExtra("url", R.string.get_reps_url + address);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (address.isEmpty()) {
                        hideKeyboard();
                        Toast.makeText(getContext(), R.string.ask_for_address, Toast.LENGTH_LONG).show();
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

    public boolean isAddressInUS(String location) {
        if (!(location.contains("united+states") || location.contains("United+States"))) {
            hideKeyboard();
            Toast.makeText(getContext(), R.string.enter_valid_us_address, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            hideKeyboard();
            Toast toast = Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
    }

    public void onResume() {
        super.onResume();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }
}
