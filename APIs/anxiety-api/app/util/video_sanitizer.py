import subprocess, uuid, os
from pathlib import Path
from app.config import settings

def sanitize_video(raw_path: Path) -> Path:
    if not settings.SANITIZE_VIDEO:
        return raw_path

    clean_path = raw_path.with_name(f"{uuid.uuid4().hex}_clean.mp4")

    cmd = [
        "ffmpeg", "-y",
        "-i", str(raw_path),
        "-vf", f"fps={settings.TARGET_FPS},format=yuv420p", 
        "-vsync", "1",                 
        "-c:v", "libx264",
        "-preset", "ultrafast",
        "-crf", "23",
        "-movflags", "+faststart",      
        "-an",
        str(clean_path),
    ]
    subprocess.check_call(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    return clean_path
