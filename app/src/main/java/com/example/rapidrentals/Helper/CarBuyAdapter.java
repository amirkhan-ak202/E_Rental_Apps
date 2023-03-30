package com.example.rapidrentals.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.rapidrentals.Activity.CarDetailActivity;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CarBuyAdapter extends ArrayAdapter<CarHelper> {

    Context context;
    List<CarHelper> carHelpers;
    List<CarHelper> carHelpersAll;
    int customLayoutId;

    public CarBuyAdapter(@NonNull Context context, int resource, @NonNull List<CarHelper> objects) {
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
        TextView bookBtn = view.findViewById(R.id.car_book_btn);

        carBrandModel.setText(carHelper.getBrand_model());
        carYearType.setText(carHelper.getYear_type());
        fuel.setText(String.valueOf(carHelper.getFuel().charAt(0)));
        rent.setText(String.format("Rs.%d", carHelper.getRentPerDay()));
        bookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CarDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putString(CarDetailActivity.CAR_ID, carHelper.getId());
                intent.putExtras(extras);
                context.startActivity(intent);
            }
        });

        StorageReference reference = Car.getStorageReference().child(carHelper.getId()).child(Car.getFileName());
        GlideApp.with(context)
                .load(reference)
                .placeholder(R.drawable.preview)
                .error(R.drawable.preview)
                .centerCrop()
                .into(carImage);

        return view;
    }

    // Filter
    @NonNull
    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<CarHelper> filteredCars = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredCars.addAll(carHelpersAll);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (CarHelper carHelper : carHelpersAll)
                    if (carHelper.getBrand_model().toLowerCase().contains(filterPattern) ||
                            carHelper.getYear_type().toLowerCase().contains(filterPattern))
                        filteredCars.add(carHelper);
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredCars;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            carHelpers.clear();
            carHelpers.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public void setOriginalList(List<CarHelper> carHelpersAll) {
        this.carHelpersAll = new ArrayList<>(carHelpersAll);
    }
}