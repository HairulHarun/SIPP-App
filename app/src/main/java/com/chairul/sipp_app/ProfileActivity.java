package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private TextView txtNama, txtNoHP, txtAlamat, txtNoRek, txtUsername, txtPassword, layoutNorek;
    private ImageView btnLogout;
    private CircleImageView photoUsers;
    private SessionAdapter sessionAdapter;
    private View viewNorek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        txtNama = (TextView) findViewById(R.id.txtProfileNama);
        txtNoHP = (TextView) findViewById(R.id.txtProfileNoHp);
        txtAlamat = (TextView) findViewById(R.id.txtProfileAlamat);
        txtNoRek = (TextView) findViewById(R.id.txtProfileNoRek);
        txtUsername = (TextView) findViewById(R.id.txtProfileUsername);
        txtPassword = (TextView) findViewById(R.id.txtProfilePassword);
        btnLogout = (ImageView) findViewById(R.id.btnLogout);
        photoUsers = findViewById(R.id.user_profile_photo);
        viewNorek = (View) findViewById(R.id.viewNorek);
        layoutNorek = (TextView) findViewById(R.id.layoutNorek);

        sessionAdapter = new SessionAdapter(getApplicationContext());

        txtNama.setText(sessionAdapter.getNama());
        txtNoHP.setText(sessionAdapter.getHp());
        txtAlamat.setText(sessionAdapter.getAlamat());
        txtNoRek.setText(sessionAdapter.getNoRek());
        txtUsername.setText(sessionAdapter.getUsername());
        txtPassword.setText(sessionAdapter.getPassword());

        if (sessionAdapter.getStatus().equals("Users")){
            viewNorek.setVisibility(View.GONE);
            txtNoRek.setVisibility(View.GONE);
            layoutNorek.setVisibility(View.GONE);
        }

        Picasso.with(getApplicationContext())
                .load(new URLAdapter().getPhotoLapak()+sessionAdapter.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(photoUsers);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionAdapter.logoutUser();
            }
        });
    }
}