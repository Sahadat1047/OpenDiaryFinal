package com.spacester.opendiaryp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref {
    final SharedPreferences sharedPreferences;
    public SharedPref(Context context){
        sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);

    }
    public void setNightModeState(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("NightMode", state);
        editor.apply();
    }
    public boolean loadNightModeState(){
        return sharedPreferences.getBoolean("NightMode", false);
    }
}
