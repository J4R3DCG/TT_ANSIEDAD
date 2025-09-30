import logging, json, shutil, tempfile, numpy as np
from concurrent.futures import ThreadPoolExecutor, as_completed
from uuid import uuid4
from pathlib import Path
from fastapi import APIRouter, File, UploadFile, Depends, status
from fastapi.encoders import jsonable_encoder
from app.util.video_sanitizer import sanitize_video

from app.schemas       import VideoResult, WindowResult
from app.dependencies  import (
    get_window_extractor, get_face_preprocessor,
    get_model_service,   get_postprocessor
)
from app.util.errors   import proto_error

router  = APIRouter(tags=["analysis"])
TMP_DIR = Path(tempfile.gettempdir()) / "anx"
TMP_DIR.mkdir(exist_ok=True)

log = logging.getLogger("app.api.analysis")

MAX_WORKERS  = 1             
BATCH_SIZE   = 8             

@router.post("/process-video", response_model=VideoResult)
async def process_video(
    video: UploadFile = File(...),
    window_extractor = Depends(get_window_extractor),
    preprocessor     = Depends(get_face_preprocessor),
    model_srv        = Depends(get_model_service),
    postprocessor    = Depends(get_postprocessor),
):

    if video.content_type not in {"video/mp4", "video/avi"}:
        proto_error(status.HTTP_415_UNSUPPORTED_MEDIA_TYPE,"UNSUPPORTED FORMAT",f"MIME {video.content_type} not allowed")

    tmp_path = TMP_DIR / f"{uuid4()}.{video.filename.split('.')[-1]}"
    with tmp_path.open("wb") as f:
        shutil.copyfileobj(video.file, f)

    clean_path = sanitize_video(tmp_path)
    tensors, meta = [], []  

    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as pool:
        futures = {
            pool.submit(preprocessor.preprocess_window, frames): (win_id, frames)
            for win_id, frames in window_extractor.iter_windows(clean_path)
        }

        for fut in as_completed(futures):
            win_id, _ = futures[fut]
            tensor = fut.result()
            if tensor is None:
                continue
            tensors.append(tensor)
            meta.append((
                win_id,
                win_id * window_extractor.stride,
                win_id * window_extractor.stride + window_extractor.size - 1
            ))

    if not tensors:
        proto_error(status.HTTP_400_BAD_REQUEST,"NO VALID WINDOWS: The video is too short or there were no faces detected","The video is too short or there were no faces detected")

    preds = model_srv.predict_batch(
        np.stack(tensors, axis=0),      
        batch_size=BATCH_SIZE,
    )

    window_results = [
        WindowResult(id=w_id, start=s, end=e, anxiety=float(p))
        for (w_id, s, e), p in zip(meta, preds)
    ]

    overall   = postprocessor.aggregate(window_results)
    sparkline = postprocessor.sparkline_b64(window_results)

    result = VideoResult(
        videoId          = uuid4(),
        windows          = window_results,
        overall          = overall,
        framesAnalyzed   = len(window_results) * window_extractor.size,
        discardedWindows = (len(meta) - len(window_results)),
        version          = "1.1.0",
        sparklineB64     = sparkline
    )

    log.info("VideoResult:\n%s", json.dumps(jsonable_encoder(result), indent=2))
    
    for p in (tmp_path, clean_path):
        try:
            p.unlink(missing_ok=True)
        except Exception:
            pass

    return result
