package com.hsbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                //handler.postDelayed(this, 1000);
                Intent mainAct = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainAct);
                finish();
            }
        };
        handler.postDelayed(r, 3000);

    }

}
