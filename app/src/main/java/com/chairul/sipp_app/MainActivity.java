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
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapterHorizontal;
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

import in.myinnos.imagesliderwithswipeslibrary.Animations.DescriptionAnimation;
import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView;

public class MainActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_LAPAK = "lapak";
    private static final String TAG_LAPAK_PHOTO = "lapak_photo";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerViewHorisontal, mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManagerHorisontal, mLayoutManager;
    private RecyclerView.Adapter adapter, adapterHorisontal;
    private List<LapakModel> lapakModelList, lapakModelList2;
    private SearchView searchView;

    private SliderLayout sliderLayout;

    String tag_json_obj = "json_obj_req";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());
        sliderLayout = findViewById(R.id.slider);

        searchView = findViewById(R.id.searchOutlet);

        mRecyclerViewHorisontal = (RecyclerView)findViewById(R.id.rvOutlet);
        mRecyclerView = (RecyclerView)findViewById(R.id.rvOutlet2);
        lapakModelList = new ArrayList<>();
        lapakModelList2 = new ArrayList<>();

        initRV();
        searchData();
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Intent intent = new Intent(MainActivity.this, LapakActivity.class);
        intent.putExtra("id", String.valueOf(slider.getBundle().get("id_lapak")));
        intent.putExtra("id_mitra", String.valueOf(slider.getBundle().get("id_mitra")));
        intent.putExtra("id_kategori", String.valueOf(slider.getBundle().get("id_kategori")));
        intent.putExtra("nama", String.valueOf(slider.getBundle().get("nama_lapak")));
        intent.putExtra("detail", String.valueOf(slider.getBundle().get("detail_lapak")));
        intent.putExtra("stok", String.valueOf(slider.getBundle().get("stok_lapak")));
        intent.putExtra("harga", String.valueOf(slider.getBundle().get("harga_lapak")));
        intent.putExtra("status", String.valueOf(slider.getBundle().get("status_lapak")));
        intent.putExtra("nama_mitra", String.valueOf(slider.getBundle().get("nama_mitra")));
        intent.putExtra("nama_kategori", String.valueOf(slider.getBundle().get("nama_kategori")));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initRV(){
        adapterHorisontal = new RVLapakAdapterHorizontal(getApplicationContext(), lapakModelList);
        mLayoutManagerHorisontal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewHorisontal.setLayoutManager(mLayoutManagerHorisontal);
        mRecyclerViewHorisontal.setHasFixedSize(true);
        mRecyclerViewHorisontal.setAdapter(adapterHorisontal);

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
                                getImage("");
                                getDataLapak("");
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(MainActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , MainActivity.this);
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

    private void searchData(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getDataLapak(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getImage(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakRandom(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        lapakModelList.clear();
                        JSONArray jsonArray = jObj.getJSONArray(TAG_LAPAK);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                LapakModel lapakModel = new LapakModel();
                                lapakModel.setIdLapak(obj.getString("id_lapak"));
                                lapakModel.setIdMitra(obj.getString("id_mitra"));
                                lapakModel.setIdKategori(obj.getString("id_kategori"));
                                lapakModel.setNama(obj.getString("nama_lapak"));
                                lapakModel.setDetail(obj.getString("detail_lapak"));
                                lapakModel.setStok(obj.getString("stok_lapak"));
                                lapakModel.setHarga(obj.getString("harga_lapak"));
                                lapakModel.setStatus(obj.getString("status_lapak"));
                                lapakModel.setNamaMitra(obj.getString("nama_mitra"));
                                lapakModel.setNamaKategori(obj.getString("nama_kategori"));

                                JSONArray jsonArray2 = obj.getJSONArray(TAG_LAPAK_PHOTO);
                                String lapak_photo = "noimage.png";
                                if (jsonArray2.length() > 0){
                                    JSONObject obj2 = jsonArray2.getJSONObject(0);
                                    lapak_photo = obj2.getString("url_lapak_photo");
                                }

                                lapakModel.setPhoto(lapak_photo);
                                lapakModelList.add(lapakModel);

                                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                                textSliderView.description(obj.getString("nama_lapak"))
                                        .descriptionSize(14)
                                        .image(new URLAdapter().getPhotoLapak()+lapak_photo)
                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                        .setOnSliderClickListener(MainActivity.this);

                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("id_lapak", obj.getString("id_lapak"));
                                textSliderView.getBundle().putString("id_mitra", obj.getString("id_mitra"));
                                textSliderView.getBundle().putString("id_kategori", obj.getString("id_kategori"));
                                textSliderView.getBundle().putString("nama_lapak", obj.getString("nama_lapak"));
                                textSliderView.getBundle().putString("detail_lapak", obj.getString("detail_lapak"));
                                textSliderView.getBundle().putString("stok_lapak", obj.getString("stok_lapak"));
                                textSliderView.getBundle().putString("harga_lapak", obj.getString("harga_lapak"));
                                textSliderView.getBundle().putString("status_lapak", obj.getString("status_lapak"));

                                sliderLayout.addSlider(textSliderView);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapterHorisontal.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_LAPAK), Toast.LENGTH_SHORT).show();
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

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);

        sliderLayout.setPresetTransformer("Stack");

    }

    private void getDataLapak(final String param) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakRandom(), new Response.Listener<String>() {
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
                params.put("param", param);

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