import cv2, numpy as np
from typing import List
import mediapipe as mp
from app.config import settings
from app.util.errors import proto_error
from fastapi import status


mp_fd = mp.solutions.face_detection


class FacePreprocessor:
    def __init__(self, img_size: int = settings.IMG_SIZE, grayscale: bool = settings.GRAYSCALE):
        self.img_size  = img_size
        self.grayscale = grayscale
        self.detector  = mp_fd.FaceDetection(model_selection=1, min_detection_confidence=0.5)

    def _crop_face(self, frame):
        try:
            res = self.detector.process(
                cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            )
        except ValueError as e:
            proto_error(
                status.HTTP_422_UNPROCESSABLE_ENTITY,
                "NON MONOTONIC TIMESTAMPS: The video contains non-monotonic timestamps in the frames. Please re-encode the video before uploading.",
                "The video contains non-monotonic timestamps in the frames. Please re-encode the video before uploading."
            )

        if not res.detections:
            return None

        bbox = res.detections[0].location_data.relative_bounding_box
        h, w, _ = frame.shape
        x, y, bw, bh = int(bbox.xmin * w), int(bbox.ymin * h), int(bbox.width * w), int(bbox.height * h)

        size = max(bw, bh)
        cx, cy = x + bw // 2, y + bh // 2
        x1, y1 = max(cx - size // 2, 0), max(cy - size // 2, 0)
        x2, y2 = min(x1 + size, w),     min(y1 + size, h)

        crop = frame[y1:y2, x1:x2]
        if crop.size == 0:
            return None

        crop = cv2.resize(crop, (self.img_size, self.img_size))

        if self.grayscale:                      
            crop = cv2.cvtColor(crop, cv2.COLOR_BGR2GRAY)
            crop = crop[..., np.newaxis]     

        return crop.astype("float32") / 255.0


    def preprocess_window(self, frames: List) -> np.ndarray | None:
        processed = []
        for f in frames:
            face = self._crop_face(f)
            processed.append(face if face is not None else None)

        valid = [f for f in processed if f is not None]
        if len(valid) < int(0.6 * len(frames)):    
            return None

        arr = np.stack([f if f is not None else valid[-1] for f in processed])
        return arr
