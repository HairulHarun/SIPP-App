package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVKategoriAdapter;
import com.chairul.sipp_app.adapter.RVLapakPhotoAdapter;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.model.KategoriModel;
import com.chairul.sipp_app.model.LapakPhotoModel;
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

public class EditPhotoLapakActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_KATEGORI = "kategori";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<LapakPhotoModel> lapakPhotoModelList;

    private Intent intent;
    private String ID, ID_MITRA, ID_KATEGORI, NAMA, DETAIL, STOK, HARGA, STATUS, NAMA_MITRA, NAMA_KATEGORI;

    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_photo_lapak);

        intent = getIntent();
        ID = intent.getStringExtra("id");
        ID_MITRA = intent.getStringExtra("id_mitra");
        ID_KATEGORI = intent.getStringExtra("id_kategori");
        NAMA = intent.getStringExtra("nama");
        DETAIL = intent.getStringExtra("detail");
        STOK = intent.getStringExtra("stok");
        HARGA = intent.getStringExtra("harga");
        STATUS = intent.getStringExtra("status");
        NAMA_MITRA = intent.getStringExtra("nama_mitra");
        NAMA_KATEGORI = intent.getStringExtra("nama_kategori");

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        mRecyclerView = (RecyclerView) findViewById(R.id.rvOutlet);
        lapakPhotoModelList = new ArrayList<>();

        initRV();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_tambah_foto) {
            Intent intent = new Intent(EditPhotoLapakActivity.this, TambahLapakPhotoActivity.class);
            intent.putExtra("id", ID);
            intent.putExtra("id_mitra", ID_MITRA);
            intent.putExtra("id_kategori", ID_KATEGORI);
            intent.putExtra("nama", NAMA);
            intent.putExtra("detail", DETAIL);
            intent.putExtra("stok", STOK);
            intent.putExtra("harga", HARGA);
            intent.putExtra("status", STATUS);
            intent.putExtra("nama_mitra", NAMA_MITRA);
            intent.putExtra("nama_kategori", NAMA_KATEGORI);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRV(){
        adapter = new RVLapakPhotoAdapter(getApplicationContext(), lapakPhotoModelList);
        mLayoutManager = new GridLayoutManager(EditPhotoLapakActivity.this, 3);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(EditPhotoLapakActivity.this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData("");
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(EditPhotoLapakActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , EditPhotoLapakActivity.this);
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

    private void getData(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakPhoto(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        lapakPhotoModelList.clear();
                        JSONArray jsonArray = jObj.getJSONArray(TAG_KATEGORI);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                LapakPhotoModel lapakPhotoModel = new LapakPhotoModel();
                                lapakPhotoModel.setId(obj.getString("id_lapak_photo"));
                                lapakPhotoModel.setUrl(obj.getString("url_lapak_photo"));

                                lapakPhotoModelList.add(lapakPhotoModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPhotoLapakActivity.this);
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