package com.qwerteach.wivi.qwerteachapp.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qwerteach.wivi.qwerteachapp.common.Common;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wivi on 20/01/17.
 */

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {

            Gson gson = new GsonBuilder().setLenient().create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Common.IP_ADDRESS + "/api/")
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
