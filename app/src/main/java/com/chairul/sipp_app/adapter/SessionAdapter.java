package com.chairul.sipp_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.chairul.sipp_app.BerandaActivity;
import com.chairul.sipp_app.LoginActivity;
import com.chairul.sipp_app.MainActivity;
import com.chairul.sipp_app.MitraActivity;

public class SessionAdapter {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    private int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Sesi";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_SHOW = "IsShow";
    public static final String KEY_STATUS = "status";
    public static final String KEY_ID = "id";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_ALAMAT = "alamat";
    public static final String KEY_HP = "hp";
    public static final String KEY_NOREK = "norek";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PHOTO = "photo";
    public static final String FIREBASE_TOKEN = "token";

    public SessionAdapter(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String status,
                                           String id,
                                           String nama,
                                           String alamat,
                                           String nohp,
                                           String norek,
                                           String username,
                                           String password,
                                           String photo){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_HP, nohp);
        editor.putString(KEY_NOREK, norek);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_PHOTO, photo);
        editor.commit();
    }

    public void simpanToken(String token){
        editor.putString(FIREBASE_TOKEN, token);
        editor.commit();
    }

    public void createDialogSession(){
        editor.putBoolean(IS_SHOW, true);
        editor.commit();
    }

    public void checkLoginMain(){
        if(!this.isLoggedIn()){
            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public void checkLogin(){
        if(this.isLoggedIn()){
            if (getStatus().equals("Mitra")){
                Intent i = new Intent(_context, MitraActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }else if (getStatus().equals("Users")){
                Intent i = new Intent(_context, BerandaActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                _context.startActivity(i);
            }else{
                Toast.makeText(_context, getStatus(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getStatus(){
        String user = pref.getString(KEY_STATUS, null);
        return user;
    }

    public String getId(){
        String user = pref.getString(KEY_ID, null);
        return user;
    }

    public String getNama(){
        String user = pref.getString(KEY_NAMA, null);
        return user;
    }

    public String getAlamat(){
        String user = pref.getString(KEY_ALAMAT, null);
        return user;
    }

    public String getHp(){
        String user = pref.getString(KEY_HP, null);
        return user;
    }

    public String getNoRek(){
        String user = pref.getString(KEY_NOREK, null);
        return user;
    }

    public String getUsername(){
        String user = pref.getString(KEY_USERNAME, null);
        return user;
    }

    public String getPassword(){
        String user = pref.getString(KEY_PASSWORD, null);
        return user;
    }

    public String getPhoto(){
        String user = pref.getString(KEY_PHOTO, null);
        return user;
    }

    public String getToken(){
        String token = pref.getString(FIREBASE_TOKEN, null);
        return token;
    }

    public void logoutUser(){
        // Clearing all data from Shared Preferences
        editor.putBoolean(IS_LOGIN, false);
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean isShow(){
        return pref.getBoolean(IS_SHOW, false);
    }
}
