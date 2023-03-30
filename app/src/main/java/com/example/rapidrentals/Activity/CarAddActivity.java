package com.example.rapidrentals.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.GlideApp;
import com.example.rapidrentals.R;
import com.example.rapidrentals.Utility.LoadingDialog;
import com.example.rapidrentals.Utility.ProcessManager;
import com.example.rapidrentals.Utility.Validation;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class CarAddActivity extends AppCompatActivity {

    public static final String CAR_OPERATION = "CAR_OPERATION";
    public static final String CAR_ADD = "CAR_ADD";
    public static final String CAR_UPDATE = "CAR_UPDATE";
    public static final String CAR_ID = "CAR_ID";

    private String carOperation;
    private String carId;

    private final int PICK_GALLERY = 101;

    private TextInputLayout brand, model, type, fuel, transmission, seat, year, reg, rent;
    private AutoCompleteTextView typeAtv, fuelAtv, transmissionAtv;
    private SwitchMaterial carAvailableSwitch, driverAvailableSwitch, locationSwitch;

    private AppCompatImageView carImageView;
    private Uri carImageUri;

    private ProcessManager processManager;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_add);

        initComponents();

    }

    private void initComponents() {

        processManager = new ProcessManager(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            carOperation = extras.getString(CAR_OPERATION);
            if (carOperation.equals(CAR_UPDATE)) {
                carId = extras.getString(CAR_ID);
                retrieveCarInformation();
            }
        } else {
            carOperation = "";
        }

        carImageView = findViewById(R.id.imgGallery);
        brand = findViewById(R.id.car_brand_layout);
        model = findViewById(R.id.car_model_layout);
        type = findViewById(R.id.car_type_layout);
        typeAtv = findViewById(R.id.car_type_atv);
        fuel = findViewById(R.id.car_fuel_layout);
        fuelAtv = findViewById(R.id.car_fuel_atv);
        transmission = findViewById(R.id.car_transmisison_layout);
        transmissionAtv = findViewById(R.id.car_transmission_atv);
        seat = findViewById(R.id.car_seat_layout);
        year = findViewById(R.id.car_year_layout);
        reg = findViewById(R.id.car_reg_layout);
        rent = findViewById(R.id.car_rent_layout);
        carAvailableSwitch = findViewById(R.id.car_available_switch);
        driverAvailableSwitch = findViewById(R.id.driver_available_switch);
        locationSwitch = findViewById(R.id.car_location_switch);

        typeAtv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.car_type)));
        fuelAtv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.car_fuel)));
        transmissionAtv.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.car_transmission)));
    }

    private void retrieveCarInformation() {
        processManager.incrementProcessCount();
        Car.getCarById(carId, new CarDao() {
            @Override
            public void getCar(Car car) {
                if (car != null) {
                    brand.getEditText().setText(car.getBrand());
                    model.getEditText().setText(car.getModel());
                    typeAtv.setText(car.getType());
                    fuelAtv.setText(car.getFuel());
                    transmissionAtv.setText(car.getTransmission());
                    year.getEditText().setText(String.valueOf(car.getYear()));
                    seat.getEditText().setText(String.valueOf(car.getNumOfSeats()));
                    reg.getEditText().setText(car.getRegNumber());
                    rent.getEditText().setText(String.valueOf(car.getRentPerDay()));
                    carAvailableSwitch.setChecked(car.isCarAvailable());
                    driverAvailableSwitch.setChecked(car.isDriverAvailable());

                    StorageReference reference = Car.getStorageReference().child(car.getId()).child(Car.getFileName());

                    GlideApp.with(getApplicationContext())
                            .load(reference)
                            .centerCrop()
                            .into(carImageView);

                }
                processManager.decrementProcessCount();
            }
        });
    }

    public void pickGalleryImage(View view) {
        ImagePicker.with(this)
                .galleryOnly()
                .crop(16f, 9f)
                .compress(1024)
                .start(PICK_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_GALLERY && resultCode == RESULT_OK && data != null) {
            carImageUri = data.getData();
            carImageView.setImageURI(carImageUri);
        }

    }

    public void onClickAddCar(View view) {
        processManager.incrementProcessCount();

        // Validation
        if (!validateCarDetails()) {
            Toast.makeText(getApplicationContext(), "Validation Failed", Toast.LENGTH_LONG).show();
            processManager.decrementProcessCount();
            return;
        }

        //Initialize Object
        Car car = new Car();
        car.setId(carOperation.equals(CAR_UPDATE) ? carId : Car.generateCarId());
        car.setOwner(currentUser.getUid());
        car.setBrand(brand.getEditText().getText().toString().trim());
        car.setModel(model.getEditText().getText().toString().trim());
        car.setFuel(fuelAtv.getText().toString().trim());
        car.setType(typeAtv.getText().toString().trim());
        car.setTransmission(transmissionAtv.getText().toString().trim());
        car.setRegNumber(reg.getEditText().getText().toString().trim());
        car.setNumOfSeats(Integer.parseInt(seat.getEditText().getText().toString().trim()));
        car.setYear(Integer.parseInt(year.getEditText().getText().toString().trim()));
        car.setRentPerDay(Integer.parseInt(rent.getEditText().getText().toString().trim()));
        car.setCarAvailable(carAvailableSwitch.isChecked());
        car.setDriverAvailable(driverAvailableSwitch.isChecked());

        // Add to Firebase
        car.addCar(new CarDao() {
            @Override
            public void getBoolean(Boolean result) {
                if (result) {
                    Toast.makeText(getApplicationContext(), "Car Updated", Toast.LENGTH_SHORT).show();
                    if (carImageUri != null) {
                        processManager.incrementProcessCount();
                        car.uploadCarImage(carImageUri, new CarDao() {
                            @Override
                            public void getBoolean(Boolean result) {
                                if (result) {
                                    Toast.makeText(getApplicationContext(), "Image Updated", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                }
                                processManager.decrementProcessCount();
                            }
                        });
                    } else {
                        finish();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                }
                processManager.decrementProcessCount();
            }
        });
    }

    public void onClickCancel(View view) {
        onBackPressed();
    }

    private boolean validateCarDetails() {
        return Validation.validateEmpty(brand)
                & Validation.validateEmpty(model)
                & Validation.validateDropDown(type, typeAtv)
                & Validation.validateDropDown(fuel, fuelAtv)
                & Validation.validateDropDown(transmission, transmissionAtv)
                & Validation.validateEmpty(year)
                & Validation.validateEmpty(reg)
                & Validation.validateEmpty(seat)
                & Validation.validateEmpty((rent));
    }

}