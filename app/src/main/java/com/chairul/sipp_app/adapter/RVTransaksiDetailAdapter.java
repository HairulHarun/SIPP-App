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
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.chairul.sipp_app.KeranjangActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.KeranjangModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class RVTransaksiDetailAdapter extends RecyclerView.Adapter<RVTransaksiDetailAdapter.ViewHolder> {
    private static final String TAG = KeranjangActivity.class.getSimpleName();
    private Activity activity;
    private Context context;
    private List<KeranjangModel> list;

    private int lastPosition = -1;

    public RVTransaksiDetailAdapter(Activity activity, Context context, List<KeranjangModel> list){
        super();

        this.list = list;
        this.activity = activity;
        this.context = context;
    }

    @Override
    public RVTransaksiDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaksi_detail, parent, false);
        RVTransaksiDetailAdapter.ViewHolder viewHolder = new RVTransaksiDetailAdapter.ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(RVTransaksiDetailAdapter.ViewHolder holder, int position) {
        final KeranjangModel keranjangModel = list.get(position);

        holder.txtCardQty.setText(keranjangModel.getJumlah());
        holder.txtCardNama.setText(keranjangModel.getNamaLapak());
        holder.txtCardTanggal.setText(keranjangModel.getTanggalPakai());
        holder.txtCardKet.setText(keranjangModel.getKeterangan());
        holder.txtCardJumlah.setText(konversiRupiah(Integer.parseInt(keranjangModel.getSubTotal())));

        setAnimation(holder.itemView, position);
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
        public TextView txtCardQty, txtCardNama, txtCardTanggal, txtCardKet, txtCardJumlah;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCardQty = (TextView) itemView.findViewById(R.id.txtCardTransaksiDetailQty);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardTransaksiDetailNama);
            txtCardTanggal = (TextView) itemView.findViewById(R.id.txtCardTransaksiDetailTanggal);
            txtCardKet = (TextView) itemView.findViewById(R.id.txtCardTransaksiDetailKet);
            txtCardJumlah = (TextView) itemView.findViewById(R.id.txtCardTransaksiDetailJumlah);
        }

    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

    private void refresh(){
        Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.startActivity(intent);
    }
}
