package com.example.rapidrentals.Helper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.R;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class CarRentAdapter extends ArrayAdapter<CarHelper> {

    Context context;
    List<CarHelper> carHelpers;
    int customLayoutId;

    public CarRentAdapter(@NonNull Context context, int resource, @NonNull List<CarHelper> objects) {
        super(context, resource, objects);
        this.context = context;
        this.carHelpers = objects;
        this.customLayoutId = resource;
    }

    @Override
    public int getCount() {
        return carHelpers.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(customLayoutId, null);
        }

        CarHelper carHelper = carHelpers.get(position);

        ImageView carImage = view.findViewById(R.id.car_image);
        TextView carBrandModel = view.findViewById(R.id.car_brand_model);
        TextView carYearType = view.findViewById(R.id.car_year_type);
        TextView rent = view.findViewById(R.id.car_rent);
        TextView fuel = view.findViewById(R.id.car_fuel);

        StorageReference reference = Car.getStorageReference().child(carHelper.getId()).child(Car.getFileName());

        GlideApp.with(context)
                .load(reference)
                .placeholder(R.drawable.preview)
                .error(R.drawable.preview)
                .centerCrop()
                .into(carImage);
        carBrandModel.setText(carHelper.getBrand_model());
        carYearType.setText(carHelper.getYear_type());
        fuel.setText(String.valueOf(carHelper.getFuel().charAt(0)));
        rent.setText(String.format("Rs.%d", carHelper.getRentPerDay()));

        return view;
    }
}