package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.adapter.HttpsTrustManagerAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.chairul.sipp_app.adapter.VolleyAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TambahLapakPhotoActivity extends AppCompatActivity {
    Button buttonChoose, btnUpload;
    ImageView imageView;
    Bitmap bitmap, decoded;
    int success;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60; // range 1 - 100

    private static final String TAG = TambahLapakPhotoActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String KEY_IMAGE = "image";

    String tag_json_obj = "json_obj_req";

    private Intent intent;
    private String ID, ID_MITRA, ID_KATEGORI, NAMA, DETAIL, STOK, HARGA, STATUS, NAMA_MITRA, NAMA_KATEGORI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_lapak_photo);

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

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        btnUpload = (Button) findViewById(R.id.btnUploadLapakPhoto);
        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(ID);
            }
        });
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(final String id_lapak) {
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().simpanLapakPhoto(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(TAG, "Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            success = jObj.getInt(TAG_SUCCESS);

                            if (success == 1) {
                                Log.e("v Add", jObj.toString());
                                Toast.makeText(TambahLapakPhotoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                                kosong();
                                Intent intent = new Intent(TambahLapakPhotoActivity.this, EditPhotoLapakActivity.class);
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
                            } else {
                                Toast.makeText(TambahLapakPhotoActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(TambahLapakPhotoActivity.this, error.getMessage().toString(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage().toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("image", getStringImage(decoded));
                params.put("id_lapak", id_lapak);

                Log.e(TAG, "" + params);
                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    public void onBackPressed(){
        Intent intent = new Intent(TambahLapakPhotoActivity.this, EditPhotoLapakActivity.class);
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

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void kosong() {
        imageView.setImageResource(0);
    }

    private void setToImageView(Bitmap bmp) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        imageView.setImageBitmap(decoded);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}