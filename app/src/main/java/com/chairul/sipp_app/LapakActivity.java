package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.myinnos.imagesliderwithswipeslibrary.Animations.DescriptionAnimation;
import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView;

public class LapakActivity extends AppCompatActivity {
    private static final String TAG = LapakActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_MESSAGE = "hasil";
    private static final String TAG_LAPAK_PHOTO = "lapak_photo";

    private Intent intent;
    private String ID, ID_MITRA, ID_KATEGORI, NAMA, DETAIL, STOK, HARGA, STATUS, NAMA_MITRA, NAMA_KATEGORI;

    private TextView txtMitra, txtKategori, txtNama, txtDetail, txtHarga, txtStok, txtStatus;
    private Button btnKeranjang, btnEditPhoto;

    private SliderLayout sliderLayout;

    private AlertDialog alertDialog;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private ProgressDialog pDialog;

    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lapak);

        koneksiAdapter= new KoneksiAdapter(getApplicationContext());
        sessionAdapter = new SessionAdapter(getApplicationContext());

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

        txtMitra = (TextView) findViewById(R.id.txt_mitra_lapak);
        txtKategori = (TextView) findViewById(R.id.txt_kategori_lapak);
        txtNama = (TextView) findViewById(R.id.txt_nama_lapak);
        txtDetail = (TextView) findViewById(R.id.txt_detail_lapak);
        txtStok = (TextView) findViewById(R.id.txt_stok_lapak);
        txtHarga = (TextView) findViewById(R.id.txt_harga_lapak);
        txtStatus = (TextView) findViewById(R.id.txt_status_lapak);
        btnKeranjang = (Button) findViewById(R.id.btnKeranjang);
        btnEditPhoto = (Button) findViewById(R.id.btnEditPhoto);

        sliderLayout = findViewById(R.id.slider);

        txtMitra.setText(NAMA_MITRA);
        txtKategori.setText(NAMA_KATEGORI);
        txtDetail.setText(DETAIL);
        txtHarga.setText("Rp. "+HARGA);
        txtStok.setText(STOK+" pcs");
        txtStatus.setText(STATUS);

        getImage(ID);

        if (sessionAdapter.isLoggedIn()){
            if (sessionAdapter.getStatus().equals("Mitra")){
                btnKeranjang.setVisibility(View.GONE);
                btnEditPhoto.setVisibility(View.VISIBLE);
                btnEditPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(LapakActivity.this, EditPhotoLapakActivity.class);
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
                });
            }else{
                btnKeranjang.setVisibility(View.VISIBLE);
                btnEditPhoto.setVisibility(View.GONE);
                btnKeranjang.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialogKeranjang();
                    }
                });
            }
        }else{
            btnKeranjang.setVisibility(View.GONE);
            btnEditPhoto.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    private void getImage(final String id) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getLapakPhoto(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        JSONArray jsonArray = jObj.getJSONArray(TAG_LAPAK_PHOTO);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                TextSliderView textSliderView = new TextSliderView(LapakActivity.this);
                                textSliderView.description(obj.getString("id_lapak_photo"))
                                        .descriptionSize(14)
                                        .image(new URLAdapter().getPhotoLapak()+obj.getString("url_lapak_photo"))
                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);
                                sliderLayout.addSlider(textSliderView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_LAPAK_PHOTO), Toast.LENGTH_SHORT).show();
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
                params.put("id_lapak", id);

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

    private void showDialogKeranjang(){
        AlertDialog.Builder builder = new AlertDialog.Builder(LapakActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_tambah_keranjang, null);

        TextView txtNama = view.findViewById(R.id.txtDialogNama);
        TextView txtHarga = view.findViewById(R.id.txtDialogHarga);
        final TextView txtTanggal = view.findViewById(R.id.txtDialogTanggal);
        final TextView txtJumlah = view.findViewById(R.id.txtDialogJumlah);
        final TextView txtKeterangan = view.findViewById(R.id.txtDialogKeterangan);

        txtTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog;
                final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

                Calendar newCalendar = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(LapakActivity.this, new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        txtTanggal.setText(dateFormatter.format(newDate.getTime()));
                    }

                },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        Button btnSimpan = view.findViewById(R.id.btnDialogSimpan);

        txtNama.setText(NAMA);
        txtNama.setText("Rp. "+HARGA);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(LapakActivity.this)
                        .withPermissions(
                                android.Manifest.permission.INTERNET,
                                Manifest.permission.ACCESS_NETWORK_STATE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {
                                    String tanggal = txtTanggal.getText().toString();
                                    String jumlah = txtJumlah.getText().toString();
                                    String keterangan = txtKeterangan.getText().toString();

                                    if (tanggal.trim().length() > 0 && jumlah.trim().length() > 0) {
                                        if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                            simpanKeranjang(tanggal, jumlah, keterangan);
                                        }else{
                                            SnackbarManager.show(
                                                    Snackbar.with(LapakActivity.this)
                                                            .text("No Connection !")
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                            .actionLabel("Refresh")
                                                            .actionListener(new ActionClickListener() {
                                                                @Override
                                                                public void onActionClicked(Snackbar snackbar) {
                                                                    refresh();
                                                                }
                                                            })
                                                    , LapakActivity.this);
                                        }
                                    } else {
                                        // Prompt user to enter credentials
                                        Toast.makeText(getApplicationContext() ,"Kolom tidak boleh kosong", Toast.LENGTH_LONG).show();
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
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void simpanKeranjang(final String tanggal, final String jumlah, final String keterangan) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logging in ...");
        showDialog();

        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().simpanKeranjang(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Check for error node in json
                    if (success == 1) {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        alertDialog.cancel();
                    } else {
                        Toast.makeText(getApplicationContext(), "Json Error: " + jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Login Error: " + error.getMessage(), Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_users", sessionAdapter.getId());
                params.put("id_lapak", ID);
                params.put("tanggal", tanggal);
                params.put("keterangan", keterangan);
                params.put("jumlah", jumlah);

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

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LapakActivity.this);
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
}