package com.chairul.sipp_app.model;

public class TransaksiModel {
    String id, idUsers, idLapak, idMitra, namaMitra, bukti, status, subTotal, kode;

    public TransaksiModel() {
    }

    public TransaksiModel(String id, String idUsers, String idLapak, String idMitra, String namaMitra, String bukti, String status, String subTotal, String kode) {
        this.id = id;
        this.idUsers = idUsers;
        this.idLapak = idLapak;
        this.idMitra = idMitra;
        this.namaMitra = namaMitra;
        this.bukti = bukti;
        this.status = status;
        this.subTotal = subTotal;
        this.kode = kode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsers() {
        return idUsers;
    }

    public void setIdUsers(String idUsers) {
        this.idUsers = idUsers;
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

    public String getNamaMitra() {
        return namaMitra;
    }

    public void setNamaMitra(String namaMitra) {
        this.namaMitra = namaMitra;
    }

    public String getBukti() {
        return bukti;
    }

    public void setBukti(String bukti) {
        this.bukti = bukti;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getKode() {
        return kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }
}
