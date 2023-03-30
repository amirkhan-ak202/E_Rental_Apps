package com.example.rapidrentals.Helper;

public class BookingHelper {

    //Fields
    private String id;
    private String brand_model;
    private String year_type;
    private int rentPerDay;
    private long from;
    private long until;
    private String status;
    private float fare;

    // Constructor
    public BookingHelper(String id, String brand_model, String year_type, int rentPerDay, long from, long until, String status, float fare) {
        this.id = id;
        this.brand_model = brand_model;
        this.year_type = year_type;
        this.rentPerDay = rentPerDay;
        this.from = from;
        this.until = until;
        this.status = status;
        this.fare = fare;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getBrand_model() {
        return brand_model;
    }

    public String getYear_type() {
        return year_type;
    }

    public int getRentPerDay() {
        return rentPerDay;
    }

    public long getFrom() {
        return from;
    }

    public long getUntil() {
        return until;
    }

    public String getStatus() {
        return status;
    }

    public float getFare() {
        return fare;
    }
}
