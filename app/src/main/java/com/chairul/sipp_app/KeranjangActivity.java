package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVKeranjangAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapterHorizontal;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.model.KeranjangModel;
import com.chairul.sipp_app.model.LapakModel;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class KeranjangActivity extends AppCompatActivity {
    private static final String TAG = KeranjangActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_HASIL = "hasil";
    private static final String TAG_KERANJANG = "keranjang";

    private ScrollView scrollView;
    private TextView txtNamaMitra, txtNoRek, txtTotal, txtKosong;
    private Button btnSimpan;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;

    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<KeranjangModel> keranjangModelList;

    int success, total;
    String nama_mitra, norek_mitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keranjang);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        txtNamaMitra = (TextView) findViewById(R.id.txtKeranjangMitra);
        txtNoRek = (TextView) findViewById(R.id.txtKeranjangNorek);
        txtTotal = (TextView) findViewById(R.id.txtKeranjangTotal);
        txtKosong = (TextView) findViewById(R.id.txtKeranjangKosong);
        btnSimpan = (Button) findViewById(R.id.btnKeranjangBayar);
        scrollView = (ScrollView) findViewById(R.id.scrollView3);

        mRecyclerView = (RecyclerView)findViewById(R.id.rvKeranjang);
        keranjangModelList = new ArrayList<>();

        initRV();
    }

    private void initRV(){
        adapter = new RVKeranjangAdapter(KeranjangActivity.this, getApplicationContext(), keranjangModelList);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getDataKeranjang();
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(KeranjangActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , KeranjangActivity.this);
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

    private void getDataKeranjang() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getKeranjang(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        txtKosong.setVisibility(View.GONE);
                        scrollView.setVisibility(View.VISIBLE);

                        total = jObj.getInt("total");
                        nama_mitra = jObj.getString("nama_mitra");
                        norek_mitra = jObj.getString("norek_mitra");

                        txtTotal.setText(konversiRupiah(total));
                        txtNamaMitra.setText(nama_mitra);
                        txtNoRek.setText(norek_mitra);

                        keranjangModelList.clear();

                        String id_lapak = "";

                        JSONArray keranjang = jObj.getJSONArray(TAG_KERANJANG);

                        for (int i = 0; i < keranjang.length(); i++) {
                            try {
                                JSONObject jsonObject = keranjang.getJSONObject(i);

                                KeranjangModel keranjangModel = new KeranjangModel();
                                keranjangModel.setIdKeranjang(jsonObject.getString("id_keranjang"));
                                keranjangModel.setIdUsers(jsonObject.getString("id_users"));
                                keranjangModel.setIdLapak(jsonObject.getString("id_lapak"));
                                keranjangModel.setTanggalPakai(jsonObject.getString("tglpakai"));
                                keranjangModel.setKeterangan(jsonObject.getString("keterangan"));
                                keranjangModel.setJumlah(jsonObject.getString("jumlah"));
                                keranjangModel.setSubTotal(jsonObject.getString("sub_total"));
                                keranjangModel.setNamaUsers(jsonObject.getString("nama_users"));
                                keranjangModel.setNamaLapak(jsonObject.getString("nama_lapak"));

                                id_lapak = jsonObject.getString("id_lapak");

                                keranjangModelList.add(keranjangModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                        final String finalId_lapak = id_lapak;
                        btnSimpan.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                prosesPesanan(finalId_lapak);
                            }
                        });

                    } else {
                        scrollView.setVisibility(View.GONE);
                        txtKosong.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_KERANJANG), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_users", sessionAdapter.getId());

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void prosesPesanan(final String id_lapak) {
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().simpanTransaksi(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_HASIL), Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(KeranjangActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_HASIL), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_users", sessionAdapter.getId());
                params.put("id_lapak", id_lapak);

                return params;
            }

        };

        strReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    public void onBackPressed(){
        startActivity(new Intent(KeranjangActivity.this, BerandaActivity.class));
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}