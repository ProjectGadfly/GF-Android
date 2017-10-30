package com.forvm.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

import static android.R.attr.width;
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
    private CreateScript createScript;
    private ScriptSuccess scriptSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_script);
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
        createScript = new CreateScript();

        fragmentManager.beginTransaction()
                .add(createScript, "BLANK")
                .replace(R.id.content_new_script, createScript)
                .commit();
    }

    private void postScript() {
        final EditText title = (EditText) findViewById(R.id.scriptTitle);
        final EditText content = (EditText) findViewById(R.id.scriptContent);
        String textC = content.getText().toString();
        String textT = title.getText().toString();
        try {
            textC = URLEncoder.encode(content.getText().toString(), "UTF-8");
            textT = URLEncoder.encode(title.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        content.setText(textC);
        title.setText(textT);
        final RadioButton federal = (RadioButton) findViewById(R.id.fedButton);
        final RadioButton senator = (RadioButton) findViewById(R.id.senatorButton);
        final RadioButton rep = (RadioButton) findViewById(R.id.repButton);
        final RadioButton state = (RadioButton) findViewById(R.id.stateButton);
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
        progressDialog = new ProgressDialog(NewScriptActivity.this);
        progressDialog.setMessage("Posting Script");
        progressDialog.show();
        contentS = content.getText().toString();
        titleS = title.getText().toString();
        new JsonTask().execute("http://gadfly.mobi/services/v1/script");

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

        //noinspection SimplifiableIfStatement
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
        boolean emptyContent = content.getText().toString().isEmpty();
        boolean emptyTitle = title.getText().toString().isEmpty();
        if (emptyContent || emptyTitle) {
            if (emptyTitle) {
                Toast.makeText(getApplicationContext(),"Enter a title",Toast.LENGTH_LONG).show();
                return false;
            } else if (emptyContent) {
                Toast.makeText(getApplicationContext(),"Content cannot be empty",Toast.LENGTH_LONG).show();
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
        FragmentManager fragmentManager = getSupportFragmentManager();
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
                    .replace(R.id.content_main, aboutFragment)
                    .addToBackStack(null)
                    .commit();
            //Handle the Tutorial button
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
                connection.setRequestMethod("POST");
                connection.setRequestProperty("APIKey", "v1key");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(5000);
                connection.connect();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                String write = "title=" + titleS + "&"  +
                        "content=" + contentS + "&" + "tags=" + fedOrState + "&" + "tags=" + repOrSen;
                writer.write(write);
                writer.flush();
                writer.close();
                InputStream stream = new BufferedInputStream(connection.getInputStream());
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();
                reader.close();
                return jsonString;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String scriptID = "";
            String status = "";
            Toast.makeText(getApplicationContext(), jsonString, Toast.LENGTH_LONG);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(jsonString);
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status.equalsIgnoreCase("ok")) {
                if (progressDialog.isShowing()) {
                    try {
                        scriptID = jsonObject.getString("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Bundle bundle = new Bundle();
                    scriptID = "http://gadfly.mobi/services/v1/script?id=" + scriptID;
                    Bitmap bitmap = null;
                    try {
                        bitmap = encodeAsBitmap(scriptID);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error getting data. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
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
}