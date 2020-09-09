package com.chairul.sipp_app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chairul.sipp_app.MitraActivity;
import com.chairul.sipp_app.R;
import com.chairul.sipp_app.adapter.SessionAdapter;

public class TentangFragment extends Fragment{
    private static final String TAG = MitraActivity.class.getSimpleName();
    private SessionAdapter sessionAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tentang, container, false);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());
        sessionAdapter.checkLoginMain();

        return root;
    }
}
