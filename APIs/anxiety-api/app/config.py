from pydantic_settings import BaseSettings, SettingsConfigDict
from pathlib import Path

BASE_DIR = Path(__file__).resolve().parent.parent  

class Settings(BaseSettings):
    WINDOW_SIZE: int = 50
    WINDOW_STRIDE: int = 50
    IMG_SIZE: int = 224
    GRAYSCALE: bool = False
    MODEL_PATH: str = str(BASE_DIR / "app" / "model" / "model21.keras")
    MAX_SYNC_SECONDS: int = 600
    TMP_DIR: str = "/tmp/anx"
    SANITIZE_VIDEO: bool = True    
    TARGET_FPS: int = 30    

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

settings = Settings()