package com.chairul.sipp_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.chairul.sipp_app.adapter.SessionAdapter;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;
    private AnimationDrawable animationDrawable;
    private SessionAdapter sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);

        sessionAdapter = new SessionAdapter(getApplicationContext());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //TODO Auto-generated method stub
                if (sessionAdapter.isLoggedIn()){
                    if (sessionAdapter.getStatus().equals("Mitra")){
                        Intent i = new Intent(SplashActivity.this, MitraActivity.class);
                        startActivity(i);
                    }else{
                        Intent i = new Intent(SplashActivity.this, BerandaActivity.class);
                        startActivity(i);
                    }
                }else{
                    Intent i = new Intent(SplashActivity.this, BerandaActivity.class);
                    startActivity(i);
                }
                //jeda selesai Splashscreen
                this.finish();
            }

            private void finish() {
                // TODO Auto-generated method stub
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (animationDrawable != null && animationDrawable.isRunning()){
            animationDrawable.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (animationDrawable != null && !animationDrawable.isRunning()){
            animationDrawable.start();
        }
    }
}