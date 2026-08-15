package com.example.utilityappandroid;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class PrivacyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_privacy);

        getWindow().setStatusBarColor(
                getColor(R.color.app_background)
        );

        getWindow().setNavigationBarColor(
                getColor(R.color.app_background)
        );

        ImageButton backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());
    }
}