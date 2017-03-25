package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private double LatAdd = 0;
    private double LngAdd = 0;
    public EditText edit;


    FragmentManager fragmentManager;
    Fragment Home;
    Fragment Web;
    Fragment Rep;
    AboutFragment aboutFragment;
    Bundle b;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Realm.init(this);
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        b = new Bundle();
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, AlwaysRunActivity.class);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
//        Fragment Home = fragmentManager.findFragmentById(R.id.home_fragment);
        Home = new HomeFragment();
//        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                .add(Home, "HOMETAG").replace(R.id.content_main, Home).commit();

//        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
//        LayoutInflater inflate = getLayoutInflater();
//        View home = inflate.inflate(R.layout.home_fragment, null);
//        edit = (EditText) home.findViewById(R.id.addressfield);

    }

    @Override
    protected void onStart() {
        super.onStart();
        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
    }

    @Override
    protected void onResume() {
        super.onResume();
        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
    }

    @Override
    protected void onPause() {
        super.onPause();
        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
    }


    @Override
    protected void onStop() {
        super.onStop();
        edit = (EditText) Home.getView().findViewById(R.id.addressfield);
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
//        aboutFragment = new AboutFragment();
        aboutFragment = new AboutFragment();
        TeamFragment team = new TeamFragment();
//        fragmentManager.beginTransaction().add(aboutFragment, "ABOUTTAG");
        if (id == R.id.homeView) {
            // Handle the camera action
            if (!fragmentManager.findFragmentByTag("HOMETAG").isVisible())
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                    .replace(R.id.content_main, Home)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.about) {
//            if (!fragmentManager.findFragmentByTag("ABOUTTAG").isAdded()) {
                aboutFragment = new AboutFragment();
//            }
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                    .add(aboutFragment, "ABOUTTAG")
                    .replace(R.id.content_main, aboutFragment)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.team) {
            fragmentManager
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                    .replace(R.id.content_main, team)
                    .addToBackStack(null)
                    .commit();
        } //else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getLocationFromAddress(String strAddress){
        //Creates new geocoder object
        Geocoder coder = new Geocoder(this);

        List<Address> address = null;

        // Attempt to get Location from entered address
        try {
            address = coder.getFromLocationName(strAddress,5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address location = null;

        //Checks if address is valid and gets coordinates
        if (address.size() != 0) {
            location = address.get(0);
            LatAdd = location.getLatitude();
            LngAdd = location.getLongitude();
        } else {
            final Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_address, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void clickAction(View v) {
        // Get entered address from text field
        String address = edit.getText().toString();
        //Store Application Context for easy referencing later
        Context context = getApplicationContext();

        //Check if the user has entered any text
        if (!address.isEmpty()) {
            Toast toast = Toast.makeText(context, address, Toast.LENGTH_LONG);

            // Checks to see whether we have an active internet connection
            ConnectivityManager cm =
                    (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();

            // Searches for entered location if there is an internet connection
            if (isConnected) {
                getLocationFromAddress(address);
                toast.show();
                final Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                b.putDouble("lat", LatAdd);
                b.putDouble("lng", LngAdd);
                intent.putExtras(b);
                startActivity(intent);
            } else {
                toast = Toast.makeText(context, R.string.no_internet, Toast.LENGTH_LONG);
                toast.show();
            }
        } else {
            final Toast toast = Toast.makeText(getApplicationContext(), R.string.ask_for_address, Toast.LENGTH_LONG);
            toast.show();
        }
    }
//    Web = null;


    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void clickAdapt(View v) {
//        Bundle bundle = new Bundle();
//        Log.e("ERROR2", result1.substring(0,20));
//        bundle.putString("json", result1);
//        Log.e("ERROR", result1.substring(0,20));
//        Rep = new epresentativesDisplay();
//        Rep.setArguments(bundle);
//        fragmentManager
//                .beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
//                .replace(R.id.content_main, Rep)
//                .addToBackStack(null)
//                .commit();
//        t.setText(jsonA.toString());
//        edit = Home.get
//        fragmentManager.beginTransaction().replace(R.id.content_main, Rep).commit();
//
        if (isConnected()) {
            final Intent intent = new Intent(getApplicationContext(), AlwaysRunActivity.class);
            intent.putExtra("url", "https://openstates.org/api/v1/legislators/?state=dc&chamber=upper");
            startActivity(intent);
            finish();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),"You are not conntected to the internet",Toast.LENGTH_LONG);
            toast.show();
        }

    }

}
