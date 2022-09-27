package com.example.ghichu.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    ApiService apiService = new Retrofit.Builder().baseUrl("http://localhost:3001/noteApp/").addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiService.class);



}

