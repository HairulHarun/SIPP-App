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
import com.chairul.sipp_app.LapakDetailActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.LapakModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RVLapakAdapter extends RecyclerView.Adapter<RVLapakAdapter.ViewHolder> {
    private Context context;
    private List<LapakModel> list;

    private int lastPosition = -1;

    public RVLapakAdapter(Context context, List<LapakModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVLapakAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_lapak, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final LapakModel lapakModel = list.get(position);

        holder.txtCardId.setText(lapakModel.getIdLapak());
        holder.txtCardIdMitra.setText(lapakModel.getIdMitra());
        holder.txtCardIdKategori.setText(lapakModel.getIdKategori());
        holder.txtCardNamaLapak.setText(lapakModel.getNama());
        holder.txtCardDetailLapak.setText(lapakModel.getDetail());
        holder.txtCardStokLapak.setText(lapakModel.getStok());
        holder.txtCardHargaLapak.setText(lapakModel.getHarga());
        holder.txtCardStatusLapak.setText(lapakModel.getStatus());

        Picasso.with(context)
                .load(new URLAdapter().getPhotoLapak()+lapakModel.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgLapak);
        setAnimation(holder.itemView, position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LapakActivity.class);
                intent.putExtra("id", lapakModel.getIdLapak());
                intent.putExtra("id_mitra", lapakModel.getIdMitra());
                intent.putExtra("id_kategori", lapakModel.getIdKategori());
                intent.putExtra("nama", lapakModel.getNama());
                intent.putExtra("detail", lapakModel.getDetail());
                intent.putExtra("stok", lapakModel.getStok());
                intent.putExtra("harga", lapakModel.getHarga());
                intent.putExtra("status", lapakModel.getStatus());
                intent.putExtra("photo", lapakModel.getPhoto());
                intent.putExtra("nama_mitra", lapakModel.getNamaMitra());
                intent.putExtra("nama_kategori", lapakModel.getNamaKategori());
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
        public ImageView imgLapak;
        public TextView txtCardId, txtCardIdMitra, txtCardIdKategori, txtCardNamaLapak, txtCardDetailLapak, txtCardStokLapak, txtCardHargaLapak, txtCardStatusLapak, txtCardPhotoLapak;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imgLapak = (ImageView) itemView.findViewById(R.id.imgLapak);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardIdLapak);
            txtCardIdMitra = (TextView) itemView.findViewById(R.id.txtCardIdMitra);
            txtCardIdKategori = (TextView) itemView.findViewById(R.id.txtCardIdKategori);
            txtCardNamaLapak = (TextView) itemView.findViewById(R.id.txtCardNamaLapak);
            txtCardDetailLapak = (TextView) itemView.findViewById(R.id.txtCardDetailLapak);
            txtCardStokLapak = (TextView) itemView.findViewById(R.id.txtCardStokLapak);
            txtCardHargaLapak = (TextView) itemView.findViewById(R.id.txtCardHargaLapak);
            txtCardStatusLapak = (TextView) itemView.findViewById(R.id.txtCardStatusLapak);
            txtCardPhotoLapak = (TextView) itemView.findViewById(R.id.txtCardPhotoLapak);

        }

    }
}
