package com.example.covidcases.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActivityUtil implements ApplicationConstants {

    public static void checkFilter(ApplicationSharedPreferences applicationSharedPreferences, LinearLayout filterLayout, TextView filterName) {
        if (applicationSharedPreferences.contains(SP_DYNAMIC_NAME)) {
            filterLayout.setVisibility(View.VISIBLE);
            filterName.setText((String) applicationSharedPreferences.get(SP_DYNAMIC_NAME));
        } else {
            filterLayout.setVisibility(View.VISIBLE);
        }
    }

    public static void removeAllCases(ApplicationSharedPreferences applicationSharedPreferences) {
        applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_GREATER_THAN);
        applicationSharedPreferences.remove(SP_TOTAL_CONFIRM_LESS_THAN);
        applicationSharedPreferences.remove(SP_TOTAL_DEATH_GREATER_THAN);
        applicationSharedPreferences.remove(SP_TOTAL_DEATH_LESS_THAN);
        applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_GREATER_THAN);
        applicationSharedPreferences.remove(SP_TOTAL_RECOVERED_LESS_THAN);

    }

    public static void removeAllColumnData(ApplicationSharedPreferences applicationSharedPreferences) {
        applicationSharedPreferences.remove(SP_COUNTRY_BY_ASC);
        applicationSharedPreferences.remove(SP_COUNTRY_BY_DESC);
        applicationSharedPreferences.remove(SP_DEATH_BY_ASC);
        applicationSharedPreferences.remove(SP_DEATH_BY_DESC);
        applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_ASC);
        applicationSharedPreferences.remove(SP_TOTAL_CASE_BY_DESC);
        applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_ASC);
        applicationSharedPreferences.remove(SP_TOTAL_RECOVER_BY_DESC);
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static AlertDialog createAlertDialog(String message, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Error Message!")
                .setMessage(message)
                .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });
        return dialog.create();
    }

}
