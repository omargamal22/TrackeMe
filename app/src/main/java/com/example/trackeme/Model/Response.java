package com.example.trackeme.Model;

import java.util.ArrayList;

public class Response {
    public String stutes;
    public String CarNumber;
    public ArrayList<StopPoint>stopPoints ;
    public String Name;

    static Response res ;
    public static Response getInstance(){
        if(res == null){
            res = new Response();
        }
        return res;
    }

    public Response() {
    }

    public String getStutes() {
        return stutes;
    }

    public void setStutes(String stutes) {
        this.stutes = stutes;
    }

    public String getCarNumber() {
        return CarNumber;
    }

    public void setCarNumber(String carNumber) {
        CarNumber = carNumber;
    }

    public ArrayList<StopPoint> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(ArrayList<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Response(String stutes, String carNumber, ArrayList<StopPoint> stopPoints , String name) {
        this.stutes = stutes;
        CarNumber = carNumber;
        this.stopPoints = stopPoints;
        Name = name;
    }

    public void AddStopPoint(StopPoint stopPoint) {
        this.stopPoints.add(stopPoint);
    }

}
