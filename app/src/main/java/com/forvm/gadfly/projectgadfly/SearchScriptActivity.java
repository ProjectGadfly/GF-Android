package com.forvm.gadfly.projectgadfly;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class SearchScriptActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


     FragmentManager fragmentManager;
     DeleteScript deleteScript;
     String scriptID = "";
     String scriptTitle = "";
     ScriptSuccess scriptSuccess;
     Bundle bundle;
     ProgressDialog progressDialog;

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
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getSupportFragmentManager();
        deleteScript = new DeleteScript();

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();

        deleteScript.setArguments(bundle);
        fragmentManager.beginTransaction()
                .add(deleteScript, "BLANK")
                .replace(R.id.content_new_script, deleteScript)
                .commit();


    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getIntent().getExtras();
        String ticket = bundle.getString("ticket");
        if (ticket != null) {
            EditText editText = (EditText) findViewById(R.id.deleteTicketNumber);
            editText.setText(ticket);
            Button button = (Button) findViewById(R.id.searchScriptButton);
            searchScript(button);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        AboutFragment aboutFragment;
        //Handle the Home button
        if (id == R.id.homeView) {
            Intent intent = new Intent(this, MainActivity.class);
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
            Intent intent = new Intent(this, Introduction.class);
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
        new AlertDialog.Builder(this)
                .setTitle("Delete Script?")
                .setMessage("Are you sure you want to delete this script?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText editText = (EditText) findViewById(R.id.deleteTicketNumber);
                        String scriptTicket = editText.getText().toString();
                        progressDialog = new ProgressDialog(SearchScriptActivity.this);
                        progressDialog.setMessage("Deleting Script");
                        progressDialog.show();
                        new deleteTicketTask().execute("http://gadfly.mobi/services/v1/script?ticket="+scriptTicket);
                    }})
                .setNegativeButton(android.R.string.no, null).show();
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

     String deleteTicketJSON;

    /**
     * Parsing Json AsyncTask
     */
     class deleteTicketTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader;
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
                deleteTicketJSON = builder.toString();
                reader.close();
                return deleteTicketJSON;
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
            String status = "";
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(deleteTicketJSON);
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status.equalsIgnoreCase("ok")) {
                if (progressDialog.isShowing()) {
                    LinearLayout scriptDisplayLayout = (LinearLayout) findViewById(R.id.displayScript);
                    LinearLayout scriptFormLayout = (LinearLayout) findViewById(R.id.deleteScriptForm);

                    LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.0f
                    );
                    scriptDisplayLayout.setLayoutParams(displayParams);

                    LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    scriptFormLayout.setLayoutParams(formParams);

                    Button shareButton = (Button) findViewById(R.id.shareScriptButton);
                    Button deleteButton = (Button) findViewById(R.id.deleteScriptButton);
                    shareButton.setVisibility(View.INVISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                    hideKeyboard();

                    TextView titleDisplay = (TextView) findViewById(R.id.displayTitle);
                    TextView contentDisplay = (TextView) findViewById(R.id.displayContent);
                    titleDisplay.setText("");
                    contentDisplay.setText("");
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Script successfully deleted.", Toast.LENGTH_LONG).show();
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error deleting script. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    String getIDJSON;
    /**
     * Parsing Json AsyncTask
     */
    class getIDTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
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
                getIDJSON = builder.toString();
                reader.close();
                return getIDJSON;
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
            String status = "";
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(getIDJSON);
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status.equalsIgnoreCase("ok")) {
                try {
                    scriptID = jsonObject.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new getScriptTask().execute("http://gadfly.mobi/services/v1/script?id="+scriptID);
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error deleting script. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

     void hideKeyboard() {
//        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }


     String getScriptJSON;
    /**
     * Parsing Json AsyncTask
     */
     class getScriptTask extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
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
                getScriptJSON = builder.toString();
                reader.close();
                return getScriptJSON;
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
            String scriptContent = "";
            String status = "";
            JSONObject jsonObject = null;
            JSONObject scriptJson = null;
            try {
                jsonObject = new JSONObject(getScriptJSON);
                status = jsonObject.getString("Status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (status.equalsIgnoreCase("ok")) {
                if (progressDialog.isShowing()) {
                    try {
                        scriptJson = jsonObject.getJSONObject("Script");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        scriptTitle = scriptJson.getString("title");
                        scriptContent = scriptJson.getString("content");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );


                    Button shareButton = (Button) findViewById(R.id.shareScriptButton);
                    Button deleteButton = (Button) findViewById(R.id.deleteScriptButton);

                    LinearLayout scriptDisplayLayout = (LinearLayout) findViewById(R.id.displayScript);
                    LinearLayout scriptFormLayout = (LinearLayout) findViewById(R.id.deleteScriptForm);

                    scriptDisplayLayout.setLayoutParams(displayParams);

                    LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.0f
                    );
                    scriptFormLayout.setLayoutParams(formParams);

                    shareButton.setVisibility(View.VISIBLE);
                    deleteButton.setVisibility(View.VISIBLE);
                    hideKeyboard();

                    TextView titleDisplay = (TextView) findViewById(R.id.displayTitle);
                    TextView contentDisplay = (TextView) findViewById(R.id.displayContent);
                    titleDisplay.setText(scriptTitle);
                    contentDisplay.setText(scriptContent);
                    progressDialog.dismiss();
                }
            } else {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Error deleting script. Please try again later.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    public void searchScript(View view) {

        LinearLayout scriptDisplayLayout = (LinearLayout) findViewById(R.id.displayScript);
        LinearLayout scriptFormLayout = (LinearLayout) findViewById(R.id.deleteScriptForm);

        LinearLayout.LayoutParams displayParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                0.0f
        );
        scriptDisplayLayout.setLayoutParams(displayParams);

        LinearLayout.LayoutParams formParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        scriptFormLayout.setLayoutParams(formParams);

        Button shareButton = (Button) findViewById(R.id.shareScriptButton);
        Button deleteButton = (Button) findViewById(R.id.deleteScriptButton);
        shareButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        hideKeyboard();

        TextView titleDisplay = (TextView) findViewById(R.id.displayTitle);
        TextView contentDisplay = (TextView) findViewById(R.id.displayContent);
        titleDisplay.setText("");
        contentDisplay.setText("");

        EditText editText = (EditText) findViewById(R.id.deleteTicketNumber);
        String scriptTicket = editText.getText().toString();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching for Script");
        progressDialog.show();
        new getIDTask().execute("http://gadfly.mobi/services/v1/id?ticket="+scriptTicket);
    }

    public void shareScript(View view) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating QR code...");
        progressDialog.show();

        scriptID = "http://gadfly.mobi/services/v1/script?id=" +scriptID;
        Bitmap bitmap = null;
        try {
            bitmap = encodeAsBitmap(scriptID);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        File cachePath = new File(this.getCacheDir(), "images");
        cachePath.mkdirs(); // don't forget to make the directory
        FileOutputStream stream ; // overwrites this image every time
        try {
            stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                scriptID);
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, "com.forvm.gadfly.fileprovider", newFile);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, this.getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(shareIntent,"Share via"));

        progressDialog.dismiss();
    }

}
