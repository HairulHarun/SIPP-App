package com.chairul.sipp_app.model;

public class LapakPhotoModel {
    String id, idLapak, url;

    public LapakPhotoModel() {
    }

    public LapakPhotoModel(String id, String idLapak, String url) {
        this.id = id;
        this.idLapak = idLapak;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdLapak() {
        return idLapak;
    }

    public void setIdLapak(String idLapak) {
        this.idLapak = idLapak;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
