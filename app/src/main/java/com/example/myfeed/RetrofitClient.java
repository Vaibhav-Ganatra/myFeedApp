package com.example.myfeed;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL= "https://2factor.in/API/V1/";
    private static Retrofit retrofit;
    private static RetrofitClient mInstance;

    private RetrofitClient() {
        retrofit= new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }
    public static synchronized RetrofitClient getInstance() {
        if(mInstance==null)
            mInstance= new RetrofitClient();
        return mInstance;

    }

    public Api getApi(){
        return retrofit.create(Api.class);

    }
}
