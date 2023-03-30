package com.example.rapidrentals.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.rapidrentals.Activity.LoginActivity;
import com.example.rapidrentals.DataModel.User;
import com.example.rapidrentals.DataModel.UserDao;
import com.example.rapidrentals.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class UserFragment extends Fragment {

    private TextView fullName, email, phone;
    private Button logoutBtn;

    private FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        initComponents(root);

        return root;
    }

    private void initComponents(View root) {
        fullName = root.findViewById(R.id.user_full_name);
        email = root.findViewById(R.id.user_email);
        phone = root.findViewById(R.id.user_phone);
        logoutBtn = root.findViewById(R.id.user_logout_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLogoutClick(view);
            }
        });

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Please Login", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
            getActivity().finish();
        }

        retrieveUserInfo();
    }

    public void onLogoutClick(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getActivity().getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
        getActivity().startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
    }

    private void retrieveUserInfo() {
        User.getUserById(currentUser.getUid(), new UserDao() {
            @Override
            public void getUser(User user) {
                fullName.setText(user.getFullName());
                email.setText(user.getEmailId());
                phone.setText(user.getPhoneNumber());
            }
        });
    }
}