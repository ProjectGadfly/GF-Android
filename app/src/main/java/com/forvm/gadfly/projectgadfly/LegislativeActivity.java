package com.forvm.gadfly.projectgadfly;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.forvm.gadfly.projectgadfly.adapter.RepresentativesRecyclerAdapter;
import com.forvm.gadfly.projectgadfly.data.Script;
import com.forvm.gadfly.projectgadfly.data.getScriptResponse;
import com.forvm.gadfly.projectgadfly.network.GadflyAPI;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    private com.getbase.floatingactionbutton.FloatingActionButton scanCodeFAB;
    private com.getbase.floatingactionbutton.FloatingActionButton createScriptFAB;
    private com.getbase.floatingactionbutton.FloatingActionButton searchScriptFAB;
    private RepresentativesRecyclerAdapter representativesRecyclerAdapter;
    private AboutFragment aboutFragment;
    private TicketListFragment ticketListFragment;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private RecyclerView recyclerView;

    private final static String BASE_URL = "http://63.142.250.185/services/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the layout of the activity based on legislator_activity layout
        setContentView(R.layout.legislator_activity);
        ButterKnife.bind(this);
        // Make the status bar transparent
        scanCodeFAB = findViewById(R.id.scanCodeFAB);
        mainFab = findViewById(R.id.mainFab);
        createScriptFAB = findViewById(R.id.createScriptFAB);
        searchScriptFAB = findViewById(R.id.searchScriptFAB);

        createScriptFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), NewScriptActivity.class);
//                startActivity(intent);
//                mainFab.collapse();
                NewScriptDialog newScriptDialog = new NewScriptDialog();
                newScriptDialog.show(getFragmentManager(), "NewScriptDialog");
                mainFab.collapse();
                mainFab.setVisibility(View.GONE);
            }
        });

        searchScriptFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchScriptActivity.class);
                startActivity(intent);
                mainFab.collapse();
            }
        });

        fragmentManager = getFragmentManager();

        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        //Set up the navigation bar of the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up the QR code scan in response to the scan button
        qrScan = new IntentIntegrator(this);

        scanCodeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
                mainFab.collapse();
            }
        });

        //Set up the navigation bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        legislatorParsing = new LegislatorParsing();
        fragmentManager.beginTransaction()
                .add(legislatorParsing, "BLANK")
                .replace(R.id.content_main, legislatorParsing)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aboutFragment = new AboutFragment();
        ticketListFragment = new TicketListFragment();
        recyclerView = legislatorParsing.getView().findViewById(R.id.repsRecycler);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mainFab.setVisibility(View.GONE);
                else if (dy < 0)
                    mainFab.setVisibility(View.VISIBLE);
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

    // Handle result of qr code scan
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        View contentView = getWindow().findViewById(R.id.content_main);
        if (scanningResult != null) {
            scanFormat = scanningResult.getFormatName();
            scanContent = scanningResult.getContents();
            String actualurl = "http://63.142.250.185/services/v1/script?id=";
            if (scanContent != null && scanContent.startsWith(actualurl)) {
                progressDialog = new ProgressDialog(LegislativeActivity.this);
                progressDialog.setMessage("Getting call script...");
                progressDialog.show();
                scanCode(scanContent);
            }
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.rescan_qr_code, Toast.LENGTH_LONG);
            toast.show();
            Snackbar.make(contentView, R.string.no_internet, Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void scanCode(final String scanContent) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GadflyAPI gadflyAPI = retrofit.create(GadflyAPI.class);
        String id = scanContent.split("=")[1];

        Call<getScriptResponse> getScriptResponseCall = gadflyAPI.getScript(Integer.parseInt(id));
        getScriptResponseCall.enqueue(new Callback<getScriptResponse>() {
            @Override
            public void onResponse(Call<getScriptResponse> call, Response<getScriptResponse> response) {
                if (response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equalsIgnoreCase("ok")) {
                        ScanResult scanResult = new ScanResult();
                        Bundle bundle = new Bundle();
                        Script script = response.body().getScript();
                        bundle.putSerializable("script", script);
                        scanResult.setArguments(bundle);
                        progressDialog.dismiss();
                        mainFab.setVisibility(View.GONE);
                        fragmentManager
                                .beginTransaction()
                                .add(scanResult, "SCANPAGE")
                                .replace(R.id.content_main, scanResult)
                                .commitAllowingStateLoss();
                    }
                }
            }

            @Override
            public void onFailure(Call<getScriptResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
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
        FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.homeView) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, legislatorParsing)
                    .commit();
            mainFab.setVisibility(View.VISIBLE);
        } else if (id == R.id.about) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, aboutFragment)
                    .commit();
            mainFab.setVisibility(View.GONE);
        } else if (id == R.id.scripts) {
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_main, ticketListFragment)
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
}