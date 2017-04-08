package com.example.gadfly.projectgadfly;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    private Fragment Home;
    private AboutFragment aboutFragment;
    private Bundle b;
    private SharedPreferences pref;
    private Context context;


    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        b = new Bundle();
        if (pref.getBoolean("have_address", false)) {
            Intent intent = new Intent(this, LegislativeActivity.class);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }
        context = getApplicationContext();

        setContentView(R.layout.activity_main);

        changeStatusBarColor();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        fragmentManager = getSupportFragmentManager();
        Home = new HomeFragment();
        fragmentManager.beginTransaction()
                .add(Home, "HOMETAG")
                .replace(R.id.content_main, Home)
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        View contentView =  getWindow().findViewById(R.id.content_main);
        final PlacesAutocompleteTextView placeText = (PlacesAutocompleteTextView) contentView.findViewById(R.id.places_autocomplete);
        placeText.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        try {
                            clickAction(placeText);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        aboutFragment = new AboutFragment();
        TeamFragment team = new TeamFragment();
        //Handle the Home button
        if (id == R.id.homeView) {
            if (!fragmentManager.findFragmentByTag("HOMETAG").isVisible())
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.content_main, Home)
                        .addToBackStack(null)
                        .commit();
            //Handle the About button
        } else if (id == R.id.about) {
            aboutFragment = new AboutFragment();
            fragmentManager
                    .beginTransaction()
                    .add(aboutFragment, "ABOUTTAG")
                    .replace(R.id.content_main, aboutFragment)
                    .addToBackStack(null)
                    .commit();
            //Handle the Team button
        } else if (id == R.id.team) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, team)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.tutorial) {
            Intent intent = new Intent(getApplicationContext(), Introduction.class);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("not_first_run", false);
            editor.apply();
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Checks if the user is connected to the internet, returns false if not
    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        } else {
            hideKeyboard();
            Toast toast = Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }
    }

    public boolean isValidAddress(String address) {
        if (isAddressInUS(address) && address.length() > 20) {
            return true;
        }
        Toast.makeText(getApplicationContext(),"Please enter a more specific address", Toast.LENGTH_LONG).show();
        return false;
    }

    public boolean isAddressInUS(String location) {
        if (!(location.contains("united+states")
                || location.contains("United+States")
                || location.contains("united+States")
                || location.contains("United+states"))) {
            hideKeyboard();
            Snackbar.make(getWindow().findViewById(R.id.content_main), "Please enter an address in the United States", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }


    private ProgressDialog progressDialog;
    //Setting the response after clicking the Legislator Adapter button
    //If the user is not connected to the Internet, a warning message
    public void clickAction(View v) throws ExecutionException, InterruptedException {
        View parentView = v.getRootView();
        PlacesAutocompleteTextView placesTextView = (PlacesAutocompleteTextView) parentView.findViewById(R.id.places_autocomplete);
        String text = placesTextView.getText().toString();

        View contentView = getWindow().findViewById(R.id.content_main);
        text = text.replaceAll(" ", "+");
        if (!text.isEmpty() && isConnected() && isValidAddress(text)) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("address_field", text);
            editor.putBoolean("have_address", true);
            editor.apply();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Getting representatives...");
            progressDialog.show();
            new JsonTask().execute(getString(R.string.get_reps_url) + text);
        } else {
            if (text.isEmpty()) {
                Snackbar.make(contentView, R.string.ask_for_address, Snackbar.LENGTH_LONG)
                        .show();
            }
            if (!isConnected()) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG);
                toast.show();
                Snackbar.make(contentView, R.string.no_internet, Snackbar.LENGTH_LONG)
                        .show();
            }
        }
    }


    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }



    private String jsonString;
    /**
     * Parsing Json AsyncTask
     */
    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
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
                Snackbar.make(getWindow().findViewById(R.id.legislator_page),
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
            if (progressDialog.isShowing()){
                progressDialog.dismiss();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("json", jsonString);
                editor.apply();
                Intent intent = new Intent(getApplicationContext(), LegislativeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    public void getCurrentLocation(View view) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        //        Toast.makeText(getApplicationContext(), "JAJAJA", Toast.LENGTH_LONG).show();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        Location location = null;
        final int requestCode = 1;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation

            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getApplicationContext(), "No Permission", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

        double latitude = locationManager.getLastKnownLocation("network").getLatitude();
        double longitude = locationManager.getLastKnownLocation("network").getLongitude();
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error getting location. Please try again later", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
            //            e.printStackTrace();
        }
        progressDialog.dismiss();
        String streetAddress, city, state, country, postalCode;
        if (addresses.size() > 0) {
            streetAddress = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            String fullAddress = streetAddress + ", " + city + ", " + state + ", " + country + ", " + postalCode;
            PlacesAutocompleteTextView textView = (PlacesAutocompleteTextView) findViewById(R.id.places_autocomplete);
            textView.setText(fullAddress);
        } else {
            Toast.makeText(getApplicationContext(), "Error. Do you have GPS turned on?", Toast.LENGTH_LONG).show();
        }
        // getting GPS status
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

}
