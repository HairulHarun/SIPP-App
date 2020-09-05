package com.chairul.sipp_app.adapter;

public class URLAdapter {
    private String URL = "http://192.168.43.177/sim-portalevent/webservices/";
    private String URL_PHOTO = "http://192.168.43.177/sim-portalevent/assets/images/photo/";

    public String register(){
        return URL = URL+"ws-register.php";
    }
    public String getLogin(){
        return URL = URL+"ws-login.php";
    }
    public String getLapakRandom(){
        return URL = URL+"ws-get-lapak-random.php";
    }
    public String getPhotoLapak(){
        return URL = URL_PHOTO+"lapak-photo/";
    }
}
