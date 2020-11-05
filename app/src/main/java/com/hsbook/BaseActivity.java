package com.hsbook;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class BaseActivity extends AppCompatActivity {

    private boolean backPressedToExitOnce = false;

    @Override
    public void onBackPressed() {

        if (backPressedToExitOnce) {
            super.onBackPressed();
        } else {
            backPressedToExitOnce = true;
            Toast.makeText(this, "Double press back button to exit.", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Your Code
                    backPressedToExitOnce = false;
                }
            }, 1000);
        }
    }
}
