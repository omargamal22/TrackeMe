package com.example.trackeme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.trackeme.Model.DataBase;

public class SHUT_DOWN_RECIVER extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BackgroundService.CheckPointsToPost.size()>0) {
            DataBase dataBase = DataBase.getInstance(context);
            dataBase.Data_Base_quaries().insertPoints(BackgroundService.CheckPointsToPost);
        }
    }
}
