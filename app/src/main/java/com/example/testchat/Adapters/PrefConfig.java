package com.example.testchat.Adapters;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefConfig {
    public  static void removeDate(Context context){
        SharedPreferences pref = context.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("unique key");
        editor.clear();
        editor.apply();
    }
}
