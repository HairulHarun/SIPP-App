package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.KoneksiAdapter;
import com.chairul.sipp_app.adapter.RVLapakAdapterHorizontal;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.TabsAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;
import com.chairul.sipp_app.fragment.Tab1Fragment;
import com.chairul.sipp_app.fragment.Tab2Fragment;
import com.chairul.sipp_app.model.LapakModel;
import com.google.android.material.tabs.TabLayout;
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

public class BerandaActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_LAPAK = "lapak";
    private static final String TAG_LAPAK_PHOTO = "lapak_photo";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;

    private LinearLayout layoutMenu;
    private TextView txtMenuKeranjang, txtMenuTransaksi;
    private ImageView imgMenu;

    private SliderLayout sliderLayout;

    private boolean doubleBackToExitPressedOnce = false;

    String tag_json_obj = "json_obj_req";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        sliderLayout = findViewById(R.id.slider);

        layoutMenu = (LinearLayout) findViewById(R.id.layoutMenu);
        txtMenuKeranjang = (TextView) findViewById(R.id.txtMenuKeranjang);
        txtMenuTransaksi = (TextView) findViewById(R.id.txtMenuTransaksi);
        imgMenu = (ImageView) findViewById(R.id.imgMenu);

        ViewPager viewPager = (ViewPager)findViewById(R.id.view_pager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);

        if (sessionAdapter.isLoggedIn()){
            layoutMenu.setVisibility(View.VISIBLE);
            txtMenuKeranjang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BerandaActivity.this, KeranjangActivity.class));
                }
            });
            txtMenuTransaksi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BerandaActivity.this, TransaksiActivity.class));
                }
            });
            imgMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(BerandaActivity.this, ProfileActivity.class));
                }
            });
        }else {
            layoutMenu.setVisibility(View.GONE);
        }

        initRV();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (sessionAdapter.isLoggedIn()){
            getMenuInflater().inflate(R.menu.menu_1, menu);
        }else{
            getMenuInflater().inflate(R.menu.menu_beranda, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (sessionAdapter.isLoggedIn()){
            if (id == R.id.action_keranjang) {
                startActivity(new Intent(BerandaActivity.this, KeranjangActivity.class));
            }else if (id == R.id.action_transaksi) {
                startActivity(new Intent(BerandaActivity.this, TransaksiActivity.class));
            }else if (id == R.id.action_profile) {
                startActivity(new Intent(BerandaActivity.this, ProfileActivity.class));
            }else if (id == R.id.action_about) {
                startActivity(new Intent(BerandaActivity.this, AboutActivity.class));
            }
        }else{
            if (id == R.id.action_login) {
                startActivity(new Intent(BerandaActivity.this, LoginActivity.class));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Klik lagi untuk keluar !", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void initRV(){
        Dexter.withActivity(BerandaActivity.this)
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
                                        com.nispok.snackbar.Snackbar.with(BerandaActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , BerandaActivity.this);
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
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakRandom(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        JSONArray jsonArray = jObj.getJSONArray(TAG_LAPAK);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                JSONArray jsonArray2 = obj.getJSONArray(TAG_LAPAK_PHOTO);
                                String lapak_photo = "noimage.png";
                                if (jsonArray2.length() > 0){
                                    JSONObject obj2 = jsonArray2.getJSONObject(0);
                                    lapak_photo = obj2.getString("url_lapak_photo");
                                }

                                TextSliderView textSliderView = new TextSliderView(BerandaActivity.this);
                                textSliderView.description(obj.getString("nama_lapak"))
                                        .descriptionSize(14)
                                        .image(new URLAdapter().getPhotoLapak()+lapak_photo)
                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                        .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                            @Override
                                            public void onSliderClick(BaseSliderView slider) {
                                                if (sessionAdapter.isLoggedIn()){
                                                    Intent intent = new Intent(BerandaActivity.this, LapakActivity.class);
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
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Silahkan Login Dulu", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("id_lapak", obj.getString("id_lapak"));
                                textSliderView.getBundle().putString("id_mitra", obj.getString("id_mitra"));
                                textSliderView.getBundle().putString("id_kategori", obj.getString("id_kategori"));
                                textSliderView.getBundle().putString("nama_lapak", obj.getString("nama_lapak"));
                                textSliderView.getBundle().putString("detail_lapak", obj.getString("detail_lapak"));
                                textSliderView.getBundle().putString("stok_lapak", obj.getString("stok_lapak"));
                                textSliderView.getBundle().putString("harga_lapak", obj.getString("harga_lapak"));
                                textSliderView.getBundle().putString("status_lapak", obj.getString("status_lapak"));
                                textSliderView.getBundle().putString("nama_mitra", obj.getString("nama_mitra"));
                                textSliderView.getBundle().putString("nama_kategori", obj.getString("nama_kategori"));

                                sliderLayout.addSlider(textSliderView);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BerandaActivity.this);
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

    private void setupViewPager(ViewPager viewPager) {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(Tab1Fragment.newInstance(), "Beranda");
        adapter.addFragment(Tab2Fragment.newInstance(), "Kategori");
        viewPager.setAdapter(adapter);
    }

    private class SectionPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}