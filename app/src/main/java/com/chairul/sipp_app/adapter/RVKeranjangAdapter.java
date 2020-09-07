package com.chairul.sipp_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chairul.sipp_app.LapakActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.KeranjangModel;
import com.chairul.sipp_app.model.LapakModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RVKeranjangAdapter extends RecyclerView.Adapter<RVKeranjangAdapter.ViewHolder> {
    private Context context;
    private List<KeranjangModel> list;

    private int lastPosition = -1;

    public RVKeranjangAdapter(Context context, List<KeranjangModel> list){
        super();

        this.list = list;
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
            txtCardQty = (TextView) itemView.findViewById(R.id.txtCardKeranjangQty);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardKeranjangNama);
            txtCardTanggal = (TextView) itemView.findViewById(R.id.txtCardKeranjangTanggal);
            txtCardKet = (TextView) itemView.findViewById(R.id.txtCardKeranjangKet);
            txtCardJumlah = (TextView) itemView.findViewById(R.id.txtCardKeranjangJumlah);

        }

    }
}
