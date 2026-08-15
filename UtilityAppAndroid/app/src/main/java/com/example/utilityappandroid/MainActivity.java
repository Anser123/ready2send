package com.example.utilityappandroid;

import android.os.Bundle;
import android.content.Intent;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ImageButton settingsButton =
                findViewById(R.id.settingsButton);

        settingsButton.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            SettingsActivity.class
                    );

            startActivity(intent);
        });

        ImageButton premiumButton =
                findViewById(R.id.premiumButton);

        premiumButton.setOnClickListener(v -> {
            Intent intent =
                    new Intent(
                            MainActivity.this,
                            PremiumActivity.class
                    );

            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            28 + systemBars.left,
                            28 + systemBars.top,
                            28 + systemBars.right,
                            28 + systemBars.bottom
                    );

                    return insets;
                }
        );
    }
}