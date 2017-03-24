package com.example.gadfly.projectgadfly;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by papak on 3/22/2017.
 */

public class HomeFragment extends Fragment {
    public View v;
    public EditText edit;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//            super.onCreateView(inflater, container, savedInstanceState);
            v = inflater.inflate(R.layout.home_fragment, container, false);
//            edit = getEdit(v);
            return v;
        }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        edit = getEdit(v);
//        return v;
//    }
    public interface getText {
       EditText getEdit(View v);
}
//    public EditText getEdit(View v) { return (EditText) v.findViewById(R.id.addressfield);
//    }
}
