package com.forvm.gadfly.projectgadfly;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        TextView textView = (TextView) v.findViewById(R.id.SHOWSCRIPT);
        ImageView imageView= (ImageView) v.findViewById(R.id.QRCODE);
        imageView.setImageBitmap(bmp);
        textView.setText(scriptID);
        return v;
    }

}
