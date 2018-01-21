package com.forvm.gadfly.projectgadfly;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * A simple {atsymbollink Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {atsymbollink ScriptSuccess.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {atsymbollink ScriptSuccess#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScriptSuccess extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textView;
    private ImageView imageView;
    private Bitmap bmp;
//    private Bitmap editBitmap;
    private String scriptID;
    private String scriptTitle;

    public ScriptSuccess() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ProgressDialog pd = new ProgressDialog(getContext());
        pd.setMessage("Please wait");
        pd.setCancelable(false);
        pd.show();

        // Inflate the layout for this fragment
        scriptID = getArguments().getString("scriptID");
        scriptTitle = getArguments().getString("scriptTitle");
        byte[] byteArray = getArguments().getByteArray("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        View v = inflater.inflate(R.layout.fragment_script_success, container, false);
        try {

            File cachePath = new File(getContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time

            float scale = getContext().getResources().getDisplayMetrics().density;
            android.graphics.Bitmap.Config bitmapConfig =   bmp.getConfig();
            // set default bitmap config if none
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            // resource bitmaps are immutable,
            // so we need to convert it to mutable one
            bmp = bmp.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bmp);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            // text size in pixels
            paint.setTextSize((int) (35 * scale));

            // draw text to the Canvas center

            canvas.drawText(scriptTitle, 40 * scale, 40 * scale, paint);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        textView = (TextView) v.findViewById(R.id.SHOWSCRIPT);
        imageView = (ImageView) v.findViewById(R.id.QRCODE);
        imageView.setImageBitmap(bmp);
        pd.dismiss();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_submit).setVisible(false);
        MenuItem shareItem = menu.findItem(R.id.action_share);
        shareItem.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            shareImage();
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareImage() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                scriptID);
        File imagePath = new File(getContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getContext(), "com.forvm.gadfly.fileprovider", newFile);

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
        shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        startActivity(Intent.createChooser(shareIntent,"Share via"));
    }
}