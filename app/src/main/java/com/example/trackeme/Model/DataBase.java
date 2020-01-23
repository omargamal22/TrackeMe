package com.example.trackeme.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {posted_point.class},version = 1,exportSchema = false)
public abstract  class DataBase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DatabaseName = "MovieDB";
    private static DataBase MDB ;
    public static  DataBase getInstance(Context context){
        if(MDB == null){
            synchronized (LOCK){
                MDB = Room.databaseBuilder(context.getApplicationContext(),DataBase.class,DatabaseName).build();
            }
        }
        return MDB;
    }
    public abstract DatabaseDao Data_Base_quaries();
}

