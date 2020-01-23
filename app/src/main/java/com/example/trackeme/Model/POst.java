package com.example.trackeme.Model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class POst {
    @Expose
    int ID;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Expose
    ArrayList<posted_point> stops;


    public ArrayList<posted_point> getStops() {
        return stops;
    }

    public void setStops(ArrayList<posted_point> stops) {
        this.stops = stops;
    }

    public POst(int id ,ArrayList<posted_point> stops) {
        this.ID = id;
        this.stops = stops;
    }

    public POst() {
        stops = new ArrayList<>();
    }
}
