package com.iesfernandowirtz.clothesvault.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Cliente {
    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    public static Retrofit getCliente(String url){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }
}
