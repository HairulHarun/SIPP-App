package com.chairul.sipp_app.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.KeranjangActivity;
import com.chairul.sipp_app.LapakKategoriActivity;
import com.chairul.sipp_app.MainActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.KategoriModel;
import com.chairul.sipp_app.model.LapakPhotoModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RVLapakPhotoAdapter extends RecyclerView.Adapter<RVLapakPhotoAdapter.ViewHolder> {
    private Context context;
    private List<LapakPhotoModel> list;

    private int lastPosition = -1;

    public RVLapakPhotoAdapter(Context context, List<LapakPhotoModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVLapakPhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lapak_photo, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        final LapakPhotoModel lapakPhotoModel = list.get(position);

        holder.txtCardId.setText(lapakPhotoModel.getId());

        Picasso.with(context)
                .load(new URLAdapter().getPhotoLapak()+lapakPhotoModel.getUrl())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgLapakPhoto);
        setAnimation(holder.itemView, position);

        holder.txtCardHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Data");
                builder.setMessage("Apakah anda yakin hapus data ini ?");
                builder.setPositiveButton("HAPUS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hapusData(lapakPhotoModel.getId().toString(), position);
                        dialog.cancel();
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
        });
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
        }

    }

    @Override
    public int getItemCount() {
        if(list!= null) {
            return list.size();
        }else{
            return 0;
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imgLapakPhoto;
        public TextView txtCardId, txtCardHapus;

        public ViewHolder(View itemView) {
            super(itemView);
            imgLapakPhoto = (ImageView) itemView.findViewById(R.id.imgLapakPhoto);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardIdLapakPhoto);
            txtCardHapus = (TextView) itemView.findViewById(R.id.txtCardHapusLapakPhoto);

        }

    }

    private void hapusData(final String id_lapak_photo, final int position) {
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().hapusLapakPhoto(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    int success = jObj.getInt("sukses");
                    if (success == 1) {
                        removeItem(position);
                        Toast.makeText(context.getApplicationContext(), jObj.getString("hasil"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context.getApplicationContext(), jObj.getString("hasil"), Toast.LENGTH_LONG).show();
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
                params.put("id_lapak_photo", id_lapak_photo);

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
}
