
package com.refactech.driibo.util;

import com.refactech.driibo.AppData;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    public static String getPrefString(final String key, final String defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(final String key, final String value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        settings.edit().putString(key, value).commit();
    }

    public static boolean getPrefBoolean(final String key, final boolean defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        return settings.getBoolean(key, defaultValue);
    }

    public static boolean hasKey(final String key) {
        return PreferenceManager.getDefaultSharedPreferences(AppData.getContext()).contains(key);
    }

    public static void setPrefBoolean(final String key, final boolean value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        settings.edit().putBoolean(key, value).commit();
    }

    public static void setPrefInt(final String key, final int value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        settings.edit().putInt(key, value).commit();
    }

    public static void increasePrefInt(final String key) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        increasePrefInt(settings, key);
    }

    public static void increasePrefInt(final SharedPreferences sp, final String key) {
        final int v = sp.getInt(key, 0) + 1;
        sp.edit().putInt(key, v).commit();
    }

    public static void increasePrefInt(final SharedPreferences sp, final String key,
            final int increment) {
        final int v = sp.getInt(key, 0) + increment;
        sp.edit().putInt(key, v).commit();
    }

    public static int getPrefInt(final String key, final int defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefFloat(final String key, final float value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        settings.edit().putFloat(key, value).commit();
    }

    public static float getPrefFloat(final String key, final float defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        return settings.getFloat(key, defaultValue);
    }

    public static void setSettingLong(final String key, final long value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        settings.edit().putLong(key, value).commit();
    }

    public static long getPrefLong(final String key, final long defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        return settings.getLong(key, defaultValue);
    }

    public static void increasePrefLong(final SharedPreferences sp, final String key,
            final long increment) {
        final long v = sp.getLong(key, 0) + increment;
        sp.edit().putLong(key, v).commit();
    }

    public static void removePreference(final String key) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(AppData
                .getContext());
        prefs.edit().remove(key).commit();
    }

    public static void clearPreference(final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.commit();
    }
}
