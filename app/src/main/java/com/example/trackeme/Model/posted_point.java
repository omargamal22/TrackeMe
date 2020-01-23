package com.example.trackeme.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
@Entity(tableName = "BindedPoints")
public class posted_point extends StopPoint {
    @PrimaryKey(autoGenerate = true)
    int id;
   @Expose
   String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public posted_point(double lat, double aLong, int id, String time) {
        super(lat, aLong);
        this.id = id;
        this.time = time;
    }

    public posted_point(int id, String time) {
        this.id = id;
        this.time = time;
    }

    public posted_point(StopPoint p, int id, String time) {
        super(p);
        this.id = id;
        this.time = time;
    }

    public posted_point(double lat, double aLong, String time) {
        super(lat, aLong);
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
