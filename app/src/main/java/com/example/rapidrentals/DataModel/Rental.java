package com.example.rapidrentals.DataModel;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Rental {
    // Database
    private static final String RENTALS_DB = "Rentals";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(RENTALS_DB);

    // Fields
    private String id;
    private String carId;
    private String ownerId;
    private float rentPerDay;
    private boolean carAvailable;
    private boolean driverAvailable;
    private Location location;


}
