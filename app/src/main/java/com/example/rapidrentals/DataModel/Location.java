package com.example.rapidrentals.DataModel;

public class Location {

    private double latitude;
    private double longitude;
    private String address;

    public Location() {
    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double computeDistance(Location location) {
        int r = 6371; // average radius of the earth in km
        double dLat = Math.toRadians(location.getLatitude() - latitude);
        double dLon = Math.toRadians(location.getLongitude() - longitude);
        double a;
        a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(location.getLatitude())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = r * c;
        return Math.round(distance * 100 / 100);
    }

    public static double computeDistance(Location location1, Location location2) {
        return location1.computeDistance(location2);
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", address='" + address + '\'' +
                '}';
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

