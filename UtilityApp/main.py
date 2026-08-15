import os
import json
import subprocess
import yt_dlp

from fastapi import FastAPI
from fastapi.responses import FileResponse, JSONResponse
from pydantic import BaseModel
from yt_dlp.utils import DownloadError

# API Section

app = FastAPI()

TARGET_SIZE_MB = 95     # 100MB WhatsApp limit

class ProcessRequest(BaseModel):
    url: str

class VideoTooLargeError(Exception):
    pass

@app.post("/process")
def process(request: ProcessRequest):
    cleanup_files_from_folders()

    try:
        download_result = download_video(request.url)
    except VideoTooLargeError:
        return JSONResponse(
            status_code=413,
            content={
                "success": False,
                "error": "Video is too large to send."
            }
        )

    if isinstance(download_result, dict):
        if not download_result["success"]:
            return JSONResponse(
                status_code=500,
                content={
                    "success": False,
                    "error": download_result["error"]
                }
            )
        video_path = download_result["path"]
    else:
        video_path = download_result

    if video_path is None:
        return JSONResponse(
            status_code=500,
            content={
                "success": False,
                "error": "Could not download video."
            }
        )

    result = process_video(video_path)

    if not result["success"]:
        status_code = (
            413
            if result["error"] == "Video is too large to send."
            else 500
        )

        return JSONResponse(
            status_code=status_code,
            content={
                "success": False,
                "error": result["error"]
            }
        )

    return FileResponse(
        result["path"],
        media_type="video/mp4",
        filename="video.mp4"
    )

# Functions Section

def download_video(url):
    if "instagram.com" in url:
        command = [
            "parth-dl",
            url,
            "-P",
            "downloads",
            "-f"
        ]

        result = subprocess.run(
            command,
            capture_output=True,
            text=True
        )

        if result.returncode != 0:
            print("Instagram download failed.")
            print(result.stderr)
            return None

        for filename in os.listdir("downloads"):
            if filename.lower().endswith(".mp4"):
                return os.path.join("downloads", filename)

        print("Instagram download completed but no MP4 was found.")
        return None

    ydl_opts = {
        "outtmpl": "downloads/%(id)s.%(ext)s",
        "format": "(bestvideo[height<=720]/bestvideo[width<=720]/bestvideo)+bestaudio/best",
        "merge_output_format": "mp4"
    }

    max_retries = 3

    for attempt in range(1, max_retries + 1):
        try:
            print(f"Download attempt {attempt}/{max_retries}")

            with yt_dlp.YoutubeDL(ydl_opts) as ydl:
                info = ydl.extract_info(
                    url,
                    download=True
                )

                video_path = ydl.prepare_filename(info)

                if not os.path.exists(video_path):
                    base_path = os.path.splitext(video_path)[0]
                    mp4_path = base_path + ".mp4"

                    if os.path.exists(mp4_path):
                        video_path = mp4_path

                return {
                    "success": True,
                    "path": video_path,
                    "error": None
                }

        except DownloadError as e:
            error_text = str(e)

            print(
                f"Download failed on attempt {attempt}."
            )
            print(error_text)

            if (
                "Sign in to confirm" in error_text
                or "not a bot" in error_text
                or "cookie" in error_text.lower()
            ):
                return {
                    "success": False,
                    "error": (
                        "Couldn't process this video right now. "
                        "Please try again."
                    )
                }

            if attempt < max_retries:
                print("Retrying...")
            else:
                print("Maximum retries reached.")

                return {
                    "success": False,
                    "error": "Could not download this video."
                }

    return {
        "success": False,
        "error": "Could not download this video."
    }


def validate_video(input_video):
    if not os.path.exists(input_video):
        return {
            "valid": False,
            "error": "File does not exist"
        }

    if not os.path.isfile(input_video):
        return {
            "valid": False,
            "error": "Path is not a file"
        }

    if os.path.getsize(input_video) == 0:
        return {
            "valid": False,
            "error": "File is empty"
        }

    command = [
        "ffprobe",
        "-v", "error",
        "-select_streams", "v:0",
        "-show_entries", "stream=codec_name",
        "-of", "default=noprint_wrappers=1:nokey=1",
        input_video
    ]

    result = subprocess.run(
        command,
        capture_output=True,
        text=True
    )

    if result.returncode != 0:
        return {
            "valid": False,
            "error": "File is not a valid video"
        }

    if not result.stdout.strip():
        return {
            "valid": False,
            "error": "No video stream found"
        }

    return {
        "valid": True,
        "error": None
    }


def get_video_size(video_path):
    result = subprocess.run(
        [
            "ffprobe",
            "-v", "quiet",
            "-print_format", "json",
            "-show_format",
            video_path
        ],
        capture_output=True,
        text=True
    )

    video_info = json.loads(result.stdout)

    return (
        int(video_info["format"]["size"])
        / (1024 * 1024)
    )


def process_video(
    input_video,
    target_size_mb=TARGET_SIZE_MB
):
    validation = validate_video(input_video)

    if not validation["valid"]:
        return {
            "success": False,
            "path": None,
            "size_mb": None,
            "error": validation["error"]
        }

    file_size = get_video_size(input_video)

    if file_size > target_size_mb:
        print(
            f"Video is too large: "
            f"{file_size:.2f} MB"
        )

        return {
            "success": False,
            "path": None,
            "size_mb": file_size,
            "error": "Video is too large to send."
        }

    print(
        f"Video is ready: "
        f"{file_size:.2f} MB"
    )

    return {
        "success": True,
        "path": input_video,
        "size_mb": file_size,
        "error": None
    }


def cleanup_files_from_folders():
    for folder in [
        "downloads",
        os.path.join("videos", "output")
    ]:
        if not os.path.exists(folder):
            continue

        for filename in os.listdir(folder):
            file_path = os.path.join(folder, filename)

            if os.path.isfile(file_path):
                try:
                    os.remove(file_path)
                    print(f"Deleted: {file_path}")
                except Exception as e:
                    print(
                        f"Could not delete "
                        f"{file_path}: {e}"
                    )

# Test Section

if __name__ == "__main__":
    url = ("https://youtu.be/GFOIHk7sn_k?si=bZJxDc6WuTbrK_kZ")

    video_path = download_video(url)

    if video_path is None:
        print("Could not download the video.")
    else:
        print("Downloaded video:", video_path)

        result = process_video(video_path)

        print("Processing result:", result)