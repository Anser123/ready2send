package com.example.utilityappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getWindow().setStatusBarColor(
                getColor(R.color.app_background)
        );

        getWindow().setNavigationBarColor(
                getColor(R.color.app_background)
        );

        ImageButton backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        findViewById(R.id.aboutButton)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    SettingsActivity.this,
                                    AboutActivity.class
                            );

                    startActivity(intent);
                });

        findViewById(R.id.privacyButton)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    SettingsActivity.this,
                                    PrivacyActivity.class
                            );

                    startActivity(intent);
                });

        findViewById(R.id.termsButton)
                .setOnClickListener(v -> {

                    Intent intent =
                            new Intent(
                                    SettingsActivity.this,
                                    TermsActivity.class
                            );

                    startActivity(intent);
                });
    }
}