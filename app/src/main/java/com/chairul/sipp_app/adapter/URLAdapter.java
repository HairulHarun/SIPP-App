package com.chairul.sipp_app.adapter;

public class URLAdapter {
    /*private String URL = "http://192.168.43.177/sim-portalevent/webservices/";
    private String URL_PHOTO = "http://192.168.43.177/sim-portalevent/assets/images/photo/";*/

    private String URL = "http://10.3.22.252/sim-portalevent/webservices/";
    private String URL_PHOTO = "http://10.3.22.252/sim-portalevent/assets/images/photo/";

    public String register(){
        return URL = URL+"ws-register.php";
    }
    public String getLogin(){
        return URL = URL+"ws-login.php";
    }
    public String getKeranjang(){
        return URL = URL+"ws-get-keranjang-users.php";
    }
    public String getLapakRandom(){
        return URL = URL+"ws-get-lapak-random.php";
    }
    public String getLapakPhoto(){
        return URL = URL+"ws-get-lapak-photo.php";
    }
    public String simpanKeranjang(){
        return URL = URL+"ws-simpan-keranjang.php";
    }
    public String simpanTransaksi(){
        return URL = URL+"ws-simpan-transaksi.php";
    }
    public String hapusKeranjang(){
        return URL = URL+"ws-hapus-keranjang.php";
    }

    public String getPhotoLapak(){
        return URL = URL_PHOTO+"lapak-photo/";
    }
}
