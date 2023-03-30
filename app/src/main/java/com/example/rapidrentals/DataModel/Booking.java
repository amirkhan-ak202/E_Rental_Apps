package com.example.rapidrentals.DataModel;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {

    public static final String UPCOMING = "Upcoming";
    public static final String ON_PROGRESS = "On Progress";
    public static final String COMPLETED = "Completed";
    public static final String CANCELLED = "Cancelled";

    private static final String bookedErrorMessage = "Already Booked";
    private static final String invalidErrorMessage = "Invalid Operation: from > until";

    // Database
    private static final String BOOKING_DB = "Bookings";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(BOOKING_DB);

    // Fields
    private String id;
    private String providerId;
    private String receiverId;
    private String carId;
    private long from;
    private long until;
    private String status;
    private boolean driver;
    private int rentPerDay;
    private float totalFare;

    // Constructors
    public Booking() {
    }

    public Booking(String id, String providerId, String receiverId, String carId, long from, long until, String status, boolean driver, int rentPerDay, float totalFare) {
        this.id = id;
        this.providerId = providerId;
        this.receiverId = receiverId;
        this.carId = carId;
        this.from = from;
        this.until = until;
        this.status = status;
        this.driver = driver;
        this.rentPerDay = rentPerDay;
        this.totalFare = totalFare;
    }

    // Main Functions
    public void startRent(BookingDao bookingDao) {
        if (status.equals(UPCOMING)) {
            status = ON_PROGRESS;
            from = new Date().getTime();
            updateBooking(bookingDao);
        } else {
            bookingDao.getBoolean(false, "Only " + UPCOMING + " Bookings can be started");
        }
    }

    public void stopRent(BookingDao bookingDao) {
        if (status.equals(ON_PROGRESS)) {
            status = COMPLETED;
            until = new Date().getTime();
            calculateTotalFare();
            updateBooking(bookingDao);
        } else {
            bookingDao.getBoolean(false, "Only " + ON_PROGRESS + " Bookings can be stopped");
        }
    }

    public void cancel(BookingDao bookingDao) {
        if (status.equals(UPCOMING)) {
            status = CANCELLED;
            updateBooking(bookingDao);
        } else {
            bookingDao.getBoolean(false, "Only " + UPCOMING + " Bookings can be cancelled");
        }
    }

    public void calculateTotalFare() {
//        totalFare = until - from;
        long milliseconds = until - from;
        float days = milliseconds/(1000f*60*60*24);
        totalFare = Math.round(days * rentPerDay * 100) / 100f;
//        totalFare = days * rentPerDay;
    }

    public static void isAvailable(String carId, Long from, Long until, BookingDao bookingDao) {
        Booking booking = new Booking();
        booking.setCarId(carId);
        booking.setFrom(from);
        booking.setUntil(until);
        booking.isAvailable(bookingDao);
    }

    public void isAvailable(BookingDao bookingDao) {
        if (from >= until) {
            bookingDao.getBoolean(false, invalidErrorMessage);
        } else {
            Booking.getBookingByCar(carId, new BookingDao() {
                @Override
                public void getBookingList(List<Booking> bookingList) {
                    boolean available = true;
                    if (bookingList != null && bookingList.size() > 0) {
                        for (Booking booking : bookingList) {
                            if ((booking.getStatus().equals(UPCOMING) || booking.getStatus().equals(ON_PROGRESS)) && !booking.isAvailable(from, until)) {
                                available = false;
                                break;
                            }
                        }
                    }
                    bookingDao.getBoolean(available, available ? null : bookedErrorMessage);
                }
            });
        }
    }

    private boolean isAvailable(Long from, Long until) {
        return from > this.until || until < this.from;  /* Non Overlapping Boundaries */
    }

    public static String generateBookingId() {
        return databaseReference.push().getKey();
    }

    public static void getBookingByProvider(String providerId, BookingDao bookingDao) {
        databaseReference.orderByChild("providerId").equalTo(providerId).addListenerForSingleValueEvent(getBookingListListener(bookingDao));
    }

    public static void getBookingByReceiver(String receiverId, BookingDao bookingDao) {
        databaseReference.orderByChild("receiverId").equalTo(receiverId).addListenerForSingleValueEvent(getBookingListListener(bookingDao));
    }

    public static void getBookingByCar(String carId, BookingDao bookingDao) {
        databaseReference.orderByChild("carId").equalTo(carId).addListenerForSingleValueEvent(getBookingListListener(bookingDao));
    }

    public static void getBookingById(String bookingId, BookingDao bookingDao) {
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.getBookingById(bookingDao);
    }

    public void getBookingById(BookingDao bookingDao) {
        databaseReference.child(id).addListenerForSingleValueEvent(getBookingListener(bookingDao));
    }

    public void addBooking(BookingDao bookingDao) {
        Booking booking = this;
        isAvailable(new BookingDao() {
            @Override
            public void getBoolean(Boolean result, String error) {
                if (result) {
                    updateBooking(bookingDao);
                } else {
                    bookingDao.getBoolean(false, error);
                }
            }
        });
    }

    public void updateBooking(BookingDao bookingDao) {
        databaseReference.child(id).setValue(this).addOnCompleteListener(getBooleanListener(bookingDao));
    }

    public void deleteBooking(BookingDao bookingDao) {
        databaseReference.child(id).removeValue().addOnCompleteListener(getBooleanListener(bookingDao));
    }

    // Listeners
    private static OnCompleteListener<Void> getBooleanListener(BookingDao bookingDao) {
        return task -> bookingDao.getBoolean(task.isSuccessful(), task.isSuccessful() ? null : task.getException().toString());
    }

    private static ValueEventListener getBookingListListener(BookingDao bookingDao) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Booking> bookingList = new ArrayList<>();
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                    bookingList.add(snapshot1.getValue(Booking.class));
                bookingDao.getBookingList(bookingList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bookingDao.getBookingList(null);
            }
        };
    }

    private static ValueEventListener getBookingListener(BookingDao bookingDao) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingDao.getBooking(snapshot.getValue(Booking.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bookingDao.getBooking(null);
            }
        };
    }

    // Getter Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getUntil() {
        return until;
    }

    public void setUntil(long until) {
        this.until = until;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDriver() {
        return driver;
    }

    public void setDriver(boolean driver) {
        this.driver = driver;
    }

    public int getRentPerDay() {
        return rentPerDay;
    }

    public void setRentPerDay(int rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    public float getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(float totalFare) {
        this.totalFare = totalFare;
    }
}