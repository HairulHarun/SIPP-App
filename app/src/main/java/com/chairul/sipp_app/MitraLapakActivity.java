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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapter;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.model.LapakModel;
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

import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;

public class MitraLapakActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_LAPAK = "lapak";
    private static final String TAG_LAPAK_PHOTO = "lapak_photo";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<LapakModel> lapakModelList2;

    int success;

    String ID_MITRA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mitra_lapak);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        mRecyclerView = (RecyclerView)findViewById(R.id.rvOutlet);
        lapakModelList2 = new ArrayList<>();

        ID_MITRA = sessionAdapter.getId();

        initRV();
    }

    private void initRV(){
        adapter = new RVLapakAdapter(getApplicationContext(), lapakModelList2);
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
                                getDataLapak();
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(MitraLapakActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , MitraLapakActivity.this);
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

    private void getDataLapak() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakMitra(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        lapakModelList2.clear();

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_LAPAK);

                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                LapakModel lapakModel = new LapakModel();
                                lapakModel.setIdLapak(jsonObject.getString("id_lapak"));
                                lapakModel.setIdMitra(jsonObject.getString("id_mitra"));
                                lapakModel.setIdKategori(jsonObject.getString("id_kategori"));
                                lapakModel.setNama(jsonObject.getString("nama_lapak"));
                                lapakModel.setDetail(jsonObject.getString("detail_lapak"));
                                lapakModel.setStok(jsonObject.getString("stok_lapak"));
                                lapakModel.setHarga(jsonObject.getString("harga_lapak"));
                                lapakModel.setStatus(jsonObject.getString("status_lapak"));
                                lapakModel.setNamaMitra(jsonObject.getString("nama_mitra"));
                                lapakModel.setNamaKategori(jsonObject.getString("nama_kategori"));

                                JSONArray jsonArray2 = jsonObject.getJSONArray(TAG_LAPAK_PHOTO);
                                String lapak_photo = "noimage.png";
                                if (jsonArray2.length() > 0){
                                    JSONObject obj2 = jsonArray2.getJSONObject(0);
                                    lapak_photo = obj2.getString("url_lapak_photo");
                                }

                                lapakModel.setPhoto(lapak_photo);

                                lapakModelList2.add(lapakModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_LAPAK), Toast.LENGTH_LONG).show();
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
                params.put("id_mitra", ID_MITRA);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
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