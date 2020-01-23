package com.example.trackeme.Networking;



import androidx.lifecycle.MutableLiveData;

import com.example.trackeme.Model.POSTRESPONSE;
import com.example.trackeme.Model.POst;
import com.example.trackeme.Model.StopPoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_Service  {


    public static MutableLiveData<String> res = new MutableLiveData<>() ;
    public static MutableLiveData<String> POST_res = new MutableLiveData<>() ;
    private static String E;

    public static void Get_Retrofit_Service(String user , String pass){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);
        Map<String,String> prams = new HashMap<>();
        prams.put("user_name",user);
        prams.put("pass_word",pass);
        Call<com.example.trackeme.Model.Response> call_ = api.GET_ResponseCall(prams);
        call_.enqueue(new Callback<com.example.trackeme.Model.Response>() {
            @Override
            public void onResponse(Call<com.example.trackeme.Model.Response> call, Response<com.example.trackeme.Model.Response> response) {
                com.example.trackeme.Model.Response response1 = response.body();
                com.example.trackeme.Model.Response.getInstance().Name = response1.Name;
                com.example.trackeme.Model.Response.getInstance().CarNumber = response1.CarNumber;
                com.example.trackeme.Model.Response.getInstance().stutes = response1.stutes;
                com.example.trackeme.Model.Response.getInstance().stopPoints = response1.stopPoints;
                res.setValue(response1.getStutes());
            }

            @Override
            public void onFailure(Call<com.example.trackeme.Model.Response> call, Throwable t) {
                res.setValue(t.getMessage());
            }
        });
        return;
    }
    public static void Post_poit(POst post){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        API api = retrofit.create(API.class);
        Call<POSTRESPONSE> call_ = api.Post_Stop(post);
        call_.enqueue(new Callback<POSTRESPONSE>() {
            @Override
            public void onResponse(Call<POSTRESPONSE> call, Response<POSTRESPONSE> response) {
                POSTRESPONSE postresponse = response.body();
                if(postresponse.getStatus().equals("OK")){
                    POST_res.setValue(String.valueOf(postresponse.getPOST_ID()));
                }
            }

            @Override
            public void onFailure(Call<POSTRESPONSE> call, Throwable t) {
                E=t.getMessage();
                POST_res.setValue("ERROR");
            }
        });
        return;
    }
}
