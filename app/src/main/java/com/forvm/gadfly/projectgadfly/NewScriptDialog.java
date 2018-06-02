package com.forvm.gadfly.projectgadfly;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class NewScriptDialog extends DialogFragment {

    private EditText etScriptTitle;
    private EditText etScriptContent;
    private RadioButton federal;
    private RadioButton senator;
    private RadioButton rep;
    private RadioButton state;
    private ScriptSuccess scriptSuccess;
    private FragmentManager fragmentManager;
    private int fedOrState = 1;
    private int repOrSen = 3;
    private final static String BASE_URL = "http://63.142.250.185/services/v1/";
    private Context context;
    public NewScriptDialog() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create New Script");
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_create_script, null);
        fragmentManager = getFragmentManager();
        etScriptTitle = rootView.findViewById(R.id.scriptTitle);
        etScriptContent = rootView.findViewById(R.id.scriptContent);
        federal = rootView.findViewById(R.id.fedButton);
        senator = rootView.findViewById(R.id.senatorButton);
        rep = rootView.findViewById(R.id.repButton);
        state = rootView.findViewById(R.id.stateButton);
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

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etScriptTitle.getText())) {
                        etScriptTitle.setError(getString(R.string.empty_field_error));
                    } else if (TextUtils.isEmpty(etScriptContent.getText())) {
                        etScriptContent.setError(getString(R.string.empty_field_error));
                    } else {
                        postScript();
                        d.dismiss();
                    }
                }
            });
        }
    }

    public void postScript() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Posting Script");
        progressDialog.show();
        Script script = new Script();
        final String titleString = etScriptTitle.getText().toString();
        String contentString = etScriptContent.getText().toString();
        script.setTitle(titleString);
        script.setContent(contentString);
        ArrayList<Integer> scriptTags = new ArrayList<>();
        scriptTags.add(fedOrState);
        scriptTags.add(repOrSen);
        script.setTags(scriptTags);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final GadflyAPI gadflyAPI = retrofit.create(GadflyAPI.class);

        Call<postScriptResponse> postScriptResponseCall = gadflyAPI.postScript(titleString, contentString, scriptTags);

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

                        final Ticket newTicket = new Ticket();
                        newTicket.setTitle(titleString);
                        newTicket.setTicket(ticket);
                        new Thread() {
                            @Override
                            public void run() {
                                AppDatabase.getAppDatabase(context).ticketDAO().insertTicket(newTicket);
                            }
                        }.start();
                        bundle.putString("scriptTitle", titleString);
                        bundle.putByteArray("image", byteArray);
                        bundle.putString("scriptID", scriptID);
                        scriptSuccess = new ScriptSuccess();
                        scriptSuccess.setArguments(bundle);
                        progressDialog.dismiss();
                        fragmentManager.beginTransaction()
                                .add(scriptSuccess, "BLANK")
                                .replace(R.id.content_main, scriptSuccess)
                                .commit();
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<postScriptResponse> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(getView(), t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
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
