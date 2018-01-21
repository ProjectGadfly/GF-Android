package com.forvm.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DeleteScriptActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{


    private FragmentManager fragmentManager;
    private DeleteScript deleteScript;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_script);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        deleteScript = new DeleteScript();

        fragmentManager.beginTransaction()
                .add(deleteScript, "BLANK")
                .replace(R.id.content_new_script, deleteScript)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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


    public void deleteScript(View view) {
        EditText editText = (EditText) findViewById(R.id.deleteTicketNumber);
        String scriptTicket = editText.getText().toString();
        progressDialog = new ProgressDialog(DeleteScriptActivity.this);
        progressDialog.setMessage("Searching for Script");
        progressDialog.show();
        new JsonTask().execute("http://gadfly.mobi/services/v1/script?ticket="+scriptTicket);
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
                connection.setRequestMethod("DELETE");
                connection.setRequestProperty("APIKey", "v1key");
                connection.setConnectTimeout(5000);
                connection.connect();

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

//                    try {
//                        scriptID = jsonObject.getString("id");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Bundle bundle = new Bundle();
//                    scriptID = "http://gadfly.mobi/services/v1/script?id=" + scriptID;
//                    Bitmap bitmap = null;
//                    try {
//                        bitmap = encodeAsBitmap(scriptID);
//                    } catch (WriterException e) {
//                        e.printStackTrace();
//                    }
//                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                    byte[] byteArray = stream.toByteArray();
//                    try {
//                        titleS = URLDecoder.decode(titleS, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                    bundle.putString("scriptTitle", titleS);
//                    bundle.putByteArray("image", byteArray);
//                    bundle.putString("scriptID", scriptID);
//                    scriptSuccess = new ScriptSuccess();
//                    scriptSuccess.setArguments(bundle);
                    progressDialog.dismiss();
//                    fragmentManager.beginTransaction()
//                            .add(scriptSuccess, "BLANK")
//                            .replace(R.id.content_new_script, scriptSuccess)
//                            .commit();
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error deleting script. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

}
