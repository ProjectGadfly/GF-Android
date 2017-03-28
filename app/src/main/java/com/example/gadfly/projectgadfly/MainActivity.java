package com.example.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.seatgeek.placesautocomplete.PlacesAutocompleteTextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentManager fragmentManager;
    private Fragment Home;
    private AboutFragment aboutFragment;
    private Bundle b;
    private SharedPreferences pref;


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
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

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

        fragmentManager = getSupportFragmentManager();
        Home = new HomeFragment();
        fragmentManager.beginTransaction()
                .add(Home, "HOMETAG")
                .replace(R.id.content_main, Home)
                .commit();

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
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    public boolean isAddressInUS(String location) {
        if (!(location.contains("united+states") || location.contains("United+States"))) {
            Snackbar.make(getWindow().findViewById(R.id.content_main), "Please enter an address in the United States", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        return true;
    }

    //Setting the response after clicking the Legislator Adapter button
    //If the user is not connected to the Internet, a warning message
    public void clickAction(View v) {
        View parentView = v.getRootView();
        PlacesAutocompleteTextView placesTextView = (PlacesAutocompleteTextView) parentView.findViewById(R.id.places_autocomplete);
        String text = placesTextView.getText().toString();

        View contentView = getWindow().findViewById(R.id.content_main);
        text = text.replaceAll(" ", "+");
        if (!text.isEmpty() && isConnected() && isAddressInUS(text)) {
            SharedPreferences.Editor editor = pref.edit();
            DataHolder2.getInstance().setData(editor);
            editor.putString("address_field", text);
            editor.putBoolean("have_address", true);
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), LegislativeActivity.class);
            intent.putExtra("url", "https://ourapi/" + text);
            startActivity(intent);
            finish();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

}
class DataHolder2 {
    private SharedPreferences.Editor data;
    public SharedPreferences.Editor getData() {return data;}
    public void setData(SharedPreferences.Editor data) {this.data = data;}
    private static final DataHolder2 holder = new DataHolder2();
    public static DataHolder2 getInstance() {return holder;}
}