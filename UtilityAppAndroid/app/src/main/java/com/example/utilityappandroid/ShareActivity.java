package com.example.utilityappandroid;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setStatusBarColor(
                android.graphics.Color.TRANSPARENT
        );
        handleSharedContent(getIntent());
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
    }

    private void sendURLToFlow(String url) {
        new Thread(() -> {
            try {
                URL apiUrl =
                        new URL("http://192.168.18.163:8000/process");
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

                    runOnUiThread(() -> {

                        TextView processingText =
                                progressDialog.findViewById(
                                        R.id.processingText
                                );

                        processingText.setText(
                                "Sending to WhatsApp..."
                        );

                        new android.os.Handler(
                                android.os.Looper.getMainLooper()
                        ).postDelayed(() -> {

                            if (progressDialog != null
                                    && progressDialog.isShowing()) {

                                progressDialog.dismiss();
                            }

                            shareVideo(outputFile);

                        }, 1000);

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

        // Directly open WhatsApp
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
}