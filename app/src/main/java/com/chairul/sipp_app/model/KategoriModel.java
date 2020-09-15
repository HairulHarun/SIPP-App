package com.chairul.sipp_app.model;

public class KategoriModel {
    String id, nama, photo;

    public KategoriModel() {
    }

    public KategoriModel(String id, String nama, String photo) {
        this.id = id;
        this.nama = nama;
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
