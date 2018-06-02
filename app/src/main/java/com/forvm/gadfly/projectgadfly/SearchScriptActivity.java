package com.forvm.gadfly.projectgadfly;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
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

import com.forvm.gadfly.projectgadfly.adapter.TicketRecyclerAdapter;
import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Script;
import com.forvm.gadfly.projectgadfly.data.Ticket;
import com.forvm.gadfly.projectgadfly.data.deleteTicketResponse;
import com.forvm.gadfly.projectgadfly.data.getScriptIDResponse;
import com.forvm.gadfly.projectgadfly.data.getScriptResponse;
import com.forvm.gadfly.projectgadfly.network.GadflyAPI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class SearchScriptActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private FragmentManager fragmentManager;
    private SearchScriptFragment searchScriptFragment;
    private String scriptID = "";
    private String scriptTitle = "";
    private ScriptSuccess scriptSuccess;
    private Bundle bundle;
    private ProgressDialog progressDialog;
    private String scriptTicket = "";
    private String scriptContent = "";
    private Retrofit retrofit;
    private GadflyAPI gadflyAPI;
    private Button shareButton;
    private Button deleteButton;
    private TextView titleDisplay;
    private TextView contentDisplay;
    private Button searchScriptButton;
    private LinearLayout scriptDisplayLayout;
    private LinearLayout scriptActionLayout;
    private EditText etSearchTicketID;
    private List<Ticket> tickets;
    private TicketRecyclerAdapter ticketRecyclerAdapter;
    private static final String BASE_URL = "http://63.142.250.185/services/v1/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_script);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager = getFragmentManager();
        searchScriptFragment = new SearchScriptFragment();

        fragmentManager.beginTransaction()
                .add(searchScriptFragment, "BLANK")
                .replace(R.id.content_new_script, searchScriptFragment)
                .commit();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gadflyAPI = retrofit.create(GadflyAPI.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        new Thread() {
            @Override
            public void run() {
                tickets = AppDatabase.getAppDatabase(SearchScriptActivity.this).ticketDAO().getAll();
                ticketRecyclerAdapter = new TicketRecyclerAdapter(tickets, SearchScriptActivity.this);
            }
        }.start();
        searchScriptButton = findViewById(R.id.searchScriptButton);
        if (getIntent().hasExtra("ticket")) {
            Bundle bundle = getIntent().getExtras();
            String ticket = bundle.getString("ticket");
            etSearchTicketID = findViewById(R.id.searchTicketID);
            if (ticket != null) {
                etSearchTicketID.setText(ticket);
                searchScript();
            }
        }
        searchScriptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchScript();
            }
        });
        shareButton = findViewById(R.id.shareScriptButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareScript();
            }
        });
        deleteButton = findViewById(R.id.deleteScriptButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteScript();
            }
        });
        titleDisplay = findViewById(R.id.displayTitle);
        contentDisplay = findViewById(R.id.displayContent);
        scriptDisplayLayout = findViewById(R.id.displayScriptLayout);
        scriptActionLayout = findViewById(R.id.scriptActionLayout);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getFragmentManager();
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
                    .replace(R.id.main_activity_layout, aboutFragment)
                    .addToBackStack(null)
                    .commit();
            //Handle the Script button
        } else if (id == R.id.scripts) {
            TicketListFragment ticketListFragment = new TicketListFragment();
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_activity_layout, ticketListFragment)
                    .commit();
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

    public void deleteScript() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Script?")
                .setMessage("Are you sure you want to delete this script?")
                .setIcon(R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        scriptTicket = etSearchTicketID.getText().toString();
                        progressDialog = new ProgressDialog(SearchScriptActivity.this);
                        progressDialog.setMessage("Deleting Script");
                        progressDialog.show();
                        Call<deleteTicketResponse> deleteTicketResponseCall = gadflyAPI.deleteTicket(scriptTicket);
                        deleteTicketResponseCall.enqueue(new Callback<deleteTicketResponse>() {
                            @Override
                            public void onResponse(Call<deleteTicketResponse> call, Response<deleteTicketResponse> response) {
                                if (response.body() != null) {
                                    String status = response.body().getStatus();
                                    if (status.equalsIgnoreCase("ok")) {
                                        for (final Ticket ticket : tickets) {
                                            if (ticket.getTicket().equals(scriptTicket)) {
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        AppDatabase.getAppDatabase(SearchScriptActivity.this).ticketDAO().delete(ticket);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                ticketRecyclerAdapter.deleteTicket(ticket);
                                                            }
                                                        });
                                                    }
                                                }.start();
                                            }
                                        }
                                    }
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(Call<deleteTicketResponse> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(SearchScriptActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
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

    public void searchScript() {
        scriptTicket = etSearchTicketID.getText().toString();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Searching for Script");
        progressDialog.show();

        Call<getScriptIDResponse> getScriptIDResponseCall = gadflyAPI.getScriptID(scriptTicket);

        getScriptIDResponseCall.enqueue(new Callback<getScriptIDResponse>() {
            @Override
            public void onResponse(Call<getScriptIDResponse> call, Response<getScriptIDResponse> response) {
                if (response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equalsIgnoreCase("ok")) {
                        getScript(response.body().getId());
                    } else {
                        Toast.makeText(SearchScriptActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<getScriptIDResponse> call, Throwable t) {
                Toast.makeText(SearchScriptActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void getScript(Integer scriptID) {
        Call<getScriptResponse> getScriptResponseCall = gadflyAPI.getScript(scriptID);

        getScriptResponseCall.enqueue(new Callback<getScriptResponse>() {
            @Override
            public void onResponse(Call<getScriptResponse> call, Response<getScriptResponse> response) {
                if (response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equalsIgnoreCase("ok")) {
                        Script script = response.body().getScript();
                        titleDisplay.setText(script.getTitle());
                        contentDisplay.setText(script.getContent());
                        scriptDisplayLayout.setVisibility(View.VISIBLE);
                        scriptActionLayout.setVisibility(View.VISIBLE);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<getScriptResponse> call, Throwable t) {
                Toast.makeText(SearchScriptActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void shareScript() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Generating QR code...");
        progressDialog.show();

        scriptID = BASE_URL + "script?id=" + scriptID;
        Bitmap bitmap = null;
        try {
            bitmap = encodeAsBitmap(scriptID);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        File cachePath = new File(this.getCacheDir(), "images");
        cachePath.mkdirs(); // don't forget to make the directory
        FileOutputStream stream; // overwrites this image every time
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
        startActivity(Intent.createChooser(shareIntent, "Share via"));
        progressDialog.dismiss();
    }
}
