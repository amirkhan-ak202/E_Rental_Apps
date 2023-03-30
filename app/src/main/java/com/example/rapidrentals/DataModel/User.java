package com.example.rapidrentals.DataModel;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class User {

    private static final String USER_DB = "Users";
    private static final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(USER_DB);

    private String userId;
    private String fullName;
    private String emailId;
    private String phoneNumber;
    private String password;
    private String birthDate;
    private String createdAt;

    public User() {
    }

    public User(String userId, String fullName, String emailId, String phoneNumber, String password, String birthDate, String createdAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.emailId = emailId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.birthDate = birthDate;
        this.createdAt = createdAt;
    }

    public static void getUserById(String userId, UserDao userDao){
        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userBean = snapshot.getValue(User.class);
                userDao.getUser(userBean);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                userDao.getUser(null);
            }
        });
    }

    public void addUser(UserDao userDao) {
        Log.d("MMESAGE", "Here -1");
        databaseReference.child(userId).setValue(this).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                userDao.getBoolean(task.isSuccessful());
            }
        });
        databaseReference.child(userId).setValue(this).addOnCompleteListener(task -> userDao.getBoolean(task.isSuccessful()));
        Log.d("MMESAGE", "Here -2");
    }

    public void deleteUser(UserDao userDao) {
        databaseReference.child(userId).removeValue().addOnCompleteListener(task -> userDao.getBoolean(task.isSuccessful()));
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
