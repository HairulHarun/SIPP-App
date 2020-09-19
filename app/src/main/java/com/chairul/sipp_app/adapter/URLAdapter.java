package com.chairul.sipp_app.adapter;

public class URLAdapter {
    /*private String URL = "http://192.168.43.177/sim-portalevent/webservices/";
    private String URL_PHOTO = "http://192.168.43.177/sim-portalevent/assets/images/photo/";*/

    private String URL = "https://si-peperta.000webhostapp.com/webservices/";
    private String URL_PHOTO = "https://si-peperta.000webhostapp.com/assets/images/photo/";

    public String register(){
        return URL = URL+"ws-register.php";
    }
    public String getLogin(){
        return URL = URL+"ws-login.php";
    }
    public String getKeranjang(){
        return URL = URL+"ws-get-keranjang-users.php";
    }
    public String getTransaksi(){
        return URL = URL+"ws-get-transaksi-users.php";
    }
    public String getTransaksiMitra(){
        return URL = URL+"ws-get-transaksi-mitra.php";
    }
    public String getTransaksiUsersDetail(){
        return URL = URL+"ws-get-transaksi-users-detail.php";
    }
    public String getKategori(){
        return URL = URL+"ws-get-kategori.php";
    }
    public String getLapakRandom(){
        return URL = URL+"ws-get-lapak-random.php";
    }
    public String getLapakKategori(){
        return URL = URL+"ws-get-lapak-kategori.php";
    }
    public String getLapakMitra(){
        return URL = URL+"ws-get-lapak-mitra.php";
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
    public String simpanLapak(){
        return URL = URL+"ws-tambah-lapak.php";
    }
    public String simpanLapakPhoto(){
        return URL = URL+"ws-tambah-lapak-photo.php";
    }
    public String hapusKeranjang(){
        return URL = URL+"ws-hapus-keranjang.php";
    }
    public String hapusLapakPhoto(){
        return URL = URL+"ws-hapus-lapak-photo.php";
    }
    public String uploadBukti(){
        return URL = URL+"ws-upload-bukti-bayar.php";
    }
    public String updateStatusTransaksi(){
        return URL = URL+"ws-update-status-transaksi.php";
    }

    public String getPhotoLapak(){
        return URL = URL_PHOTO+"lapak-photo/";
    }
    public String getPhotoKategori(){
        return URL = URL_PHOTO+"kategori/";
    }
    public String getPhotoBuktiPembayaran(){
        return URL = URL_PHOTO+"bukti/";
    }
}
