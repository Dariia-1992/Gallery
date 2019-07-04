package com.example.gallery;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    public static final String APP_PREFERENCES_FILE_NAME = "userdata";
    public static final String USER_ID = "userID";
    public static final String TOKEN = "token";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String USER_NAME = "username";

    private SharedPreferences mPreferences;

    public AppPreferences(Context context){
        this.mPreferences = context.getSharedPreferences(APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key){
        return mPreferences.getString(key, null);
    }
    public  void putString(String key, String value){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public void clear(){
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
