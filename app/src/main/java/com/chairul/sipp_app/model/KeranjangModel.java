package com.chairul.sipp_app.model;

public class KeranjangModel {
    String idKeranjang, idUsers, idLapak, tanggalPakai, keterangan, jumlah, namaUsers, namaLapak, namaMitra, norekMitra, subTotal, total;

    public KeranjangModel() {
    }

    public KeranjangModel(String idKeranjang, String idUsers, String idLapak, String tanggalPakai, String keterangan, String jumlah, String namaUsers, String namaLapak, String namaMitra, String norekMitra, String subTotal, String total) {
        this.idKeranjang = idKeranjang;
        this.idUsers = idUsers;
        this.idLapak = idLapak;
        this.tanggalPakai = tanggalPakai;
        this.keterangan = keterangan;
        this.jumlah = jumlah;
        this.namaUsers = namaUsers;
        this.namaLapak = namaLapak;
        this.namaMitra = namaMitra;
        this.norekMitra = norekMitra;
        this.subTotal = subTotal;
        this.total = total;
    }

    public String getIdKeranjang() {
        return idKeranjang;
    }

    public void setIdKeranjang(String idKeranjang) {
        this.idKeranjang = idKeranjang;
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

    public String getTanggalPakai() {
        return tanggalPakai;
    }

    public void setTanggalPakai(String tanggalPakai) {
        this.tanggalPakai = tanggalPakai;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getNamaUsers() {
        return namaUsers;
    }

    public void setNamaUsers(String namaUsers) {
        this.namaUsers = namaUsers;
    }

    public String getNamaLapak() {
        return namaLapak;
    }

    public void setNamaLapak(String namaLapak) {
        this.namaLapak = namaLapak;
    }

    public String getNamaMitra() {
        return namaMitra;
    }

    public void setNamaMitra(String namaMitra) {
        this.namaMitra = namaMitra;
    }

    public String getNorekMitra() {
        return norekMitra;
    }

    public void setNorekMitra(String norekMitra) {
        this.norekMitra = norekMitra;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
}
