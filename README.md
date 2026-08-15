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

## 🏗️ Architecture

```text
┌──────────────────────────────┐
│       Android Application    │
│                              │
│          Java + XML          │
│                              │
│  • Share Intent              │
│  • UI / Activities           │
│  • FileProvider              │
│  • API Communication         │
└──────────────┬───────────────┘
               │
               │ HTTP API
               ▼
┌──────────────────────────────┐
│        Backend Server        │
│                              │
│        Python + FastAPI      │
│                              │
│  • API endpoints             │
│  • Video processing          │
│  • yt-dlp                    │
│  • FFmpeg / FFprobe          │
└──────────────────────────────┘
```

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

| Main | Share Integration |
|---|---|
| ![Ready2Send Main Screen](App%20Screenshots/Main.jpeg) | ![Ready2Send Share Integration](App%20Screenshots/Icon%5FShareSheet.jpeg) |

| Processing | Sending |
|---|---|
| ![Ready2Send Processing](App%20Screenshots/Processing.jpeg) | ![Ready2Send Sending](App%20Screenshots/Sending.jpeg) |

| Settings | Premium |
|---|---|
| ![Ready2Send Settings](App%20Screenshots/Settings.jpeg) | ![Ready2Send Premium](App%20Screenshots/Premium.jpeg) |

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
