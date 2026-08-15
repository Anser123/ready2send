# Ready2Send

> Get your videos ready to share.

Ready2Send is an Android application designed to simplify the process of sharing supported online videos through WhatsApp.

Instead of manually downloading, converting, and preparing a video for sharing, users can share a supported video link directly with Ready2Send. The application sends the link to its backend, where the video is processed and returned as a shareable MP4.

## ✨ Features

- Share supported video links directly from Android's share menu
- Automatic video downloading and preparation
- Video conversion to MP4
- Automatic processing for sharing
- Direct handoff to WhatsApp
- Supported platform detection
- Clean dark-themed interface
- Settings and app information
- Privacy Policy and Terms of Use
- Premium section prepared for future development

## 📱 How It Works

Ready2Send follows a simple three-step workflow:

### 1. Share

Share a video link from a supported application with Ready2Send.

### 2. Prepare

Ready2Send sends the link to its backend, where the video is downloaded and processed into an MP4 file.

### 3. Send

Once processing is complete, the prepared video is passed to WhatsApp and is ready to be sent.

## 🏗️ How Ready2Send Works

The overall Ready2Send flow is illustrated below:

<p align="center">
  <img src="AppFlow.png" alt="Ready2Send application flow" width="900">
</p>

The application follows a simple client-server workflow:

1. **Share** — A supported video link is shared with Ready2Send from Android's share menu.
2. **Prepare** — The Android app sends the link to the backend, where the video is downloaded and processed into an MP4.
3. **Send** — The prepared video is returned to the Android app and passed to WhatsApp, ready to send.

## 🛠️ Tech Stack

### Android

- Java
- XML
- Android Studio
- Android Intents
- FileProvider
- SharedPreferences

### Backend

- Python
- FastAPI
- yt-dlp
- FFmpeg
- FFprobe

### Development

- Git & GitHub
- REST API architecture
- HTTP communication
- Physical Android device testing

## 📸 Screenshots

Here is the Ready2Send experience from start to finish.

### 1. Share from a supported app

Ready2Send appears directly in Android's share sheet. Select Ready2Send after finding a video you want to prepare for sharing.

<p align="center">
  <img src="App%20Screenshots/Icon%5FShareSheet.jpeg" alt="Ready2Send in the Android share sheet" width="300">
</p>

### 2. Processing the video

After receiving the shared link, Ready2Send starts preparing the video through the backend.

<p align="center">
  <img src="App%20Screenshots/Processing.jpeg" alt="Ready2Send processing a video" width="300">
</p>

### 3. Sending to WhatsApp

Once processing is complete, Ready2Send hands the prepared video over to WhatsApp.

<p align="center">
  <img src="App%20Screenshots/Sending.jpeg" alt="Ready2Send sending the video to WhatsApp" width="300">
</p>

### 4. Main screen

The main screen provides the central Ready2Send workflow and shows the supported platforms.

<p align="center">
  <img src="App%20Screenshots/Main.jpeg" alt="Ready2Send main screen" width="300">
</p>

### 5. Settings

The settings area provides access to app information, privacy policy, and terms of use.

<p align="center">
  <img src="App%20Screenshots/Settings.jpeg" alt="Ready2Send settings screen" width="300">
</p>

### 6. Premium

The premium section is prepared for future subscription features and an ad-free experience.

<p align="center">
  <img src="App%20Screenshots/Premium.jpeg" alt="Ready2Send premium screen" width="300">
</p>

## 🚀 Project Status

Ready2Send is currently under active development.

The Android application and backend processing pipeline are functional, while additional improvements, infrastructure, and production deployment are being developed.

## 🔮 Planned Improvements

- Production backend deployment
- Improved download reliability
- Faster video processing
- Better handling of large videos
- Additional supported platforms
- User authentication
- Usage limits and account management
- Premium subscription functionality
- Improved error handling and retry mechanisms
- Production monitoring and logging

## 🔐 Privacy & Legal

Ready2Send includes an in-app Privacy Policy and Terms of Use.

The application is designed around processing user-provided video links and does not require users to provide social-media account credentials.

Users are responsible for ensuring that their use of the application complies with the terms and policies of the platforms and content they access.

## 👨‍💻 About the Project

Ready2Send is an independent software project developed as part of my journey toward becoming a backend developer.

The project provides hands-on experience building a complete client-server application, including an Android client, REST API, server-side video processing, external tool integration, file handling, and deployment.

The long-term goal is to continue improving the backend architecture and eventually transition the backend into a Java/Spring Boot-based production system.

## 📄 License

This project is currently intended primarily as a learning and portfolio project.
