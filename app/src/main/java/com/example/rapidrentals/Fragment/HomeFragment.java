package com.example.rapidrentals.Fragment;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapidrentals.Activity.SearchActivity;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.CarAdapter;
import com.example.rapidrentals.Helper.CarHelper;
import com.example.rapidrentals.Helper.CategoryAdapter;
import com.example.rapidrentals.Helper.CategoryHelper;
import com.example.rapidrentals.R;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private SearchView searchView;

    //AvailableCar Recycler
    private RecyclerView availableCarRecycler;
    private RecyclerView.Adapter availableCarAdapter;
    private ArrayList<CarHelper> availableCarHelpers;

    //Category Recycler
    private RecyclerView categoryRecycler;
    private RecyclerView.Adapter categoryAdapter;
    private ArrayList<CategoryHelper> categoryHelpers;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        initComponents(root);

        return root;
    }

    private void initComponents(View root) {

        GradientDrawable[] gradientDrawables = new GradientDrawable[4];
        gradientDrawables[0] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
        gradientDrawables[1] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradientDrawables[2] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
        gradientDrawables[3] = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});

        searchView = root.findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putString(SearchActivity.SEARCH_QUERY, query);
                intent.putExtras(extras);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Available Cars Recycler
        availableCarRecycler = root.findViewById(R.id.available_cars_recycler);
        availableCarRecycler.setHasFixedSize(true);
        availableCarRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        availableCarHelpers = new ArrayList<>();
        availableCarAdapter = new CarAdapter(getActivity().getApplicationContext(),availableCarHelpers);
        availableCarRecycler.setAdapter(availableCarAdapter);

        retrieveAvailableCars();

        // Category Recycler
        categoryRecycler = root.findViewById(R.id.categories_recycler);
        categoryRecycler.setHasFixedSize(true);
        categoryRecycler.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));

        categoryHelpers = new ArrayList<>();
        categoryHelpers.add(new CategoryHelper(R.drawable.car_sports, "1", "Sports", gradientDrawables[0]));
        categoryHelpers.add(new CategoryHelper(R.drawable.car_sedan, "2", "Sedan", gradientDrawables[1]));
        categoryHelpers.add(new CategoryHelper(R.drawable.car_luxury, "3", "Luxury", gradientDrawables[2]));
        categoryHelpers.add(new CategoryHelper(R.drawable.car_coupe1, "4", "Coupe", gradientDrawables[3]));

        categoryAdapter = new CategoryAdapter(getActivity().getApplicationContext(),categoryHelpers);
        categoryRecycler.setAdapter(categoryAdapter);
    }

    private void retrieveAvailableCars() {
        Car.getAvailableCars(new CarDao() {
            @Override
            public void getCarList(List<Car> carList) {
                if (carList != null && carList.size() > 0) {
                    availableCarHelpers.clear();
                    for (Car car : carList) {
                        availableCarHelpers.add(new CarHelper(0, car.getId(), car.getBrand() + ", " + car.getModel(), car.getYear() + " • " + car.getType(), null, car.getFuel(), car.getRentPerDay()));
                    }
                    availableCarAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "No Available Cars", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}