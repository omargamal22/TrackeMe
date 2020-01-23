package com.example.trackeme;

import android.app.Application;
import android.graphics.ColorSpace;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.trackeme.Model.DataBase;
import com.example.trackeme.Model.posted_point;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PostsViewModel extends AndroidViewModel {
    LiveData<Integer> listLiveData;
    public PostsViewModel(@NonNull Application application) {
        super(application);
        listLiveData = DataBase.getInstance(getApplication()).Data_Base_quaries().getCOUNT();
    }
    public LiveData<Integer> getListLiveData(){
        return listLiveData;
    }
}
