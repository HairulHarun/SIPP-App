package com.chairul.sipp_app.adapter;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.chairul.sipp_app.TransaksiActivity;
import com.chairul.sipp_app.TransaksiDetailActivity;
import com.chairul.sipp_app.model.KeranjangModel;
import com.chairul.sipp_app.model.TransaksiModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RVTransaksiAdapter extends RecyclerView.Adapter<RVTransaksiAdapter.ViewHolder> {
    private static final String TAG = TransaksiActivity.class.getSimpleName();
    private Activity activity;
    private Context context;
    private List<TransaksiModel> list;

    private int lastPosition = -1;

    public RVTransaksiAdapter(Activity activity, Context context, List<TransaksiModel> list){
        super();

        this.list = list;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public RVTransaksiAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaksi, parent, false);
        RVTransaksiAdapter.ViewHolder viewHolder = new RVTransaksiAdapter.ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(RVTransaksiAdapter.ViewHolder holder, int position) {
        final TransaksiModel transaksiModel = list.get(position);

        holder.txtCardNamaMitra.setText(transaksiModel.getKode());
        holder.txtCardStatus.setText(transaksiModel.getStatus());
        holder.txtCardId.setText(transaksiModel.getId());
        holder.txtCardIdUsers.setText(transaksiModel.getIdUsers());
        holder.txtCardIdLapak.setText(transaksiModel.getIdLapak());
        holder.txtCardIdMitra.setText(transaksiModel.getIdMitra());
        holder.txtCardSubTotal.setText("Rp. "+transaksiModel.getSubTotal());

        setAnimation(holder.itemView, position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TransaksiDetailActivity.class);
                intent.putExtra("id", transaksiModel.getId());
                intent.putExtra("id_lapak", transaksiModel.getIdLapak());
                intent.putExtra("id_users", transaksiModel.getIdUsers());
                intent.putExtra("id_mitra", transaksiModel.getIdMitra());
                intent.putExtra("nama_mitra", transaksiModel.getNamaMitra());
                intent.putExtra("kode", transaksiModel.getKode());
                intent.putExtra("bukti", transaksiModel.getBukti());
                intent.putExtra("status", transaksiModel.getStatus());
                intent.putExtra("sub_total", transaksiModel.getSubTotal());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
        public LinearLayout layout;
        public TextView txtCardNamaMitra, txtCardStatus, txtCardId, txtCardIdUsers, txtCardIdLapak, txtCardIdMitra, txtCardSubTotal;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            txtCardNamaMitra = (TextView) itemView.findViewById(R.id.txtCardTransaksiNamaMitra);
            txtCardStatus = (TextView) itemView.findViewById(R.id.txtCardTransaksiStatus);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardTransaksiId);
            txtCardIdUsers = (TextView) itemView.findViewById(R.id.txtCardTransaksiIdUsers);
            txtCardIdLapak = (TextView) itemView.findViewById(R.id.txtCardTransaksiIdLapak);
            txtCardIdMitra = (TextView) itemView.findViewById(R.id.txtCardTransaksiIdMitra);
            txtCardSubTotal = (TextView) itemView.findViewById(R.id.txtCardTransaksiSubTotal);

        }

    }
}
