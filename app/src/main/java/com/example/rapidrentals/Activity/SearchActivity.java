package com.example.rapidrentals.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.CarBuyAdapter;
import com.example.rapidrentals.Helper.CarHelper;
import com.example.rapidrentals.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    public static final String SEARCH_QUERY = "SEARCH_QUERY";

    private String searchQuery;

    private SearchView searchView;
    private ListView searchResultListView;

    private List<CarHelper> carHelperList;
    private CarBuyAdapter carBuyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initComponents();

    }

    private void initComponents() {
        searchView = findViewById(R.id.search_view);
        searchResultListView = findViewById(R.id.search_result_list_view);

        carHelperList = new ArrayList<>();
        carBuyAdapter = new CarBuyAdapter(getApplicationContext(), R.layout.car_design_card, carHelperList);
        searchResultListView.setAdapter(carBuyAdapter);

        Bundle extras =getIntent().getExtras();
        searchQuery = extras.getString(SEARCH_QUERY);

        retrieveAvailableCars();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                carBuyAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                carBuyAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    private void retrieveAvailableCars() {
        Car.getAvailableCars(new CarDao() {
            @Override
            public void getCarList(List<Car> carList) {
                if (carList != null && carList.size() > 0) {
                    carHelperList.clear();
                    for (Car car : carList) {
                        carHelperList.add(new CarHelper(0, car.getId(), car.getBrand() + ", " + car.getModel(), car.getYear() + " • " + car.getType(), null, car.getFuel(), car.getRentPerDay()));
                    }
                    carBuyAdapter.setOriginalList(carHelperList);
                    carBuyAdapter.notifyDataSetChanged();
                    if(searchQuery != null && !searchQuery.isEmpty()) {
                        searchView.setQuery(searchQuery, true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Available Cars", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}