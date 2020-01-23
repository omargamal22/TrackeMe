package com.example.trackeme.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface DatabaseDao {

    @Query("SELECT * FROM BindedPoints")
    List<posted_point> get();

    @Query("SELECT COUNT(*) from BindedPoints")
    LiveData<Integer> getCOUNT();

    @Insert
    void insertpoint(posted_point point);

    @Insert
    void insertPoints(List<posted_point> points);

    @Insert
    void InsertAll(List<posted_point> Ps);
    @Delete
    void DeletePoint(posted_point p);
    @Query("Delete FROM BindedPoints")
    void deletALL();
}
