package com.example.testchat.Services;


import com.example.testchat.Models.Person;
import com.example.testchat.Models.Result;
import com.example.testchat.Models.User;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIServices {
    @Multipart
    @POST("users/register")
    Call<Result> registration(@Part MultipartBody.Part file,
                              @Part("email") RequestBody email,
                              @Part("password") RequestBody password,
                              @Part("fullName") RequestBody fullName,
                              @Part("phone") RequestBody phone
    );
    @POST("users/login")
    Call<Result> login(@Body User user);
    @GET("users/test")
    Call<Result> getData();
    @GET("users/profile")
    Call<Person> getDataPerson(@Header("Authorization")String token);
}
