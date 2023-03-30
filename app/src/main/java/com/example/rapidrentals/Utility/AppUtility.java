package com.example.rapidrentals.Utility;

import android.content.Context;
import android.widget.Toast;

public class AppUtility {
    public static void makeToast(Context context, String message, int duration ) {
        Toast.makeText(context, message, duration).show();
    }
}
