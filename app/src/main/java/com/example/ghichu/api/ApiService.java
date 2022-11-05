package com.example.ghichu.api;


import com.example.ghichu.models.NoteModel;
import com.example.ghichu.models.UserModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    //using ip myself
    ApiService apiService = new Retrofit.Builder().baseUrl("https://note-app-android.herokuapp.com/v1/").addConverterFactory(GsonConverterFactory.create(gson)).build().create(ApiService.class);

    //HTTP
    @GET("users")
    Call<List<UserModel>> getUsers();

    @GET("notesU/{id}")
    Call<List<NoteModel>> getNotesU(@Path("id") String Id);

    @POST("users")
    Call<UserModel> addUser(@Body UserModel userModel);

    @GET("notes")
    Call<List<NoteModel>> getAllNotes();

    @GET("notes/{Id}")
    Call<NoteModel> getNote(@Path("Id") Integer Id);

    @POST("notes")
    Call<NoteModel> addNote(@Body NoteModel noteModel);

    @PUT("notes")
    Call<NoteModel> updateNote(@Body NoteModel note);

    @PUT("notes/{Id}")
    Call<NoteModel> updatePinned(@Path("Id") Integer Id);

    @DELETE("notes/{Id}")
    Call<Void> deleteNote(@Path("Id") Integer Id);
}

