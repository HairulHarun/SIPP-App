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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVKeranjangAdapter;
import com.chairul.sipp_app.adapter.RVTransaksiDetailAdapter;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.model.KeranjangModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransaksiDetailActivity extends AppCompatActivity {
    private static final String TAG = TransaksiDetailActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_HASIL = "hasil";
    private static final String TAG_TRANSAKSI = "transaksi";

    private ScrollView scrollView;
    private TextView txtNamaMitra, txtNoRek, txtStatus, txtTotal, txtPenjelasan, txtPenjelasanMitra, txtKetBukti;
    private Button btnUpload, btnTerima, btnTolak;
    private ImageView imgBukti;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;

    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<KeranjangModel> keranjangModelList;

    int success, total;
    String nama_mitra, norek_mitra, status_transaksi;

    private Intent intent;
    private String intent_id, intent_idlapak, intent_idusers, intent_idmitra, intent_kode, intent_namamitra, intent_bukti, intent_status, intent_subtotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_detail);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        intent = getIntent();
        intent_id = intent.getStringExtra("id");
        intent_idlapak = intent.getStringExtra("id_lapak");
        intent_idusers = intent.getStringExtra("id_users");
        intent_idmitra = intent.getStringExtra("id_mitra");
        intent_kode = intent.getStringExtra("kode");
        intent_namamitra = intent.getStringExtra("nama_mitra");
        intent_bukti = intent.getStringExtra("bukti");
        intent_status = intent.getStringExtra("status");
        intent_subtotal = intent.getStringExtra("sub_total");

        txtNamaMitra = (TextView) findViewById(R.id.txtTransaksiDetailNamaMitra);
        txtNoRek = (TextView) findViewById(R.id.txtTransaksiDetailNorek);
        txtTotal = (TextView) findViewById(R.id.txtTransaksiDetailTotal);
        txtStatus = (TextView) findViewById(R.id.txtTransaksiDetailStatus);
        txtPenjelasan = (TextView) findViewById(R.id.txtTransaksiDetailPenjelasan);
        txtPenjelasanMitra = (TextView) findViewById(R.id.txtTransaksiDetailPenjelasanMitra);
        txtKetBukti = (TextView) findViewById(R.id.txtTransaksiDetailBukti);
        btnUpload = (Button) findViewById(R.id.btnTransaksiDetailUpload);
        btnTolak = (Button) findViewById(R.id.btnTransaksiDetailVerifikasi1);
        btnTerima = (Button) findViewById(R.id.btnTransaksiDetailVerifikasi2);
        imgBukti = (ImageView) findViewById(R.id.imgTransaksiDetailBukti);
        scrollView = (ScrollView) findViewById(R.id.scrollView3);

        mRecyclerView = (RecyclerView)findViewById(R.id.rvTransaksiDetail);
        keranjangModelList = new ArrayList<>();

        initRV();

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransaksiDetailActivity.this, UploadBuktiActivity.class);
                intent.putExtra("kode", intent_kode);
                startActivity(intent);
            }
        });
    }

    private void initRV(){
        adapter = new RVTransaksiDetailAdapter(TransaksiDetailActivity.this, getApplicationContext(), keranjangModelList);
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
                                getDataTransaksiDetail(intent_kode);
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(TransaksiDetailActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , TransaksiDetailActivity.this);
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

    private void getDataTransaksiDetail(final String kode) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getTransaksiUsersDetail(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        scrollView.setVisibility(View.VISIBLE);

                        total = jObj.getInt("total");
                        nama_mitra = jObj.getString("nama_mitra");
                        norek_mitra = jObj.getString("norek_mitra");
                        status_transaksi = jObj.getString("status_transaksi");

                        txtTotal.setText("Rp. "+String.valueOf(total));
                        txtNamaMitra.setText(nama_mitra);
                        txtNoRek.setText(norek_mitra);
                        txtStatus.setText(status_transaksi);

                        if (sessionAdapter.getStatus().equals("Mitra")){
                            if (status_transaksi.equals("Menunggu Pembayaran")){
                                txtPenjelasan.setVisibility(View.GONE);
                                btnUpload.setVisibility(View.GONE);
                                txtKetBukti.setVisibility(View.GONE);
                                imgBukti.setVisibility(View.GONE);

                                txtPenjelasanMitra.setVisibility(View.GONE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);

                            }else if (status_transaksi.equals("Menunggu Verifikasi")){
                                txtPenjelasan.setVisibility(View.GONE);
                                btnUpload.setVisibility(View.GONE);
                                txtKetBukti.setVisibility(View.GONE);
                                imgBukti.setVisibility(View.VISIBLE);

                                Picasso.with(TransaksiDetailActivity.this)
                                        .load(new URLAdapter().getPhotoBuktiPembayaran()+intent_bukti)
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round)
                                        .into(imgBukti);

                                txtPenjelasanMitra.setVisibility(View.VISIBLE);
                                btnTerima.setVisibility(View.VISIBLE);
                                btnTolak.setVisibility(View.VISIBLE);

                                btnTerima.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        updateStatus(intent_kode, "Pembayaran Di Verifikasi");
                                    }
                                });

                                btnTolak.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        updateStatus(intent_kode, "Pembayaran Di Tolak");
                                    }
                                });

                            }else if (status_transaksi.equals("Pembayaran Di Verifikasi")){
                                txtPenjelasan.setVisibility(View.GONE);
                                btnUpload.setVisibility(View.GONE);
                                txtKetBukti.setVisibility(View.GONE);
                                imgBukti.setVisibility(View.VISIBLE);

                                txtPenjelasanMitra.setVisibility(View.VISIBLE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);

                                Picasso.with(TransaksiDetailActivity.this)
                                        .load(new URLAdapter().getPhotoBuktiPembayaran()+intent_bukti)
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round)
                                        .into(imgBukti);

                                txtPenjelasanMitra.setText("Pesanan ini sudah di verifikasi, Silahkan Hub. User Untuk Kepastian Pesanan");

                            }else if (status_transaksi.equals("Pembayaran Di Tolak")){
                                txtPenjelasan.setVisibility(View.GONE);
                                btnUpload.setVisibility(View.GONE);
                                txtKetBukti.setVisibility(View.GONE);
                                imgBukti.setVisibility(View.VISIBLE);

                                txtPenjelasanMitra.setVisibility(View.VISIBLE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);

                                Picasso.with(TransaksiDetailActivity.this)
                                        .load(new URLAdapter().getPhotoBuktiPembayaran()+intent_bukti)
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round)
                                        .into(imgBukti);

                                txtPenjelasanMitra.setText("Pesanan ini tidak di verifikasi, Silahkan Hub. User Untuk Kepastian Pesanan");
                            }
                        }else{
                            if (status_transaksi.equals("Menunggu Pembayaran")){
                                txtPenjelasan.setVisibility(View.VISIBLE);
                                btnUpload.setVisibility(View.VISIBLE);
                                txtKetBukti.setVisibility(View.GONE);
                                imgBukti.setVisibility(View.GONE);

                                txtPenjelasanMitra.setVisibility(View.GONE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);

                            }else if (status_transaksi.equals("Menunggu Verifikasi")){
                                txtPenjelasan.setVisibility(View.GONE);
                                btnUpload.setVisibility(View.GONE);
                                txtKetBukti.setVisibility(View.VISIBLE);
                                imgBukti.setVisibility(View.VISIBLE);

                                txtPenjelasanMitra.setVisibility(View.GONE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);

                                Picasso.with(TransaksiDetailActivity.this)
                                        .load(new URLAdapter().getPhotoBuktiPembayaran()+intent_bukti)
                                        .placeholder(R.mipmap.ic_launcher_round)
                                        .error(R.mipmap.ic_launcher_round)
                                        .into(imgBukti);
                            }else if (status_transaksi.equals("Pembayaran Di Verifikasi")){
                                txtPenjelasan.setVisibility(View.VISIBLE);
                                txtPenjelasan.setText("Selamat, Pesanan Anda Telah Di Terima. Silahkan Tunggu Pihak Mitra Mengkonfirmasi Anda !");

                                txtPenjelasanMitra.setVisibility(View.GONE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);
                            }else if (status_transaksi.equals("Pembayaran Di Tolak")){
                                txtPenjelasan.setVisibility(View.VISIBLE);
                                txtPenjelasan.setText("Maaf, Pesanan Anda Di Tolak. Silahkan Hubungi Mitra Untuk Melakukan Konfirmasi Pesanan Anda !");

                                txtPenjelasanMitra.setVisibility(View.GONE);
                                btnTerima.setVisibility(View.GONE);
                                btnTolak.setVisibility(View.GONE);
                            }
                        }

                        keranjangModelList.clear();

                        JSONArray keranjang = jObj.getJSONArray(TAG_TRANSAKSI);

                        for (int i = 0; i < keranjang.length(); i++) {
                            try {
                                JSONObject jsonObject = keranjang.getJSONObject(i);

                                KeranjangModel keranjangModel = new KeranjangModel();
                                keranjangModel.setIdKeranjang(jsonObject.getString("id_transaksi"));
                                keranjangModel.setIdUsers(jsonObject.getString("id_users"));
                                keranjangModel.setIdLapak(jsonObject.getString("id_lapak"));
                                keranjangModel.setTanggalPakai(jsonObject.getString("tglpakai"));
                                keranjangModel.setKeterangan(jsonObject.getString("keterangan"));
                                keranjangModel.setJumlah(jsonObject.getString("jumlah"));
                                keranjangModel.setSubTotal(jsonObject.getString("sub_total"));
                                keranjangModel.setNamaUsers(jsonObject.getString("nama_users"));
                                keranjangModel.setNamaLapak(jsonObject.getString("nama_lapak"));

                                keranjangModelList.add(keranjangModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                    } else {
                        scrollView.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_TRANSAKSI), Toast.LENGTH_LONG).show();
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
                params.put("kode", kode);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void updateStatus(final String kode, final String status) {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().updateStatusTransaksi(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());
                                Toast.makeText(TransaksiDetailActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(TransaksiDetailActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();

                        Toast.makeText(TransaksiDetailActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("kode", kode);
                params.put("status", status);

                Log.e(TAG, "" + params);
                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, "volley");
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

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}