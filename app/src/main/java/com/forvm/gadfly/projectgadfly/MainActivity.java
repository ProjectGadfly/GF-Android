package com.forvm.gadfly.projectgadfly;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Representative;
import com.forvm.gadfly.projectgadfly.data.getRepsResponse;
import com.forvm.gadfly.projectgadfly.network.GadflyAPI;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;
import com.seatgeek.placesautocomplete.model.Place;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    private Fragment Home;
    private AboutFragment aboutFragment;
    private Button btnGetLocation;
    private TicketListFragment ticketListFragment;
    private SharedPreferences pref;
    private Context context;
    //    private static final String BASE_URL = "http://gadfly.mobi/services/v1/";
    private static final String BASE_URL = "http://63.142.250.185/services/v1/";
    private PlacesAutocompleteTextView placesAutocompleteTextView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if (pref.getBoolean("have_reps", false)) {
            Intent intent = new Intent(this, LegislativeActivity.class);
            startActivity(intent);
            finish();
        }
        context = getApplicationContext();

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        fragmentManager = getFragmentManager();
        Home = new HomeFragment();
        fragmentManager.beginTransaction()
                .add(Home, "HOMETAG")
                .replace(R.id.content_main, Home)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aboutFragment = new AboutFragment();
        ticketListFragment = new TicketListFragment();
        View contentView = getWindow().findViewById(R.id.content_main);
        placesAutocompleteTextView = contentView.findViewById(R.id.places_autocomplete);
        btnGetLocation = contentView.findViewById(R.id.btnGetLocation);
        Button submitButton = contentView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickAction();
            }
        });
        Button getLocationButton = findViewById(R.id.btnGetLocation);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        placesAutocompleteTextView.setOnPlaceSelectedListener(
                new OnPlaceSelectedListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        clickAction();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
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
        FragmentManager fragmentManager = getFragmentManager();
        aboutFragment = new AboutFragment();

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
            fragmentManager
                    .beginTransaction()
                    .add(aboutFragment, "ABOUTTAG")
                    .replace(R.id.content_main, aboutFragment)
                    .addToBackStack(null)
                    .commit();
//            mainImage.post(new Runnable() {
//                @Override
//                public void run() {
//                    AnimationDrawable frameAnimation =
//                            (AnimationDrawable) mainImage.getBackground();
//                    frameAnimation.stop();
//                }
//            });
            // Handle the Script Button
        } else if (id == R.id.scripts) {
            fragmentManager
                    .beginTransaction()
                    .add(ticketListFragment, "SCRIPTTAG")
                    .replace(R.id.content_main, ticketListFragment)
                    .addToBackStack(null)
                    .commit();
//            mainImage.post(new Runnable() {
//                @Override
//                public void run() {
//                    AnimationDrawable frameAnimation =
//                            (AnimationDrawable) mainImage.getBackground();
//                    frameAnimation.stop();
//                }
//            });
            //Handle the Tutorial button
        } else if (id == R.id.tutorial) {
            Intent intent = new Intent(getApplicationContext(), Introduction.class);
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("not_first_run", false);
            editor.apply();
//            mainImage.post(new Runnable() {
//                @Override
//                public void run() {
//                    AnimationDrawable frameAnimation =
//                            (AnimationDrawable) mainImage.getBackground();
//                    frameAnimation.stop();
//                }
//            });
            startActivity(intent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ProgressDialog progressDialog;
    private String text;

    //Setting the response after clicking the Legislator Adapter button
    //If the user is not connected to the Internet, a warning message
    public void clickAction() {
        final View contentView = getWindow().findViewById(R.id.content_main);
        if (!TextUtils.isEmpty(placesAutocompleteTextView.getText())) {
            text = placesAutocompleteTextView.getText().toString();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Getting representatives...");
            progressDialog.show();

            final Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            final GadflyAPI gadflyAPI = retrofit.create(GadflyAPI.class);

            Call<getRepsResponse> getRepsResponseCall = gadflyAPI.getReps(text);

            getRepsResponseCall.enqueue(new Callback<getRepsResponse>() {
                @Override
                public void onResponse(Call<getRepsResponse> call, Response<getRepsResponse> response) {
                    if (response.body() != null) {
                        String status = response.body().getStatus();
                        if (status.equalsIgnoreCase("ok")) {
                            List<Representative> results = response.body().getRepresentatives();
                            for (Representative rep : results) {
                                addRep(rep);
                            }
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putBoolean("have_reps", true);
                            editor.apply();
                            Intent intent = new Intent(MainActivity.this, LegislativeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(Call<getRepsResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Snackbar.make(contentView, t.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            Snackbar.make(contentView, R.string.ask_for_address, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    public void addRep(final Representative newRep) {
        new Thread() {
            @Override
            public void run() {
                long id = AppDatabase.getAppDatabase(MainActivity.this).representativeDAO().insertRep(newRep);
                newRep.setRepID(id);
            }
        }.start();
    }


    public void getCurrentLocation() {
        requestNeededPermission();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        final Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final int requestCode = 1;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            progressDialog.dismiss();
            return;
        }
        double latitude = locationManager.getLastKnownLocation("network").getLatitude();
        double longitude = locationManager.getLastKnownLocation("network").getLongitude();
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            Snackbar.make(getWindow().findViewById(R.id.content_main), R.string.location_data_error, Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
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
            placesAutocompleteTextView.setText(fullAddress);
        } else {
            Snackbar.make(getWindow().findViewById(R.id.content_main), R.string.gps_connected_error, Snackbar.LENGTH_LONG).show();
        }
    }

    private void requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(context, "Location permission is needed to get current location.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 101: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    btnGetLocation.setClickable(false);
                    btnGetLocation.setEnabled(false);
                }
            }
        }
    }
}
