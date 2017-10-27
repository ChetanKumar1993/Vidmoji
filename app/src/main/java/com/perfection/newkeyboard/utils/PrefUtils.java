package com.perfection.newkeyboard.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrefUtils {

    public static final String SHARED_PREF_NAME = "emoji_pref";
    public static final String IS_LOGGEDIN = "is_loggedin";
    public static final String IS_EXTERNAL_STORAGE = "is_externale_storage";
    public static final String USER_ID = "userId";
    public static final String WIFI_ENABLE = "wifi_enable";
    public static final String IS_SETTING_WIFI = "setting_wifi";
    public static final String USER_NAME = "userName";
    public static final String PASSWORD = "password";
    public static final String USERMODE = "UserMode";
    public static final String IS_ACTIVE = "isActive";

    public static final String DOWNLOAD_ON_WIFI = "DOWNLOAD_ON_WIFI";
    public static final String SUGGEST_VIDMOGI = "SUGGEST_VIDMOGI";

    private static SharedPreferences sharedPreferences;

    private static List<String> mValueList = new ArrayList<String>();
    public static String RESIDENTIAL_FILTER_DATA = "residential_filter_data";
    public static final String COMMERCIAL_FILTER_DATA = "commercial_filter_data";

    public static void loginUser(Context context, String userId, int mode, String isActive) {
        setSharedPrefBooleanData(context, PrefUtils.IS_LOGGEDIN, true);
        setSharedPrefStringData(context, PrefUtils.USER_ID, userId);
        setSharedPrefIntData(context, PrefUtils.USERMODE, mode);
        setSharedPrefStringData(context, PrefUtils.IS_ACTIVE, isActive);
    }

    public static void logoutUser(Context context) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        HashSet<String> hashSet = new HashSet<>();
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
    }

    public static void setSharedPrefStringData(Context context, String key, String value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putString(key, value);
        appInstallInfoEditor.commit();
    }

    public static ArrayList getSharedPrefListData(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String localKey;
        ArrayList<String> list = new ArrayList<String>();
        if (mValueList != null)
            for (int i = 0; i < mValueList.size(); i++) {
                localKey = key + (i + 1);
                list.add(userAcountPreference.getString(localKey, ""));
            }
        return list;
    }

    public static boolean getSharedPrefBoolean(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getBoolean(key, false);
    }

    public static String getSharedPrefString(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getString(key, "");
    }

    public static float getSharedPrefFloat(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getFloat(key, 0);
    }

    public static Long getSharedPrefLong(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getLong(key, 0);
    }

    public static int getSharedPrefInt(Context context, String key) {
        SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        return userAcountPreference.getInt(key, 0);
    }

    public static void setSharedPrefBooleanData(Context context, String key, boolean value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putBoolean(key, value);
        appInstallInfoEditor.commit();
    }

    public static void setSharedPrefFloatData(Context context, String key, float value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putFloat(key, value);
        appInstallInfoEditor.commit();

    }

    public static void setSharedPrefLongData(Context context, String key, Long value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putLong(key, value);
        appInstallInfoEditor.commit();

    }

    public static void setSharedPrefIntData(Context context, String key, int value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putInt(key, value);
        appInstallInfoEditor.commit();
    }

    public static void setSharedPrefHashSetData(Context context, String key, Set<String> value) {
        SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
        appInstallInfoEditor.putStringSet(key, value);
        appInstallInfoEditor.commit();
    }

    public static Set<String> getSharedPrefHashSetData(Context context, String key) {
        try {
            SharedPreferences userAcountPreference = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
            return userAcountPreference.getStringSet(key, new HashSet<String>());
        } catch (Exception e) {
            return new HashSet<String>();
        }
    }
//
//    public static void setSharedPrefCustomObj(Context context, String key, Object obj){
//
//            SharedPreferences appInstallInfoSharedPref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
//            SharedPreferences.Editor appInstallInfoEditor = appInstallInfoSharedPref.edit();
//            Gson gson = new Gson();
//            String json = gson.toJson(obj);
//            appInstallInfoEditor.putString(key, json);
//            appInstallInfoEditor.commit();
//
//
//    }
//
//    public static <T extends Object> T getSharedPrefCustomeObj(Context context, String key, Class<T> classType){
//        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        Gson gson=new Gson();
//        String json=pref.getString(key,"");
//        return gson.fromJson(json,classType);
//    }

    private static SharedPreferences.Editor getEditor() {
        return sharedPreferences.edit();
    }


    public static void clearAll(Context context) {
        SharedPreferences settings = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        settings.edit().clear().commit();
    }

}
