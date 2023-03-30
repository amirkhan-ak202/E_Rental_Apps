package com.example.rapidrentals.Helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapidrentals.Activity.CarDetailActivity;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.R;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

    Context context;
    ArrayList<CarHelper> carHelpers;

    public CarAdapter(Context context, ArrayList<CarHelper> carHelpers) {
        this.context = context;
        this.carHelpers = carHelpers;
    }

    @NonNull
    @Override
    public CarAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_design_card, parent, false);
        return new CarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.CarViewHolder holder, int position) {
        CarHelper carHelper = carHelpers.get(position);

        holder.image.setImageResource(carHelper.getImage());
        StorageReference reference = Car.getStorageReference().child(carHelper.getId()).child(Car.getFileName());

        GlideApp.with(context)
                .load(reference)
                .placeholder(R.drawable.preview)
                .error(R.drawable.preview)
                .centerCrop()
                .into(holder.image);
        holder.brand_model.setText(carHelper.getBrand_model());
        holder.year_type.setText(carHelper.getYear_type());
        holder.location.setText(carHelper.getLocation());
        holder.price.setText(String.valueOf(carHelper.getRentPerDay()));
        holder.fuel.setText(""+carHelper.getFuel().charAt(0));
        holder.bookBtn.setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public int getItemCount() {
        return carHelpers.size();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {

        TextView brand_model;
        TextView year_type;
        TextView location;
        TextView price;
        TextView fuel;
        ImageView image;
        TextView bookBtn;

        public CarViewHolder(@NonNull View itemView) {
            super(itemView);
            brand_model = itemView.findViewById(R.id.car_brand_model);
            year_type = itemView.findViewById(R.id.car_year_type);
            location = itemView.findViewById(R.id.car_location);
            price = itemView.findViewById(R.id.car_rent);
            fuel = itemView.findViewById(R.id.car_fuel);
            image = itemView.findViewById(R.id.car_image);
            bookBtn = itemView.findViewById(R.id.car_book_btn);
        }
    }

}
