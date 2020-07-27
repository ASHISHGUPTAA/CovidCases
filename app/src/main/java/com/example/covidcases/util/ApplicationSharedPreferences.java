package com.example.covidcases.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ApplicationSharedPreferences {

    private SharedPreferences sharedPreferences;

    public ApplicationSharedPreferences(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void putListString(String key, ArrayList<String> stringList) {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        this.sharedPreferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public ArrayList<String> getListString(String key) {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(this.sharedPreferences.getString(key, ""), "‚‗‚")));
    }

    public void set(Map<String, Object> map) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (e.getValue() instanceof String) {
                editor.putString(e.getKey(), e.getValue().toString());
            } else if (e.getValue() instanceof Double || e.getValue() instanceof Float) {
                editor.putFloat(e.getKey(), Float.parseFloat(e.getValue().toString()));
            } else if (e.getValue() instanceof Long) {
                editor.putLong(e.getKey(), (Long) e.getValue());
            }
        }
        editor.apply();
    }

    private Map<String, ?> getAll() {
        return this.sharedPreferences.getAll();
    }

    public Object get(String key) {
        return this.getAll().get(key);
    }

    public void put(String key, Object value) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, value.toString());
        } else if (value instanceof Double || value instanceof Float) {
            editor.putFloat(key, Float.parseFloat(value.toString()));
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    public void remove(String key) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    private void checkForNullKey(String key) {
        if (key == null) {
            throw new NullPointerException();
        }
    }

    public Boolean contains(String key) {
        return sharedPreferences.contains(key);
    }
}
