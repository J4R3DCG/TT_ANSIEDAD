import cv2
import mediapipe as mp
import numpy as np
import os
from pathlib import Path

# === CONFIGURACIÓN ===
IMG_SIZE = 224
TO_GRAYSCALE = False

# === RUTAS ===
CURRENT_DIR = Path(__file__).parent
VENTANAS_DIR = CURRENT_DIR / "ventanas"  # Nueva carpeta base

# Inicializa MediaPipe
mp_face_detection = mp.solutions.face_detection

def procesar_frame(frame, detector):
    results = detector.process(cv2.cvtColor(frame, cv2.COLOR_BGR2RGB))
    if results.detections:
        detection = results.detections[0]
        bbox = detection.location_data.relative_bounding_box
        h, w, _ = frame.shape
        x, y, w_box, h_box = int(bbox.xmin * w), int(bbox.ymin * h), int(bbox.width * w), int(bbox.height * h)

        size = max(w_box, h_box)
        cx = x + w_box // 2
        cy = y + h_box // 2
        x1 = max(cx - size // 2, 0)
        y1 = max(cy - size // 2, 0)
        x2 = min(x1 + size, w)
        y2 = min(y1 + size, h)

        crop = frame[y1:y2, x1:x2]
        if crop.size == 0:
            return None

        resized = cv2.resize(crop, (IMG_SIZE, IMG_SIZE))
        normalized = resized.astype(np.float32) / 255.0

        if TO_GRAYSCALE:
            gray = cv2.cvtColor(normalized, cv2.COLOR_RGB2GRAY)
            return gray[..., np.newaxis]
        else:
            return normalized
    return None

def procesar_ventanas():
    with mp_face_detection.FaceDetection(model_selection=1, min_detection_confidence=0.5) as detector:
        for video_id_folder in VENTANAS_DIR.iterdir():
            if video_id_folder.is_dir():
                print(f"\n📁 Procesando carpeta: {video_id_folder.name}")
                vp_dir = video_id_folder / "VP"
                vp_dir.mkdir(exist_ok=True)

                for video_file in video_id_folder.glob("ventana_*.mp4"):
                    ventana_id = video_file.stem  # 'ventana_0', 'ventana_1', etc.
                    print(f"  🎞️ Procesando {video_file.name}...")

                    out_dir = vp_dir / ventana_id
                    out_dir.mkdir(parents=True, exist_ok=True)

                    cap = cv2.VideoCapture(str(video_file))
                    count = 0
                    saved = 0

                    while cap.isOpened():
                        ret, frame = cap.read()
                        if not ret:
                            break

                        result = procesar_frame(frame, detector)
                        if result is not None:
                            out_path = out_dir / f"frame_{saved:04d}.jpg"
                            if TO_GRAYSCALE:
                                cv2.imwrite(str(out_path), (result * 255).astype(np.uint8))
                            else:
                                # Guardar en formato BGR
                                cv2.imwrite(str(out_path), (result * 255).astype(np.uint8))
                            saved += 1

                        count += 1

                    cap.release()
                    print(f"    ✅ {saved} frames guardados en {out_dir}")

if __name__ == "__main__":
    procesar_ventanas()
