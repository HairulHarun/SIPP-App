package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.model.KategoriModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TambahLapakActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_HASIL = "hasil";
    private static final String TAG_KATEGORI = "kategori";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private Spinner spinnerKategori;
    private ProgressDialog pDialog;
    private Button btnSimpan;
    private EditText txtNama, txtStok, txtHarga, txtDetail;

    private ArrayList<String> listKategori;
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_lapak);

        spinnerKategori = (Spinner) findViewById(R.id.spinner_kategori);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        txtNama = (EditText) findViewById(R.id.txt_nama);
        txtStok = (EditText) findViewById(R.id.txt_stok);
        txtHarga = (EditText) findViewById(R.id.txt_harga);
        txtDetail = (EditText) findViewById(R.id.txt_detail);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(
                getApplicationContext());

        listKategori = new ArrayList<String>();

        Dexter.withActivity(TambahLapakActivity.this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getKategori("");
                                btnSimpan.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (spinnerKategori.getSelectedItem().toString().equals("")){
                                            Toast.makeText(getApplicationContext(), "Silahkan pilih kategori !", Toast.LENGTH_SHORT).show();
                                        }else{
                                            String currentString = spinnerKategori.getSelectedItem().toString();
                                            String[] separated = currentString.split("/");
                                            String id_kategori = separated[0];

                                            simpanLapak(id_kategori, txtNama.getText().toString(),
                                                    txtDetail.getText().toString(),
                                                    txtStok.getText().toString(),
                                                    txtHarga.getText().toString());
                                        }
                                    }
                                });
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(TambahLapakActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , TambahLapakActivity.this);
                            }

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void getKategori(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getKategori(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray(TAG_KATEGORI);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                listKategori.add(obj.getString("id_kategori")+"/"+obj.getString("nama_kategori"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        spinnerKategori.setAdapter(new ArrayAdapter<String>(TambahLapakActivity.this, android.R.layout.simple_spinner_dropdown_item, listKategori));

                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_KATEGORI), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void simpanLapak(final String id_kategori, final String nama, final String detail, final String stok, final String harga) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Menyimpan Data ...");
        showDialog();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().simpanLapak(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_HASIL), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Json Error: " + jObj.getString(TAG_HASIL), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_mitra", sessionAdapter.getId());
                params.put("id_kategori", id_kategori);
                params.put("nama", nama);
                params.put("detail", detail);
                params.put("stok", stok);
                params.put("harga", harga);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TambahLapakActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}