package com.example.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class LegislativeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private IntentIntegrator qrScan;
    private LegislatorParsing legislatorParsing;
    private FragmentManager fragmentManager;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }


        //Set the layout of the activity based on legislator_activity layout
        setContentView(R.layout.legislator_activity);

        changeStatusBarColor();

        fragmentManager = getSupportFragmentManager();

        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);


        //Set up the navigation bar of the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Set up the QR code scan in response to the scan button
        qrScan = new IntentIntegrator(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //Set up the icon of the button
        fab.setImageResource(R.drawable.ic_scan_icon);
        //Initiate scan activity
        fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrScan.initiateScan();
            }
        });
        //Set up the navigation bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle bundle = new Bundle();
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        String url;
        if (pref.getString("json", "").equalsIgnoreCase("")) {
            if (pref.getBoolean("have_address", false)) {
//                url = "https://openstates.org/api/v1/legislators/?state=dc&chamber=upper";
//            url = pref.getString("address_field", "");
                url = "https://api.myjson.com/bins/1bxuqf";
            } else {
//                url = "https://openstates.org/api/v1/legislators/?state=dc&chamber=upper";
                url = "https://api.myjson.com/bins/1bxuqf";
                //Error should never get here
            }
            try {
                Object result = new JsonTask().execute(url).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            try {
                if (!handleErrorMessage(jsonString)) {
//
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean("have_address", false);
                    editor.apply();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("json", jsonString);
        editor.apply();
        bundle.putString("json", pref.getString("json", ""));
        bundle.putString("address", pref.getString("address_field", ""));

        legislatorParsing = new LegislatorParsing();
        legislatorParsing.setArguments(bundle);

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_r, R.anim.slide_out_l, R.anim.slide_in_l, R.anim.slide_out_r)
                .add(legislatorParsing, "BLANK").replace(R.id.content_main, legislatorParsing).commit();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            String scanFormat = scanningResult.getFormatName();
            String scanContent = scanningResult.getContents();
            if (scanContent != null) {
                ScanResult scanResult = new ScanResult();
                LegislatorParsing legislatorParsing = new LegislatorParsing();
                Bundle bundle = new Bundle();
                bundle.putString("scanFormat", scanFormat);
                bundle.putString("scanContent", scanContent);
                scanResult.setArguments(bundle);

                fragmentManager
                        .beginTransaction()
                        .add(scanResult, "SCANPAGE")
                        .replace(R.id.content_main, scanResult)
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "No data", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Set up alert dialog when clicking on the forget me button
        if (id == R.id.action_settings) {
            DialogFragment alertDialExample = new ForgetDialogFragment();
            alertDialExample.show(getSupportFragmentManager(), "AlertDialogFragment");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        FragmentManager fragmentManager = getSupportFragmentManager();

        AboutFragment aboutFragment = new AboutFragment();
        //LegislatorParsing legislatorParsing = new LegislatorParsing();
        TeamFragment team = new TeamFragment();
        int id = item.getItemId();

        if (id == R.id.homeView) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, legislatorParsing)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.about) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, aboutFragment)
                    .addToBackStack(null)
                    .commit();
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



    private ProgressDialog progressDialog;
    private String jsonString;
    /**
     * Parsing Json AsyncTask
     */
    private class JsonTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(LegislativeActivity.this);
            //Create a dialog when waiting for the activity to execute
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
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
                        "Error connecting to our servers. Please try again later.",
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
            }
        }
    }

    private boolean handleErrorMessage(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String result = jsonObject.getString("Status");
        if (result.equals("invalid address")) {
            Snackbar.make(getWindow().findViewById(R.id.content_main),
                    "Please enter valid address in the United States", Snackbar.LENGTH_LONG)
                    .show();
            return false;
        } else if (result.equals("address should be in US")) {
            Toast.makeText(getApplicationContext(), "Please enter valid address in the United States", Toast.LENGTH_LONG).show();

            return false;
        } else if ( result.equals("address too broad")) {
            Toast.makeText(getApplicationContext(), "Please enter specific address in the United States", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
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
