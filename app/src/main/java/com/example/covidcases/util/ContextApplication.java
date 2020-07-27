package com.example.covidcases.util;

import android.app.Application;
import android.content.Context;

public class ContextApplication extends Application{

    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public static Context getContext() {
        return application.getApplicationContext();
    }

}
