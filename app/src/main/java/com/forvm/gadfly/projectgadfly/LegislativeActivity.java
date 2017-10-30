package com.forvm.gadfly.projectgadfly;

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

import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

public class LegislativeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private IntentIntegrator qrScan;
    private LegislatorParsing legislatorParsing;
    private FragmentManager fragmentManager;
    SharedPreferences pref;
    private String scanFormat;
    private String scanContent;
    private ProgressDialog progressDialog;
    private FloatingActionsMenu mainFab;
    private com.getbase.floatingactionbutton.FloatingActionButton csFAB;
    private com.getbase.floatingactionbutton.FloatingActionButton qrFAB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make the status bar transparent (SDK 21 AND UP)
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        //Set the layout of the activity based on legislator_activity layout
        setContentView(R.layout.legislator_activity);

        // Make the status bar transparent
        changeStatusBarColor();

        mainFab = (FloatingActionsMenu) findViewById(R.id.mainFab);
        qrFAB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.qrFAB);
        csFAB = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.csFAB);


        csFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewScriptActivity.class);
                startActivity(intent);
                mainFab.collapse();
            }
        });

        fragmentManager = getSupportFragmentManager();

        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        //Set up the navigation bar of the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up the QR code scan in response to the scan button
        qrScan = new IntentIntegrator(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        //Set up the icon of the button
//        fab.setImageResource(R.drawable.ic_scan_icon);
//        fab.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//
//        //Initiate scan activity
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                qrScan.initiateScan();
//            }
//        });
        qrFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
                mainFab.collapse();
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
        navigationView.getMenu().getItem(0).setChecked(true);

        Bundle bundle = new Bundle();
        String existingJSON = pref.getString("json", "");
        String enteredAddress = pref.getString("address_field", "");

        String url = getString(R.string.get_reps_url);

        if (existingJSON.equalsIgnoreCase("")) {
            if (pref.getBoolean("have_address", false)) {
                url += enteredAddress;
            } else {
//                url = "https://openstates.org/api/v1/legislators/?state=dc&chamber=upper";
//                url = "https://api.myjson.com/bins/1bxuqf";
                //Error should never get here
                finish();
                Toast.makeText(this, "SAVE US", Toast.LENGTH_LONG).show();
            }
        }

        bundle.putString("json", existingJSON);
        bundle.putString("address", enteredAddress);

        legislatorParsing = new LegislatorParsing();
        legislatorParsing.setArguments(bundle);

        fragmentManager.beginTransaction()
                .add(legislatorParsing, "BLANK")
                .replace(R.id.content_main, legislatorParsing)
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

    // Handle result of qr code scan
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        View contentView = getWindow().findViewById(R.id.content_main);
        if (scanningResult != null) {
            scanFormat = scanningResult.getFormatName();
            scanContent = scanningResult.getContents();
            String actualurl = "http://gadfly.mobi/services/v1/script?id=";
            if (scanContent != null && scanContent.startsWith(actualurl)) {
                progressDialog = new ProgressDialog(LegislativeActivity.this);
                progressDialog.setMessage("Getting call scripts..");
                progressDialog.show();
//                new JsonTask().execute(getString(R.string.get_reps_url) );
                new JsonTask().execute(scanContent);
                return;
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.rescan_qr_code, Toast.LENGTH_LONG);
            toast.show();
            Snackbar.make(contentView, R.string.no_internet, Snackbar.LENGTH_LONG)
                    .show();
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
        int id = item.getItemId();

        if (id == R.id.homeView) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, legislatorParsing)
                    .commit();
        } else if (id == R.id.about) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, aboutFragment)
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




    private boolean isValidAddress(String jsonString) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonString);
        String result = jsonObject.getString("Status");
        if (result.equals(getString(R.string.server_invalid_address))) {
            Toast.makeText(getApplicationContext(), R.string.enter_valid_us_address, Toast.LENGTH_LONG).show();
            return false;
        } else if (result.equals(getString(R.string.server_no_us_address))) {
            Toast.makeText(getApplicationContext(), R.string.enter_valid_us_address, Toast.LENGTH_LONG).show();
            return false;
        } else if ( result.equals(getString(R.string.server_broad_address))) {
            Toast.makeText(getApplicationContext(), R.string.enter_specific_address, Toast.LENGTH_LONG).show();
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
                ScanResult scanResult = new ScanResult();
                Bundle bundle = new Bundle();
                bundle.putString("scanFormat", scanFormat);
                bundle.putString("scanContent", scanContent);
                scanResult.setArguments(bundle);
                fragmentManager
                        .beginTransaction()
                        .add(scanResult, "SCANPAGE")
                        .replace(R.id.content_main, scanResult)
                        .commitAllowingStateLoss();
            }
        }
    }


}