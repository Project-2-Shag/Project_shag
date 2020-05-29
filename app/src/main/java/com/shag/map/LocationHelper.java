package com.shag.map;

public class LocationHelper {

    private double longitude;
    private double latitude;
    private String way;
    private String issharing;

    public LocationHelper() {

    }

    public LocationHelper(String way, double latitude, double longitude, String issharing)
    {
        this.way = way;
        this.latitude = latitude;
        this.longitude = longitude;
        this.issharing = issharing;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getWay() {
        return way;
    }

    public String getIssharing() {
        return issharing;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public void setIssharing(String issharing) {
        this.issharing = issharing;
    }
}
