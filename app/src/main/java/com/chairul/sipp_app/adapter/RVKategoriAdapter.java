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
import com.chairul.sipp_app.LapakKategoriActivity;
import com.chairul.sipp_app.MitraLapakActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.model.KategoriModel;
import com.chairul.sipp_app.model.LapakModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RVKategoriAdapter extends RecyclerView.Adapter<RVKategoriAdapter.ViewHolder> {
    private SessionAdapter sessionAdapter;
    private Context context;
    private List<KategoriModel> list;

    private int lastPosition = -1;

    public RVKategoriAdapter(Context context, List<KategoriModel> list){
        super();

        this.list = list;
        this.context = context;
        sessionAdapter = new SessionAdapter(context);
    }

    @Override
    public RVKategoriAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_kategori, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final KategoriModel kategoriModel = list.get(position);

        holder.txtCardId.setText(kategoriModel.getId());
        holder.txtCardNamaKategori.setText(kategoriModel.getNama());

        Picasso.with(context)
                .load(new URLAdapter().getPhotoKategori()+kategoriModel.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgKategori);
        setAnimation(holder.itemView, position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionAdapter.getStatus().equals("Mitra")){
                    Intent intent = new Intent(context, MitraLapakActivity.class);
                    intent.putExtra("id_kategori", kategoriModel.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, LapakKategoriActivity.class);
                    intent.putExtra("id_kategori", kategoriModel.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
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
        public ImageView imgKategori;
        public TextView txtCardId, txtCardNamaKategori;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imgKategori = (ImageView) itemView.findViewById(R.id.imgKategori);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardIdKategori);
            txtCardNamaKategori = (TextView) itemView.findViewById(R.id.txtCardNamaKategori);

        }

    }
}
