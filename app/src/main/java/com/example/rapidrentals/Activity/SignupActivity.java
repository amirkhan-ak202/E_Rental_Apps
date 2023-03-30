package com.example.rapidrentals.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rapidrentals.DataModel.User;
import com.example.rapidrentals.DataModel.UserDao;
import com.example.rapidrentals.R;
import com.example.rapidrentals.Utility.AppUtility;
import com.example.rapidrentals.Utility.LoadingDialog;
import com.example.rapidrentals.Utility.Validation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

public class SignupActivity extends AppCompatActivity {

    private LoadingDialog loadingDialog;
    private TextView headerText;
    private TextInputLayout fullNameLayout, emailLayout, phoneLayout, passwordLayout, cpasswordLayout;
    private Button registerBtn, goToLoginBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        loadingDialog = new LoadingDialog(SignupActivity.this);
        headerText = findViewById(R.id.header);
        fullNameLayout = findViewById(R.id.fullname_layout);
        emailLayout = findViewById(R.id.email_layout);
        phoneLayout = findViewById(R.id.phone_layout);

        passwordLayout = findViewById(R.id.password_layout);
        cpasswordLayout = findViewById(R.id.cpassword_layout);
        registerBtn = findViewById(R.id.register_button);
        goToLoginBtn = findViewById(R.id.goto_login);

//        Firebase
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                Log.d("MMESAGE", "Here 0");
                String fullName = fullNameLayout.getEditText().getText().toString().trim();
                String email = emailLayout.getEditText().getText().toString().trim();
                String phone = phoneLayout.getEditText().getText().toString().trim();
                String password = passwordLayout.getEditText().getText().toString().trim();
                String cpassword = cpasswordLayout.getEditText().getText().toString().trim();
                String createdAt = new Timestamp(System.currentTimeMillis()).toString();

                if (!Validation.validateEmpty(fullNameLayout) | !Validation.validateEmail(emailLayout)
                        | !Validation.validateEmpty(phoneLayout)
                        | !Validation.validatePassword(passwordLayout)
                        | !Validation.comparePassword(passwordLayout, cpasswordLayout)) {
                    Toast.makeText(SignupActivity.this, "Validation Failed", Toast.LENGTH_LONG).show();
                    loadingDialog.stopLoadingDialog();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("MMESAGE", "Here 1");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            User user = new User(currentUser.getUid(), fullName, email, phone, password, "", createdAt);
                            user.addUser(new UserDao() {
                                @Override
                                public void getBoolean(Boolean result) {
                                    Log.d("MMESAGE", "Here 2");
                                    if (result) {
                                        Log.d("MMESAGE", "Here 3");
//                                        AppUtility.makeToast(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT);
                                        Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        loadingDialog.stopLoadingDialog();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("MMESAGE", "Here 4");
                                        loadingDialog.stopLoadingDialog();
//                                        AppUtility.makeToast(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT);
                                        Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Log.d("MMESAGE", "Here 5");
                            loadingDialog.stopLoadingDialog();
                            AppUtility.makeToast(getApplicationContext(), "User couldn't be added. Try again", Toast.LENGTH_SHORT);
                        }
                        Log.d("MMESAGE", "Here 6");
                    }
                });
            }
        });

        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                Pair[] pairs = new Pair[5];
                pairs[0] = new Pair<View, String>(headerText, "header_text");
                pairs[1] = new Pair<View, String>(emailLayout, "email_trans");
                pairs[2] = new Pair<View, String>(passwordLayout, "password_trans");
                pairs[3] = new Pair<View, String>(registerBtn, "button_trans");
                pairs[4] = new Pair<View, String>(goToLoginBtn, "goto_trans");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);

                startActivity(intent, options.toBundle());
            }
        });
    }
}