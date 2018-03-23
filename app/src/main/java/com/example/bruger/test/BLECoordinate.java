package com.example.bruger.test;

/**
 * Created by Bruger on 22-03-2018.
 */

public class BLECoordinate {
    private String name = "";
    private double lat,lng,distance;

    public BLECoordinate(String name, double lat, double lng) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public BLECoordinate(String name, double lat, double lng, double distance){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }







}
