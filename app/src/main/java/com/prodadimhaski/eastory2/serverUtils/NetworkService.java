package com.prodadimhaski.eastory2.serverUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {
    private static NetworkService mInstance;
    private static final String BASE_URL = "http://174.138.102.149:8080";
    private Retrofit mRetrofit;

    private NetworkService() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();



    }

    public JSONApi getJSONApi() {
        return mRetrofit.create(JSONApi.class);
    }

    public static NetworkService getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkService();
        }
        return mInstance;
    }

    public static void destroy() {
        if (mInstance != null)
            mInstance = null;
    }

}
