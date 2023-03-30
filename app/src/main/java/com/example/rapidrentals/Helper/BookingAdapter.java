package com.example.rapidrentals.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rapidrentals.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingAdapter extends ArrayAdapter<BookingHelper> {

    private Context context;
    private int resource;
    private List<BookingHelper> bookingHelpers;

    public BookingAdapter(@NonNull Context context, int resource, @NonNull List<BookingHelper> bookingHelpers) {
        super(context, resource, bookingHelpers);
        this.context = context;
        this.resource = resource;
        this.bookingHelpers = bookingHelpers;
    }

    @Override
    public int getCount() {
        return bookingHelpers.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, null);
        }

        BookingHelper bookingHelper = bookingHelpers.get(position);

        TextView carBrandModel = view.findViewById(R.id.car_brand_model);
        TextView carYearType = view.findViewById(R.id.car_year_type);
        TextView rent = view.findViewById(R.id.car_rent);
        TextView from = view.findViewById(R.id.booking_from);
        TextView until = view.findViewById(R.id.booking_until);
        TextView status = view.findViewById(R.id.booking_status);
        TextView fare = view.findViewById(R.id.booking_fare);

        carBrandModel.setText(bookingHelper.getBrand_model());
        carYearType.setText(bookingHelper.getYear_type());
        rent.setText(String.valueOf(bookingHelper.getRentPerDay()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy hh:mm a");
        from.setText(dateFormat.format(new Date(bookingHelper.getFrom())));
        until.setText(dateFormat.format(new Date(bookingHelper.getUntil())));
        status.setText(bookingHelper.getStatus());
        fare.setText(String.format("Rs.%.2f", bookingHelper.getFare()));
        return view;
    }
}
