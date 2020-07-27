package com.example.covidcases.repository.remote.api;

import com.example.covidcases.util.ApplicationConstants;
import com.example.covidcases.util.RetrofitUtil;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface CovidApi {

    CovidApi instance = new Retrofit.Builder()
            .baseUrl(ApplicationConstants.API_BASE_URL)
            .client(RetrofitUtil.getClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(CovidApi.class);

    @GET
    Call<JsonObject> getCovidCases(@Url String url);

}
