import cv2
from collections import deque
from pathlib import Path
from typing import Iterator
from app.config import settings
from app.util.errors import proto_error
from fastapi import status

class WindowExtractor:
    def __init__(self, size: int = settings.WINDOW_SIZE, stride: int = settings.WINDOW_STRIDE): self.size, self.stride = size, stride

    def iter_windows(self, video_path: Path) -> Iterator[tuple[int, list]]:
        cap = cv2.VideoCapture(str(video_path))
        if not cap.isOpened():
            proto_error(status.HTTP_400_BAD_REQUEST, "CANNOT OPEN VIDEO: External framework could not open or decode the file ", "OpenCV could not open or decode the file")

        buffer = deque(maxlen=self.size)         
        frame_idx = 0                           
        win_id = 0                           

        while True:
            ret, frame = cap.read()
            if not ret:
                break                           

            buffer.append(frame)
            frame_idx += 1

            if len(buffer) == self.size and (frame_idx - self.size) % self.stride == 0:
                yield win_id, list(buffer)
                win_id += 1

        cap.release()