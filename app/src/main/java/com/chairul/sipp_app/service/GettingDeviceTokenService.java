package com.chairul.sipp_app.service;

import android.util.Log;

import com.chairul.sipp_app.adapter.SessionAdapter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class GettingDeviceTokenService extends FirebaseMessagingService {
    private SessionAdapter sessionAdapter;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        sessionAdapter.simpanToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}
