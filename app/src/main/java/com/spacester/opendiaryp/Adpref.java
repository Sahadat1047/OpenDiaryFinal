package com.spacester.opendiaryp;

import android.content.Context;
import android.content.SharedPreferences;

public class Adpref {
    final SharedPreferences sharedPreferences;
    public Adpref(Context context){
        sharedPreferences = context.getSharedPreferences("filename", Context.MODE_PRIVATE);

    }
    public void setAdsModeState(Boolean state){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("adsShow", state);
        editor.apply();
    }
    public boolean loadAdsModeState(){
        return sharedPreferences.getBoolean("adsShow", false);
    }
}
