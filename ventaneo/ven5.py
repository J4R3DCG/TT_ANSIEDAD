import cv2
import os
import csv
from pathlib import Path

# --- Configuración ---
input_folder = ""
output_csv = "fragmentos.csv"
output_folder = "ventanas"
video_extensions = [".mp4", ".avi", ".mov", ".mkv"]

# Crear archivo CSV si no existe
if not os.path.exists(output_csv):
    with open(output_csv, mode="w", newline="") as f:
        writer = csv.writer(f)
        writer.writerow(["video_id", "ventana_id", "t_inicio", "t_fin", "num_frames", "ruta_video"])

# Crear carpeta de salida
os.makedirs(output_folder, exist_ok=True)

# Leer todos los videos
video_paths = [v for ext in video_extensions for v in Path(input_folder).glob(f"*{ext}")]
print(f"\nSe encontraron {len(video_paths)} videos.")

for video_path in video_paths:
    video_id = video_path.stem
    cap = cv2.VideoCapture(str(video_path))

    if not cap.isOpened():
        print(f"No se pudo abrir el video {video_id}")
        continue

    fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    duration_total_video = total_frames / fps

    print(f"\n--- Procesando video: {video_id} ---")
    print(f"Duración total: {duration_total_video:.2f} segundos ({total_frames} frames)")

    inicio = float(input("¿Desde qué segundo quieres iniciar el ventaneo? "))
    duracion = float(input("¿Cuántos segundos desde el inicio quieres considerar? "))
    n_frames_ventana = int(input("¿Cuántos frames quieres por ventana? "))

    frame_inicio_total = int(inicio * fps)
    frame_fin_total = int((inicio + duracion) * fps)

    cap.set(cv2.CAP_PROP_POS_FRAMES, frame_inicio_total)

    ventana_id = 0
    while cap.get(cv2.CAP_PROP_POS_FRAMES) < frame_fin_total:
        frames = []
        t_inicio = cap.get(cv2.CAP_PROP_POS_MSEC) / 1000  # tiempo en segundos

        while len(frames) < n_frames_ventana:
            ret, frame = cap.read()
            if not ret:
                break
            frames.append(frame)

        if len(frames) < n_frames_ventana:
            print(f"Ventana {ventana_id} descartada por pocos frames ({len(frames)}).")
            break

        t_fin = cap.get(cv2.CAP_PROP_POS_MSEC) / 1000

        # Guardar fragmento de video
        subfolder = os.path.join(output_folder, video_id)
        os.makedirs(subfolder, exist_ok=True)
        output_video_path = os.path.join(subfolder, f"ventana_{ventana_id}.mp4")

        h, w, _ = frames[0].shape
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        out = cv2.VideoWriter(output_video_path, fourcc, fps, (w, h))

        for frame in frames:
            out.write(frame)
        out.release()

        # Guardar metadatos de la ventana
        with open(output_csv, mode="a", newline="") as f:
            writer = csv.writer(f)
            writer.writerow([video_id, ventana_id, round(t_inicio, 2), round(t_fin, 2), len(frames), output_video_path])

        print(f"✅ Ventana {ventana_id} guardada con {len(frames)} frames.")
        ventana_id += 1

    cap.release()

print("\n🎬 Ventaneo completado. Archivo CSV guardado:", output_csv)
