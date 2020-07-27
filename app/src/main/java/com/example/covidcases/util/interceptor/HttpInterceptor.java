package com.example.covidcases.util.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HttpInterceptor implements Interceptor {

    private static final String KEY = "Authorization";

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
            request = request.newBuilder()
                    .addHeader(KEY, "Bearer " + "accessToken")
                    .build();

        return chain.proceed(request);
    }

}
