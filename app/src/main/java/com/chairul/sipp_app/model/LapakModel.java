package com.chairul.sipp_app.model;

public class LapakModel {
    String idLapak, idMitra, idKategori, nama, detail, stok, harga, status, photo;

    public LapakModel() {
    }

    public LapakModel(String idLapak, String idMitra, String idKategori, String nama, String detail, String stok, String harga, String status, String photo) {
        this.idLapak = idLapak;
        this.idMitra = idMitra;
        this.idKategori = idKategori;
        this.nama = nama;
        this.detail = detail;
        this.stok = stok;
        this.harga = harga;
        this.status = status;
        this.photo = photo;
    }

    public String getIdLapak() {
        return idLapak;
    }

    public void setIdLapak(String idLapak) {
        this.idLapak = idLapak;
    }

    public String getIdMitra() {
        return idMitra;
    }

    public void setIdMitra(String idMitra) {
        this.idMitra = idMitra;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
