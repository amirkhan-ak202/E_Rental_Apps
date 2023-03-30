package com.example.rapidrentals.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rapidrentals.Activity.CarBookActivity;
import com.example.rapidrentals.Activity.CarDetailActivity;
import com.example.rapidrentals.DataModel.Booking;
import com.example.rapidrentals.DataModel.BookingDao;
import com.example.rapidrentals.DataModel.Car;
import com.example.rapidrentals.DataModel.CarDao;
import com.example.rapidrentals.Helper.BookingAdapter;
import com.example.rapidrentals.Helper.BookingHelper;
import com.example.rapidrentals.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout noRecordLayout;
    private ListView bookingListView;
    private ArrayList<BookingHelper> bookingHelpers;
    private ArrayList<Booking> bookingArrayList;
    private BookingAdapter bookingAdapter;

    private FirebaseUser currentUser;

    public BookingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookingListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookingListFragment newInstance(String param1, String param2) {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);

        noRecordLayout = root.findViewById(R.id.no_record_found_layout);
        bookingListView = root.findViewById(R.id.booking_list);
        bookingHelpers = new ArrayList<>();
        bookingArrayList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(getActivity(), R.layout.booking_card, bookingHelpers);
        bookingListView.setAdapter(bookingAdapter);
        registerForContextMenu(bookingListView);
        bookingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getActivity().openContextMenu(view);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        return root;
    }

    private void retrieveBookingInfo() {
        Booking.getBookingByReceiver(currentUser.getUid(), new BookingDao() {
            @Override
            public void getBookingList(List<Booking> bookingList) {
                if (bookingList != null && bookingList.size() > 0) {
                    bookingListView.setVisibility(View.VISIBLE);
                    noRecordLayout.setVisibility(View.GONE);
                    bookingHelpers.clear();
                    bookingArrayList.clear();
                    bookingArrayList.addAll(bookingList);
                    for (int i = 0; i < bookingList.size(); i++) {
                        Booking booking = bookingList.get(i);
                        int finalI = i;
                        Car.getCarById(booking.getCarId(), new CarDao() {
                            @Override
                            public void getCar(Car car) {
                                if (car != null) {
                                    bookingHelpers.add(new BookingHelper(booking.getId(), car.getBrandModel(), car.getYearType(), car.getRentPerDay(), booking.getFrom(), booking.getUntil(), booking.getStatus(), booking.getTotalFare()));
                                    if (finalI == bookingList.size() - 1)
                                        bookingAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                } else {
                    bookingListView.setVisibility(View.GONE);
                    noRecordLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        BookingHelper bookingHelper = (BookingHelper) bookingHelpers.get((int) info.id);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.context_booking_menu, menu);

        HashMap<String, Integer> statusCode = new HashMap<>();
        statusCode.put(Booking.UPCOMING, 0);
        statusCode.put(Booking.ON_PROGRESS, 1);
        statusCode.put(Booking.COMPLETED, 2);
        statusCode.put(Booking.CANCELLED, 3);


        boolean[][] operationMatrix = new boolean[][]
                {/* Upcoming | On progress | Completed | Cancelled */
/* Start */                        {true, false, false, false},
/* Stop */                        {false, true, false, false},
/* Edit */                        {true, false, false, false},
/* View */                        {true, true, true, true},
/* Cancel */                        {true, false, false, false}

                };

        for (int i = 0; i < 5; i++) {
            menu.getItem(i).setEnabled(operationMatrix[i][statusCode.get(bookingHelper.getStatus())]);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        BookingHelper bookingHelper = (BookingHelper) bookingHelpers.get((int) menuInfo.id);
        Booking booking = findBookingById(bookingHelper.getId());
        if (booking == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Booking not found", Toast.LENGTH_SHORT).show();
            return true;
        }
        switch (item.getItemId()) {
            case R.id.booking_start:
                booking.startRent(new BookingDao() {
                    @Override
                    public void getBoolean(Boolean result, String error) {
                        if (result) {
                            Toast.makeText(getActivity().getApplicationContext(), "Rent Started", Toast.LENGTH_SHORT).show();
                            retrieveBookingInfo();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.booking_stop:
                booking.stopRent(new BookingDao() {
                    @Override
                    public void getBoolean(Boolean result, String error) {
                        if (result) {
                            Toast.makeText(getActivity().getApplicationContext(), "Rent Stopped", Toast.LENGTH_SHORT).show();
                            retrieveBookingInfo();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            case R.id.booking_edit:
                Intent editIntent = new Intent(getActivity().getApplicationContext(), CarBookActivity.class);
                Bundle editExtras = new Bundle();
                editExtras.putString(CarBookActivity.BOOK_OPERATION, CarBookActivity.BOOK_MODIFY);
                editExtras.putString(CarBookActivity.CAR_ID, booking.getCarId());
                editExtras.putString(CarBookActivity.BOOK_ID, booking.getId());
                editIntent.putExtras(editExtras);
                startActivity(editIntent);
                break;
            case R.id.booking_view_car:
                Intent intent = new Intent(getActivity().getApplicationContext(), CarDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString(CarDetailActivity.CAR_ID, booking.getCarId());
                intent.putExtras(extras);
                startActivity(intent);
                break;
            case R.id.booking_cancel:
                booking.cancel(new BookingDao() {
                    @Override
                    public void getBoolean(Boolean result, String error) {
                        if (result) {
                            Toast.makeText(getActivity().getApplicationContext(), "Booking Cancelled", Toast.LENGTH_SHORT).show();
                            retrieveBookingInfo();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            default:
                return super.onContextItemSelected(item);
        }
        return true;
    }

    private Booking findBookingById(String bookingId) {
        for (Booking booking : bookingArrayList)
            if (booking.getId().equals(bookingId))
                return booking;
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        retrieveBookingInfo();
    }
}