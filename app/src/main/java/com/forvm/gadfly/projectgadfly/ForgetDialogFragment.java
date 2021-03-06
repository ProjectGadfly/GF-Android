package com.forvm.gadfly.projectgadfly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.forvm.gadfly.projectgadfly.data.AppDatabase;
import com.forvm.gadfly.projectgadfly.data.Representative;

public class ForgetDialogFragment extends DialogFragment {

    //Create a dialog for users to forget previous address input
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity())
                // Set title and message of the Dialog
                .setTitle("Forget Me")
                .setMessage("This will delete your stored address. Do you wish to continue?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread() {
                            @Override
                            public void run() {
                                for (Representative rep : AppDatabase.getAppDatabase(getContext()).representativeDAO().getAll()) {
                                    AppDatabase.getAppDatabase(getContext()).representativeDAO().delete(rep);
                                }
//                                AppDatabase.getAppDatabase(getContext()).clearAllTables();
                            }
                        }.start();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        SharedPreferences pref = getContext().getSharedPreferences("ActivityPREF", MainActivity.MODE_PRIVATE);
                        SharedPreferences.Editor ed = pref.edit();
                        ed.putBoolean("have_reps", false);
                        ed.apply();
                        startActivity(intent);
                        getActivity().finish();
                    }})
                //Set Cancel button response
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}});

        return alertDialogBuilder.create();
    }

}