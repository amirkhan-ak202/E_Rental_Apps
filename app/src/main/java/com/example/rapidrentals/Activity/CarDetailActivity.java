package com.example.rapidrentals.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapidrentals.DataModel.Booking;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.DataModel.User;
import com.example.rapidrentals.DataModel.UserDao;
import com.example.rapidrentals.Helper.GlideApp;
import com.example.rapidrentals.R;
import com.example.rapidrentals.Utility.ProcessManager;
import com.google.firebase.storage.StorageReference;

public class CarDetailActivity extends AppCompatActivity {

    public static final String CAR_ID = "CAR_ID";

    private String carId;

    private ProcessManager processManager;

    private ImageView image, backBtn;
    private TextView brand_model, year_type, rent, fuel, transmission, seat, location, reg, owner, contact;
    private Button boonBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_detail);

        initComponents();

    }

    private void initComponents() {
        // Hooks
        image = findViewById(R.id.car_image);
        brand_model = findViewById(R.id.car_brand_model);
        year_type = findViewById(R.id.car_year_type);
        rent = findViewById(R.id.car_rent);
        fuel = findViewById(R.id.car_fuel);
        transmission = findViewById(R.id.car_transmission);
        seat = findViewById(R.id.car_seat);
        location = findViewById(R.id.car_location);
        reg = findViewById(R.id.car_reg);
        owner = findViewById(R.id.car_owner);
        contact = findViewById(R.id.car_contact);
        backBtn = findViewById(R.id.backButton);

        processManager = new ProcessManager(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            carId = extras.getString(CAR_ID);
        } else {
            Toast.makeText(getApplicationContext(), "Car Id not found!", Toast.LENGTH_SHORT).show();
            finish();
        }

        retrieveCarInformation();

    }

    private void retrieveCarInformation() {
        processManager.incrementProcessCount();
        Car.getCarById(carId, new CarDao() {
            @Override
            public void getCar(Car car) {
                if(car != null) {
                    StorageReference reference = Car.getStorageReference().child(car.getId()).child(Car.getFileName());
                    GlideApp.with(getApplicationContext())
                            .load(reference)
                            .placeholder(R.drawable.preview)
                            .error(R.drawable.preview)
                            .centerCrop()
                            .into(image);
                    brand_model.setText(String.format("%s, %s", car.getBrand(), car.getModel()));
                    year_type.setText(String.format("%d • %s", car.getYear(), car.getType()));
                    rent.setText(String.format("Rs.%d", car.getRentPerDay()));
                    fuel.setText(car.getFuel());
                    transmission.setText(car.getTransmission());
                    seat.setText((car.getNumOfSeats()+ " Seats"));
                    reg.setText(car.getRegNumber());
                    processManager.incrementProcessCount();
                    User.getUserById(car.getOwner(), new UserDao() {
                        @Override
                        public void getUser(User user) {
                            if (user != null) {
                                owner.setText(user.getFullName());
                                contact.setText(user.getPhoneNumber());
                            }
                            processManager.decrementProcessCount();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Car not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                processManager.decrementProcessCount();
            }
        });
    }

    public void onClickBook(View view) {
        Intent intent = new Intent(getApplicationContext(), CarBookActivity.class);
        Bundle extras = new Bundle();
        extras.putString(CarBookActivity.CAR_ID,carId);
        extras.putString(CarBookActivity.BOOK_OPERATION, CarBookActivity.BOOK_ADD);
        intent.putExtras(extras);
        startActivity(intent);
    }

    public void onClickBack(View view) {
        onBackPressed();
    }
}