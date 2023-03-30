package com.example.rapidrentals.DataModel;

public interface UserDao {
    default void getUser(User user){};
    default void getBoolean(Boolean result){};
}
