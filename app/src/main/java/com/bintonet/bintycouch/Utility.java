package com.bintonet.bintycouch;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Darren on 07/08/2014.
 */

public class Utility {

    public static String getAPIKey(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_api_key_key),
                "");
    }
//    public setAPIKey(Context context, String apiKey) {
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//        prefs.edit().putString(context.getString(R.string.pref_api_key_key), apiKey);
//    }

    public static String getHostAddress(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_host_address_key),
                "");
    }
    public static String getServerPort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_server_port_key),
                "");
    }

    public static String convertArrayToString(ArrayList<String> array){
        String strSeparator = "__,__";
        String str = "";
        for (int i = 0;i<array.size(); i++) {
            str = str+ array.get(i);
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str = str+strSeparator;
            }
        }
        Log.v("Utility Task", "Converted Array to string " + str);
        return str;
    }
    public static String[] convertStringToArray(String str){
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        return arr;
    }

}
