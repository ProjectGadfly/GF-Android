package com.forvm.gadfly.projectgadfly;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import org.w3c.dom.Text;


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
    private ShareActionProvider mShareActionProvider;

    private TextView textView;
    private ImageView imageView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ScriptSuccess() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String scriptID = getArguments().getString("scriptID");
        byte[] byteArray = getArguments().getByteArray("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        View v = inflater.inflate(R.layout.fragment_script_success, container, false);
        textView = (TextView) v.findViewById(R.id.SHOWSCRIPT);
        imageView = (ImageView) v.findViewById(R.id.QRCODE);
        imageView.setImageBitmap(bmp);
        textView.setText(scriptID);
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
        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                textView.getText());
        mShareActionProvider.setShareIntent(shareIntent);
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {

        }

        return super.onOptionsItemSelected(item);
    }

}