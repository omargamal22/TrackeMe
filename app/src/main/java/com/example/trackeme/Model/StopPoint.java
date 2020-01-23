package com.example.trackeme.Model;

import com.google.gson.annotations.Expose;

public class StopPoint {
    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLong() {
        return Long;
    }

    public void setLong(double aLong) {
        Long = aLong;
    }

    public StopPoint(double lat, double aLong) {
        Lat = lat;
        Long = aLong;
    }

    public StopPoint() {
    }
    public StopPoint(StopPoint p) {
        this.Lat = p.getLat();
        this.Long = p.getLong();
    }

    @Expose
    public double Lat,Long;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    @Expose
    public  String Name;

}
