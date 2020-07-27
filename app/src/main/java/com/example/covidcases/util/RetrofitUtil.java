package com.example.covidcases.util;

import android.content.Context;

import com.example.covidcases.repositoryconfig.CovidDatabase;
import com.example.covidcases.util.interceptor.HttpInterceptor;

import java.util.concurrent.TimeUnit;

import androidx.room.Room;
import okhttp3.OkHttpClient;

public class RetrofitUtil {

    private static final String DATABASE_NAME = "covidCases.db";

    public static OkHttpClient getClient() {
        return new OkHttpClient.Builder().readTimeout(1000, TimeUnit.SECONDS).writeTimeout(1000, TimeUnit.SECONDS).connectTimeout(1000, TimeUnit.SECONDS).addInterceptor(new HttpInterceptor()).build();
    }

    public static CovidDatabase getDatabase(Context context) {
        return Room.databaseBuilder(context, CovidDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
    }

}
