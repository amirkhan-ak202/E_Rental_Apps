package com.example.rapidrentals.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.rapidrentals.Activity.CarAddActivity;
import com.example.rapidrentals.Activity.MainActivity;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.CarHelper;
import com.example.rapidrentals.Helper.CarRentAdapter;
import com.example.rapidrentals.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class AddFragment extends Fragment {

    ImageView addCarBtn;
    GridView carGridView;
    LinearLayout emptyLayout;

    ArrayList<Car> carArrayList;
    ArrayList<CarHelper> carHelpers;
    CarRentAdapter carRentAdapter;

    FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add, container, false);

        initComponents(root);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveCarInformation();
    }

    private void initComponents(View root) {
        carGridView = root.findViewById(R.id.car_grid_view);
        emptyLayout = root.findViewById(R.id.no_record_found_layout);
        addCarBtn = root.findViewById(R.id.addCarButton);

        carGridView.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.VISIBLE);

        carArrayList = new ArrayList<>();
        carHelpers = new ArrayList<>();
        carRentAdapter = new CarRentAdapter(getActivity().getApplicationContext(), R.layout.car_design_1_card, carHelpers);
        carGridView.setAdapter(carRentAdapter);
        registerForContextMenu(carGridView);
        carGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getActivity().openContextMenu(view);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        addCarBtn.setOnClickListener(view -> onClickAddCar(view));

        retrieveCarInformation();

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.context_car_add_menu, menu);
    }

    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo contextMenuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        CarHelper carHelper = (CarHelper) carHelpers.get((int) contextMenuInfo.id);
        switch (item.getItemId()) {
            case R.id.context_update_car:
                Intent addCarIntent = new Intent(getActivity().getApplicationContext(), CarAddActivity.class);
                Bundle extras = new Bundle();
                extras.putString(CarAddActivity.CAR_OPERATION, CarAddActivity.CAR_UPDATE);
                extras.putString(CarAddActivity.CAR_ID, carHelper.getId());
                addCarIntent.putExtras(extras);
                startActivity(addCarIntent);
                return true;
            case R.id.context_delete_car:
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Car")
                        .setMessage("Are you sure you want to delete this Service ?")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Car car = new Car();
                                car.setId(carHelper.getId());
                                car.deleteCar(new CarDao() {
                                    @Override
                                    public void getBoolean(Boolean result) {
                                        Toast.makeText(getActivity().getApplicationContext(), result ? "Car Deleted" : "Car couldn't be Deleted", Toast.LENGTH_SHORT).show();
                                        if (result) {
                                            carHelpers.remove((int) contextMenuInfo.id);
                                            carRentAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onClickAddCar(View view) {
        Intent intent = new Intent(getActivity().getApplicationContext(), CarAddActivity.class);
        startActivity(intent);
    }

    private void retrieveCarInformation() {
        Car.getCarsByOwner(currentUser.getUid(), new CarDao() {
            @Override
            public void getCarList(List<Car> carList) {
                if (carList != null && carList.size() > 0) {
                    emptyLayout.setVisibility(View.GONE);
                    carGridView.setVisibility(View.VISIBLE);

                    carHelpers.clear();
                    for (Car car : carList) {
                        carHelpers.add(new CarHelper(0, car.getId(), car.getBrand() + ", " + car.getModel(), car.getYear() + " • " + car.getType(), null, car.getFuel(), car.getRentPerDay()));
                    }

                } else {
                    emptyLayout.setVisibility(View.VISIBLE);
                    carGridView.setVisibility(View.GONE);
                }
            }
        });
    }

}