package com.chairul.sipp_app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.KeranjangActivity;
import com.chairul.sipp_app.LapakActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.KeranjangModel;
import com.chairul.sipp_app.model.LapakModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RVKeranjangAdapter extends RecyclerView.Adapter<RVKeranjangAdapter.ViewHolder> {
    private static final String TAG = KeranjangActivity.class.getSimpleName();
    private Activity activity;
    private Context context;
    private List<KeranjangModel> list;

    private int lastPosition = -1;

    public RVKeranjangAdapter(Activity activity, Context context, List<KeranjangModel> list){
        super();

        this.list = list;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public RVKeranjangAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_keranjang, parent, false);
        RVKeranjangAdapter.ViewHolder viewHolder = new RVKeranjangAdapter.ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(RVKeranjangAdapter.ViewHolder holder, int position) {
        final KeranjangModel keranjangModel = list.get(position);

        holder.txtCardQty.setText(keranjangModel.getJumlah());
        holder.txtCardNama.setText(keranjangModel.getNamaLapak());
        holder.txtCardTanggal.setText(keranjangModel.getTanggalPakai());
        holder.txtCardKet.setText(keranjangModel.getKeterangan());
        holder.txtCardJumlah.setText("Rp. "+keranjangModel.getSubTotal());

        holder.imgHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(keranjangModel.getIdKeranjang());
            }
        });

        setAnimation(holder.itemView, position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
        }
    }

    private void showDialog(final String id_keranjang) throws Resources.NotFoundException {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Hapus Data");
        builder.setMessage("Apakah anda ingin menghapus data ini ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                hapusKeranjang(id_keranjang);
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public int getItemCount() {
        if(list!= null) {
            return list.size();
        }else{
            return 0;
        }

    }

    private void hapusKeranjang(final String id_keranjang) {
        HttpsTrustManagerAdapter.allowAllSSL();
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().hapusKeranjang(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Login Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("sukses");

                    if (success == 1) {
                        Toast.makeText(activity.getApplicationContext(), jObj.getString("hasil"), Toast.LENGTH_LONG).show();
                        refresh();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "Json Error: " + jObj.getString("hasil"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(context.getApplicationContext(), "Login Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_keranjang", id_keranjang);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView txtCardQty, txtCardNama, txtCardTanggal, txtCardKet, txtCardJumlah;
        public ImageView imgHapus;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCardQty = (TextView) itemView.findViewById(R.id.txtCardKeranjangQty);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardKeranjangNama);
            txtCardTanggal = (TextView) itemView.findViewById(R.id.txtCardKeranjangTanggal);
            txtCardKet = (TextView) itemView.findViewById(R.id.txtCardKeranjangKet);
            txtCardJumlah = (TextView) itemView.findViewById(R.id.txtCardKeranjangJumlah);
            imgHapus = (ImageView) itemView.findViewById(R.id.txtCardKeranjangHapus);
        }

    }

    private void refresh(){
        Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }
}
