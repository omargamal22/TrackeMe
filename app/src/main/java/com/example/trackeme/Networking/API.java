package com.example.trackeme.Networking;

import com.example.trackeme.Model.POSTRESPONSE;
import com.example.trackeme.Model.POst;
import com.example.trackeme.Model.Response;
import com.example.trackeme.Model.StopPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface API {
    String BASE_URL = "http://192.168.1.12:8080/JavaAPI/rest/";

    @GET("user")
    Call<Response> GET_ResponseCall(@QueryMap Map<String,String> n );
    @POST("user")
    Call<POSTRESPONSE> Post_Stop(@Body POst s);


}
