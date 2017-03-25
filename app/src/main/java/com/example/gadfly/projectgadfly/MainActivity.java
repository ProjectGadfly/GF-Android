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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText edit;
    private FragmentManager fragmentManager;
    private Fragment Home;
    private AboutFragment aboutFragment;
    private Bundle b;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Realm.init(this);

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
        Home = new HomeFragment();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                .add(Home, "HOMETAG").replace(R.id.content_main, Home).commit();

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
        aboutFragment = new AboutFragment();
        TeamFragment team = new TeamFragment();
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
                aboutFragment = new AboutFragment();
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
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void clickAdapt(View v) {
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
