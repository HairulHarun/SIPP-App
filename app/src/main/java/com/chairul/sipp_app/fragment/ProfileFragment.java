package com.chairul.sipp_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chairul.sipp_app.MitraActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.adapter.SessionAdapter;
import com.chairul.sipp_app.adapter.URLAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment{
    private static final String TAG = MitraActivity.class.getSimpleName();
    private SessionAdapter sessionAdapter;
    private TextView txtNama, txtNoHP, txtAlamat, txtNoRek, txtUsername, txtPassword, layoutNorek;
    private ImageView btnLogout;
    private CircleImageView photoUsers;
    private View viewNorek;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        txtNama = (TextView) root.findViewById(R.id.txtProfileNama);
        txtNoHP = (TextView) root.findViewById(R.id.txtProfileNoHp);
        txtAlamat = (TextView) root.findViewById(R.id.txtProfileAlamat);
        txtNoRek = (TextView) root.findViewById(R.id.txtProfileNoRek);
        txtUsername = (TextView) root.findViewById(R.id.txtProfileUsername);
        txtPassword = (TextView) root.findViewById(R.id.txtProfilePassword);
        btnLogout = (ImageView) root.findViewById(R.id.btnLogout);
        photoUsers = root.findViewById(R.id.user_profile_photo);
        viewNorek = (View) root.findViewById(R.id.viewNorek);
        layoutNorek = (TextView) root.findViewById(R.id.layoutNorek);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

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

        Picasso.with(getActivity().getApplicationContext())
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

        return root;
    }
}
