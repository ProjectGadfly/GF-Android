package com.forvm.gadfly.projectgadfly;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Script;
import com.forvm.gadfly.projectgadfly.data.Ticket;
import com.forvm.gadfly.projectgadfly.data.postScriptResponse;
import com.forvm.gadfly.projectgadfly.network.GadflyAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class NewScriptActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    ProgressDialog progressDialog;
    String titleS = "";
    String contentS = "";
    int fedOrState = 1;
    int repOrSen = 3;
    private FragmentManager fragmentManager;
    private CreateScriptFragment createScriptFragment;
    private ScriptSuccess scriptSuccess;
    private SharedPreferences pref;
    private JSONObject ticketJSONObject;

    private final static String BASE_URL = "http://63.142.250.185/services/v1/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);

        setContentView(R.layout.activity_new_script);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getFragmentManager();
        createScriptFragment = new CreateScriptFragment();

        fragmentManager.beginTransaction()
                .add(createScriptFragment, "BLANK")
                .replace(R.id.content_new_script, createScriptFragment)
                .commit();
    }

    private void postScript() {
        final EditText title = findViewById(R.id.scriptTitle);
        final EditText content = findViewById(R.id.scriptContent);
        contentS = content.getText().toString();
        titleS = title.getText().toString();
        progressDialog = new ProgressDialog(NewScriptActivity.this);
        progressDialog.setMessage("Posting Script");
        progressDialog.show();
        try {
            contentS = URLEncoder.encode(contentS, "UTF-8");
            titleS = URLEncoder.encode(titleS, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final RadioButton federal = findViewById(R.id.fedButton);
        final RadioButton senator = findViewById(R.id.senatorButton);
        final RadioButton rep = findViewById(R.id.repButton);
        final RadioButton state = findViewById(R.id.stateButton);
        if (federal.isChecked()) {
            fedOrState = 1;
        } else if (state.isChecked()) {
            fedOrState = 2;
        }
        if (senator.isChecked()) {
            repOrSen = 3;
        } else if (rep.isChecked()) {
            repOrSen = 4;
        }
        Script script = new Script();
        script.setTitle(titleS);
        script.setContent(contentS);
        ArrayList<Integer> scriptTags = new ArrayList<>();
        scriptTags.add(fedOrState);
        scriptTags.add(repOrSen);
        script.setTags(scriptTags);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GadflyAPI gadflyAPI = retrofit.create(GadflyAPI.class);

        Call<postScriptResponse> postScriptResponseCall = gadflyAPI.postScript(titleS, contentS, scriptTags);

        postScriptResponseCall.enqueue(new Callback<postScriptResponse>() {
            @Override
            public void onResponse(Call<postScriptResponse> call, Response<postScriptResponse> response) {
                if (response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equalsIgnoreCase("ok")) {
                        String scriptID = "";
                        String ticket = "";
                        scriptID = "http://63.142.250.185/services/v1/script?id=" + response.body().getId();
                        ticket = response.body().getTicket();
                        Bundle bundle = new Bundle();
                        Bitmap bitmap = null;
                        try {
                            bitmap = encodeAsBitmap(scriptID);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        try {
                            titleS = URLDecoder.decode(titleS, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

//TODO REPLACE TICKET SHARED PREF WITH DATABASE
                        final Ticket newTicket = new Ticket();
                        newTicket.setTitle(titleS);
                        newTicket.setTicket(ticket);
                        new Thread() {
                            @Override
                            public void run() {
                                AppDatabase.getAppDatabase(NewScriptActivity.this).ticketDAO().insertTicket(newTicket);
                            }
                        }.start();
                        bundle.putString("scriptTitle", titleS);
                        bundle.putByteArray("image", byteArray);
                        bundle.putString("scriptID", scriptID);
                        scriptSuccess = new ScriptSuccess();
                        scriptSuccess.setArguments(bundle);
                        progressDialog.dismiss();
                        fragmentManager.beginTransaction()
                                .add(scriptSuccess, "BLANK")
                                .replace(R.id.content_new_script, scriptSuccess)
                                .commit();
                    }
                } else {
                    Toast.makeText(NewScriptActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<postScriptResponse> call, Throwable t) {
                progressDialog.dismiss();
//                Snackbar.make(contentView, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });


//        new postScriptTask().execute("http://63.142.250.185/services/v1/script");

    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 1800, 1800, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 1800, 0, 0, w, h);
        return bitmap;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_script, menu);
        menu.findItem(R.id.action_share).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_submit) {
            if (checkValidScript()) {
                hideKeyboard();
                postScript();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean checkValidScript() {
        EditText title = (EditText) findViewById(R.id.scriptTitle);
        final EditText content = (EditText) findViewById(R.id.scriptContent);
        boolean emptyContent = TextUtils.isEmpty(content.getText());
        boolean emptyTitle = TextUtils.isEmpty(title.getText());
        if (emptyContent || emptyTitle) {
            if (emptyTitle) {
                Toast.makeText(getApplicationContext(), "Enter a title", Toast.LENGTH_LONG).show();
                return false;
            }
            if (emptyContent) {
                Toast.makeText(getApplicationContext(), "Content cannot be empty", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
        AboutFragment aboutFragment;
        //Handle the Home button
        if (id == R.id.homeView) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            //Handle the About button
        } else if (id == R.id.about) {
            aboutFragment = new AboutFragment();
            fragmentManager
                    .beginTransaction()
                    .add(aboutFragment, "ABOUTTAG")
                    .replace(R.id.content_new_script, aboutFragment)
                    .addToBackStack(null)
                    .commit();
            //Handle the Scripts button
        } else if (id == R.id.scripts) {
            TicketListFragment ticketListFragment = new TicketListFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_new_script, ticketListFragment)
                    .commit();
            //Handle the tutorial button
        } else if (id == R.id.tutorial) {
            Intent intent = new Intent(getApplicationContext(), Introduction.class);
            SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
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