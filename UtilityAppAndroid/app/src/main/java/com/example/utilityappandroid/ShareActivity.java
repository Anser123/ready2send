package com.example.utilityappandroid;

import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;

import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ShareActivity extends AppCompatActivity {
    private Dialog progressDialog;

    private static final String AD_UNIT_ID =
            "ca-app-pub-3940256099942544/1033173712";

    private InterstitialAd interstitialAd;

    private boolean processingFinished = false;
    private boolean adFinished = false;
    private File processedVideoFile;
    private boolean adDelayFinished = false;
    private boolean adWaitExpired = false;
    private boolean shareTriggered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Thread(() -> {

            MobileAds.initialize(
                    this,
                    new InitializationConfig.Builder(
                            "ca-app-pub-3940256099942544~3347511713"
                    ).build(),
                    initializationStatus -> {

                        Log.d(
                                "Ready2SendAds",
                                "Mobile Ads SDK initialized"
                        );

                        loadInterstitialAd();
                    }
            );

        }).start();

        Window window = getWindow();
        window.setStatusBarColor(
                android.graphics.Color.TRANSPARENT
        );
        handleSharedContent(getIntent());

    }

    private void loadInterstitialAd() {

        InterstitialAd.load(
                new AdRequest.Builder(AD_UNIT_ID).build(),
                new AdLoadCallback<InterstitialAd>() {

                    @Override
                    public void onAdLoaded(
                            @NonNull InterstitialAd ad) {

                        interstitialAd = ad;

                        if (adDelayFinished) {
                            showInterstitialAdIfReady();
                        }

                        interstitialAd.setAdEventCallback(
                                new InterstitialAdEventCallback() {

                                    @Override
                                    public void onAdDismissedFullScreenContent() {

                                        interstitialAd = null;
                                        adFinished = true;

                                        Log.d(
                                                "Ready2SendAds",
                                                "Interstitial ad dismissed"
                                        );

                                        checkIfReadyToShare();
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(
                                            @NonNull FullScreenContentError error) {

                                        interstitialAd = null;
                                        adFinished = true;

                                        Log.e(
                                                "Ready2SendAds",
                                                "Interstitial ad failed to show: "
                                                        + error.getMessage()
                                        );

                                        checkIfReadyToShare();
                                    }
                                }
                        );

                        Log.d(
                                "Ready2SendAds",
                                "Interstitial ad loaded"
                        );
                    }

                    @Override
                    public void onAdFailedToLoad(
                            @NonNull LoadAdError adError) {

                        interstitialAd = null;
                        adFinished = true;

                        Log.e(
                                "Ready2SendAds",
                                "Interstitial ad failed to load: "
                                        + adError.getMessage()
                        );

                        checkIfReadyToShare();
                    }
                }
        );
    }
    private void handleSharedContent(Intent intent) {

        if (!Intent.ACTION_SEND.equals(intent.getAction())) {
            finish();
            return;
        }
        String sharedText =
                intent.getStringExtra(Intent.EXTRA_TEXT);

        if (sharedText == null || sharedText.trim().isEmpty()) {
            Log.d(
                    "Ready2SendShare",
                    "No shared URL received"
            );
            finish();
            return;
        }

        String videoUrl = sharedText.trim();
        Log.d(
                "Ready2SendShare",
                "Extracted URL: " + videoUrl
        );

        showProcessingDialog();
        sendURLToFlow(videoUrl);

        new android.os.Handler(
                android.os.Looper.getMainLooper()
        ).postDelayed(() -> {

            adDelayFinished = true;
            showInterstitialAdIfReady();

            new android.os.Handler(
                    android.os.Looper.getMainLooper()
            ).postDelayed(() -> {

                if (!adFinished && interstitialAd == null) {

                    adWaitExpired = true;
                    adFinished = true;

                    Log.d(
                            "Ready2SendAds",
                            "Ad wait expired - skipping ad"
                    );

                    checkIfReadyToShare();
                }

            }, 5000);

        }, 2000);
    }

    private void sendURLToFlow(String url) {
        new Thread(() -> {
            try {
                URL apiUrl =
                        new URL("http://35.209.212.44:8000/process");
                HttpURLConnection connection =
                        (HttpURLConnection) apiUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty(
                        "Content-Type",
                        "application/json"
                );

                connection.setDoOutput(true);
                String json =
                        "{\"url\":\"" + url + "\"}";

                OutputStream outputStream =
                        connection.getOutputStream();
                outputStream.write(
                        json.getBytes(StandardCharsets.UTF_8)
                );
                outputStream.flush();
                outputStream.close();

                int responseCode =
                        connection.getResponseCode();

                String contentType =
                        connection.getHeaderField("Content-Type");

                if (responseCode ==
                        HttpURLConnection.HTTP_OK) {

                    InputStream inputStream =
                            connection.getInputStream();

                    if (contentType != null &&
                            contentType.contains("application/json")) {

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                inputStream,
                                                StandardCharsets.UTF_8
                                        )
                                );

                        StringBuilder response =
                                new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();

                        String responseBody =
                                response.toString();

                        Log.e(
                                "API",
                                "Server response: "
                                        + responseBody
                        );

                        String errorMessage =
                                "The server could not process this video.";

                        if (responseBody.contains("\"error\"")) {

                            int errorStart =
                                    responseBody.indexOf(
                                            "\"error\":\""
                                    );

                            if (errorStart != -1) {

                                errorStart +=
                                        "\"error\":\"".length();

                                int errorEnd =
                                        responseBody.indexOf(
                                                "\"",
                                                errorStart
                                        );

                                if (errorEnd != -1) {

                                    errorMessage =
                                            responseBody.substring(
                                                    errorStart,
                                                    errorEnd
                                            );
                                }
                            }
                        }

                        inputStream.close();

                        showError(errorMessage);
                        connection.disconnect();
                        return;
                    }

                    File outputFile =
                            new File(
                                    getExternalFilesDir(
                                            Environment.DIRECTORY_MOVIES
                                    ),
                                    "video.mp4"
                            );

                    FileOutputStream
                            outputStreamFile =
                            new FileOutputStream(
                                    outputFile
                            );

                    byte[] buffer =
                            new byte[8192];

                    int bytesRead;

                    while (
                            (bytesRead =
                                    inputStream.read(buffer))
                                    != -1
                    ) {

                        outputStreamFile.write(
                                buffer,
                                0,
                                bytesRead
                        );
                    }

                    outputStreamFile.close();
                    inputStream.close();

                    Log.d(
                            "Ready2SendAPI",
                            "Video saved: "
                                    + outputFile.getAbsolutePath()
                    );

                    processedVideoFile = outputFile;
                    processingFinished = true;

                    runOnUiThread(() -> {

                        TextView processingText =
                                progressDialog.findViewById(
                                        R.id.processingText
                                );

                        android.content.SharedPreferences preferences =
                                getSharedPreferences(
                                        "Ready2SendPrefs",
                                        MODE_PRIVATE
                                );

                        boolean openWhatsApp =
                                preferences.getBoolean(
                                        "open_whatsapp_after_processing",
                                        true
                                );

                        processingText.setText(
                                openWhatsApp
                                        ? "Sending to WhatsApp..."
                                        : "Opening Share Sheet..."
                        );

                        checkIfReadyToShare();
                    });

                } else {

                    InputStream errorStream =
                            connection.getErrorStream();

                    String errorMessage =
                            "The server could not process this video.";

                    if (errorStream != null) {

                        BufferedReader reader =
                                new BufferedReader(
                                        new InputStreamReader(
                                                errorStream,
                                                StandardCharsets.UTF_8
                                        )
                                );

                        StringBuilder response =
                                new StringBuilder();

                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        reader.close();

                        String responseBody =
                                response.toString();

                        Log.e(
                                "Ready2SendAPI",
                                "Server response: "
                                        + responseBody
                        );

                        if (responseBody.contains("\"error\"")) {

                            int errorStart =
                                    responseBody.indexOf(
                                            "\"error\":\""
                                    );

                            if (errorStart != -1) {

                                errorStart +=
                                        "\"error\":\"".length();

                                int errorEnd =
                                        responseBody.indexOf(
                                                "\"",
                                                errorStart
                                        );

                                if (errorEnd != -1) {

                                    errorMessage =
                                            responseBody.substring(
                                                    errorStart,
                                                    errorEnd
                                            );
                                }
                            }
                        }
                    }

                    Log.e(
                            "Ready2SendAPI",
                            "Server returned: " + responseCode
                    );

                    showError(errorMessage);
                }

                connection.disconnect();

            } catch (Exception e) {

                Log.e(
                        "Ready2SendAPI",
                        "Request failed",
                        e
                );

                showError(
                        "Could not connect to the Ready2Send server."
                );
            }

        }).start();
    }

    private void shareVideo(File videoFile) {

        Uri videoUri =
                FileProvider.getUriForFile(
                        this,
                        getPackageName()
                                + ".fileprovider",
                        videoFile
                );

        Intent shareIntent =
                new Intent(Intent.ACTION_SEND);

        shareIntent.setType("video/mp4");

        shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                videoUri
        );

        shareIntent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
        );

        android.content.SharedPreferences preferences =
                getSharedPreferences(
                        "Ready2SendPrefs",
                        MODE_PRIVATE
                );

        boolean openWhatsApp =
                preferences.getBoolean(
                        "open_whatsapp_after_processing",
                        true
                );

        if (openWhatsApp) {

            // Open WhatsApp directly
            try {

                shareIntent.setPackage("com.whatsapp");

                startActivity(shareIntent);
                finish();

            } catch (Exception e) {

                Log.e(
                        "SHARE",
                        "WhatsApp could not be opened",
                        e
                );

                showError(
                        "WhatsApp is not installed on this device."
                );
            }

        } else {

            // Open Android Share Sheet
            try {

                Intent chooser =
                        Intent.createChooser(
                                shareIntent,
                                "Send video with"
                        );

                startActivity(chooser);
                finish();

            } catch (Exception e) {

                Log.e(
                        "SHARE",
                        "Share Sheet could not be opened",
                        e
                );

                showError(
                        "Could not open the Share Sheet."
                );
            }
        }
    }

    private void showProcessingDialog() {
        progressDialog = new Dialog(this);
        progressDialog.setContentView(
                R.layout.processing_dialog
        );

        progressDialog.setCancelable(false);
        Window window = progressDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT)
            );
        }
        progressDialog.show();

        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private void showError(String message) {
        runOnUiThread(() -> {

            if (progressDialog != null
                    && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Dialog errorDialog = new Dialog(this);

            errorDialog.setContentView(
                    R.layout.error_dialog
            );
            errorDialog.setCancelable(false);

            Window window = errorDialog.getWindow();

            if (window != null) {

                window.setBackgroundDrawable(
                        new ColorDrawable(Color.TRANSPARENT)
                );
            }

            TextView errorMessage =
                    errorDialog.findViewById(
                            R.id.errorMessage
                    );

            TextView errorButton =
                    errorDialog.findViewById(
                            R.id.errorButton
                    );

            errorMessage.setText(message);

            errorButton.setOnClickListener(v -> {

                errorDialog.dismiss();
                finish();

            });

            errorDialog.show();

            if (window != null) {

                window.setLayout(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                );
            }
        });
    }

    private void showInterstitialAdIfReady() {

        if (!adDelayFinished || adWaitExpired) {
            return;
        }

        if (interstitialAd != null) {

            Log.d(
                    "Ready2SendAds",
                    "Showing interstitial ad"
            );

            interstitialAd.show(this);

        } else {

            Log.d(
                    "Ready2SendAds",
                    "Interstitial ad not ready yet"
            );
        }
    }

    private void checkIfReadyToShare() {

        if (processingFinished && adFinished && !shareTriggered) {

            shareTriggered = true;

            new android.os.Handler(
                    android.os.Looper.getMainLooper()
            ).postDelayed(() -> {

                if (progressDialog != null
                        && progressDialog.isShowing()) {

                    progressDialog.dismiss();
                }

                shareVideo(processedVideoFile);

            }, 700);
        }
    }
}