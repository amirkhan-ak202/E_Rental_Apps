package com.example.rapidrentals.DataModel;

import java.util.List;

public interface CarDao {
    default void getCar(Car car){};
    default void getCarList(List<Car> carList){};
    default void getBoolean(Boolean result){};
}
