package com.example.rapidrentals.Helper;

public class CarHelper {
    private int image;
    private String id;
    private String brand_model;
    private String year_type;
    private String location;
    private String fuel;
    private int rentPerDay;

    public CarHelper(int image, String id, String brand_model, String year_type, String location, String fuel, int rentPerDay) {
        this.image = image;
        this.id = id;
        this.brand_model = brand_model;
        this.year_type = year_type;
        this.location = location;
        this.fuel = fuel;
        this.rentPerDay = rentPerDay;
    }

    public int getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getBrand_model() {
        return brand_model;
    }

    public String getYear_type() {
        return year_type;
    }

    public String getLocation() {
        return location;
    }

    public String getFuel() {
        return fuel;
    }

    public int getRentPerDay() {
        return rentPerDay;
    }
}
