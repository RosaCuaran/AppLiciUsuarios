package com.codigoj.liciu.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by JHON on 27/02/2017.
 */

public class AppPreferences {

    public static final String TEMPORAL = "TEMPORAL";
    private static final String APP_SHARED_PREFERENCES = AppPreferences.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public AppPreferences(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFERENCES, Activity.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    /**
     * Saved data type String in the SharedPreferences
     * @param clave this is the key
     * @param valor this is the value to save
     */
    public void saveDataString(String clave, String valor){
        editor.putString(clave, valor);
        editor.commit();
    }

    /**
     * Saved data type Integer in the SharedPreferences
     * @param clave this is the key
     * @param valor this is the value to save
     */
    public void saveDataInt(String clave, int valor){
        editor.putInt(clave, valor);
        editor.commit();
    }

    /**
     * Get the data saved in the SharedPreferences
     * @param clave this is the key
     * @param valor this is the value default if the value doesn't exist
     * @return the value in String saved in the SharedPreferences
     */
    public String getDataString(String clave, String valor){
        return sharedPreferences.getString(clave, valor);
    }

    /**
     * Get the data saved in the SharedPreferences
     * @param clave this is the key
     * @param valor this is the value default if the value doesn't exist
     * @return the value in Integer saved in the SharedPreferences
     */
    public int getDataint(String clave, int valor){
        return sharedPreferences.getInt(clave, valor);
    }

    public void cleanPreferences() {
        sharedPreferences.edit().clear().apply();
    }
}
