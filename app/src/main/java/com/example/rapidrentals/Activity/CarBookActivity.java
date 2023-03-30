package com.example.rapidrentals.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.rapidrentals.DataModel.Booking;
import com.example.rapidrentals.DataModel.BookingDao;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.GlideApp;
import com.example.rapidrentals.R;
import com.example.rapidrentals.Utility.ProcessManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CarBookActivity extends AppCompatActivity {

    public static final String BOOK_OPERATION = "BOOK_OPERATION";
    public static final String BOOK_ADD = "BOOK_ADD";
    public static final String BOOK_MODIFY = "BOOK_MODIFY";
    public static final String BOOK_ID = "BOOK_ID";
    public static final String CAR_ID = "CAR_ID";

    private String bookOperation, bookId, carId;
    private FirebaseUser currentUser;
    private Car currentCar;
    private Booking currentBooking;
    private Boolean isAvailable;

    private  Calendar fromDate;
    private  Calendar untilDate;


    private ProcessManager processManager;

    private ImageView image,checkIcon;
    private TextView brand_model, year_type, rent, checkBtn;
    private Button fromBtn, untilBtn;
    private SwitchMaterial driverSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_book);

        initComponents();

    }

    private void initComponents() {
        image = findViewById(R.id.car_image);
        brand_model = findViewById(R.id.car_brand_model);
        year_type = findViewById(R.id.car_year_type);
        rent = findViewById(R.id.car_rent);
        driverSwitch = findViewById(R.id.driver_availability_switch);
        checkIcon = findViewById(R.id.available_check_icon);
        checkBtn = findViewById(R.id.available_check_btn);
        fromBtn = findViewById(R.id.booking_from);
        untilBtn = findViewById(R.id.booking_until);

        processManager = new ProcessManager(this);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            bookOperation = extras.getString(BOOK_OPERATION);
            carId = extras.getString(CAR_ID);
            if (bookOperation.equals(BOOK_MODIFY)) {
                bookId = extras.getString(BOOK_ID);
                retrieveBookingInformation();
            } else {
                bookId = Booking.generateBookingId();
            }
            retrieveCarInformation();
        } else {
            Toast.makeText(getApplicationContext(), "Data Missing", Toast.LENGTH_SHORT).show();
            finish();
        }

        fromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fromDate == null)
                    fromDate = Calendar.getInstance();
                getDatePicker(fromBtn, fromDate);
            }
        });

        untilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (untilDate == null)
                    untilDate = Calendar.getInstance();
                getDatePicker(untilBtn, untilDate);
            }
        });

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBookingAvailability(view);
            }
        });

    }


    private void getDatePicker(Button button, Calendar calendar) {
        new DatePickerDialog(CarBookActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(i, i1, i2);
                getTimePicker(button, calendar);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE)).show();
    }

    private void getTimePicker(Button button, Calendar calendar) {

        new TimePickerDialog(CarBookActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.set(Calendar.HOUR_OF_DAY,i );
                calendar.set(Calendar.MINUTE,i1);
                calendar.set(Calendar.SECOND,0);
                button.setText(calendar.getTime().toString());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm a");
                String formatted = dateFormat.format(calendar.getTimeInMillis());
                button.setText(formatted);
            }
        }, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), false).show();
    }

    private void retrieveCarInformation() {
        processManager.incrementProcessCount();
        Car.getCarById(carId, new CarDao() {
            @Override
            public void getCar(Car car) {
                if (car != null) {
                    StorageReference reference = Car.getStorageReference().child(car.getId()).child(Car.getFileName());
                    GlideApp.with(getApplicationContext())
                            .load(reference)
                            .placeholder(R.drawable.preview)
                            .error(R.drawable.preview)
                            .centerCrop()
                            .into(image);
                    currentCar = car;
                    brand_model.setText(String.format("%s, %s", car.getBrand(), car.getModel()));
                    year_type.setText(String.format("%d • %s", car.getYear(), car.getType()));
                    rent.setText(String.format("Rs.%d", car.getRentPerDay()));
                } else {
                    Toast.makeText(getApplicationContext(), "Car not found!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                processManager.decrementProcessCount();
            }
        });
    }


    private void retrieveBookingInformation() {
        processManager.incrementProcessCount();
        Booking.getBookingById(bookId, new BookingDao() {
            @Override
            public void getBooking(Booking booking) {
                if (booking != null) {
                    currentBooking = booking;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm a");
                    fromBtn.setText(dateFormat.format(new Date(booking.getFrom())));
                    untilBtn.setText(dateFormat.format(new Date(booking.getUntil())));
                    driverSwitch.setChecked(booking.isDriver());
                } else {
                    Toast.makeText(getApplicationContext(), "Data not found", Toast.LENGTH_SHORT).show();
                    finish();
                }
                processManager.decrementProcessCount();
            }
        });
    }

    public void onClickBook(View view) {

        //Validation
        if (!validateTimeFields()) return;

        Long from = fromDate.getTimeInMillis();
        Long until = untilDate.getTimeInMillis();
        Boolean driverRequired = driverSwitch.isChecked();

        Booking booking = new Booking();
        booking.setId(bookId);
        booking.setCarId(currentCar.getId());
        booking.setProviderId(currentCar.getOwner());
        booking.setReceiverId(currentUser.getUid());
        booking.setFrom(from);
        booking.setUntil(until);
        booking.setDriver(driverRequired);
        booking.setStatus(Booking.UPCOMING);
        booking.setRentPerDay(currentCar.getRentPerDay());

        booking.addBooking(new BookingDao() {
            @Override
            public void getBoolean(Boolean result, String error) {
                if (result) {
                    Toast.makeText(getApplicationContext(), "Booking Updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Booking Failed! " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateTimeFields() {
        try {
            Long from = fromDate.getTimeInMillis();
            Long until = untilDate.getTimeInMillis();
            if (from >= until || from < new Date().getTime())
                throw new Exception("From is after Until");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Invalid Date Time", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void checkBookingAvailability(View view) {

        if (!validateTimeFields()) return;

        Long from = fromDate.getTimeInMillis();
        Long until = untilDate.getTimeInMillis();


        Booking.isAvailable(currentCar.getId(), from, until, new BookingDao() {
            @Override
            public void getBoolean(Boolean result, String error) {
                isAvailable = result;
                Toast.makeText(getApplicationContext(), result ? "Available" : error, Toast.LENGTH_SHORT).show();
                checkIcon.setVisibility(View.VISIBLE);
                checkIcon.setImageResource(result ? R.drawable.ic_baseline_done_24 : R.drawable.ic_baseline_close_24);
            }
        });

        isAvailable = false;

    }

    public void goBackButton(View view) {
        onBackPressed();
    }

}