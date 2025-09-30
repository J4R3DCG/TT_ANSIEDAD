from functools import lru_cache
import logging
from app.services.window_extractor import WindowExtractor
from app.services.face_preprocessor import FacePreprocessor
from app.services.model_service import ModelService
from app.services.postprocessor import PostProcessor

log = logging.getLogger("app.dependencies")

@lru_cache
def get_window_extractor():
    return WindowExtractor()

@lru_cache
def get_face_preprocessor():
    return FacePreprocessor()

@lru_cache(maxsize=1)                  
def get_model_service() -> ModelService:
    svc = ModelService()                
    log.info("ModelService instanciado; id=%s", id(svc))
    return svc

@lru_cache
def get_postprocessor():
    return PostProcessor()