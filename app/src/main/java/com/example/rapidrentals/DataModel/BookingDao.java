package com.example.rapidrentals.DataModel;

import java.util.List;

public interface BookingDao {
    default void getBooking(Booking booking){};
    default void getBookingList(List<Booking> bookingList){};
    default void getBoolean(Boolean result, String error){};
}
