package com.example.utilityappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        SharedPreferences preferences =
                getSharedPreferences("Ready2SendPrefs", MODE_PRIVATE);

        LinearLayout whatsappOption =
                findViewById(R.id.whatsappOption);

        LinearLayout shareSheetOption =
                findViewById(R.id.shareSheetOption);

        View whatsappCheck =
                findViewById(R.id.whatsappCheck);

        View shareSheetCheck =
                findViewById(R.id.shareSheetCheck);

        boolean openWhatsApp =
                preferences.getBoolean("open_whatsapp_after_processing", true);

        updateSelection(
                openWhatsApp,
                whatsappCheck,
                shareSheetCheck
        );

        getWindow().setStatusBarColor(
                getColor(R.color.app_background)
        );

        getWindow().setNavigationBarColor(
                getColor(R.color.app_background)
        );

        ImageButton backButton =
                findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> finish());

        whatsappOption.setOnClickListener(v -> {

            preferences.edit()
                    .putBoolean("open_whatsapp_after_processing", true)
                    .apply();

            updateSelection(
                    true,
                    whatsappCheck,
                    shareSheetCheck
            );
        });

        shareSheetOption.setOnClickListener(v -> {

            preferences.edit()
                    .putBoolean("open_whatsapp_after_processing", false)
                    .apply();

            updateSelection(
                    false,
                    whatsappCheck,
                    shareSheetCheck
            );
        });

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

    private void updateSelection(
            boolean openWhatsApp,
            View whatsappCheck,
            View shareSheetCheck) {

        if (openWhatsApp) {

            whatsappCheck.setBackgroundResource(
                    R.drawable.radio_selected
            );

            shareSheetCheck.setBackgroundResource(
                    R.drawable.radio_unselected
            );

        } else {

            whatsappCheck.setBackgroundResource(
                    R.drawable.radio_unselected
            );

            shareSheetCheck.setBackgroundResource(
                    R.drawable.radio_selected
            );
        }
    }
}