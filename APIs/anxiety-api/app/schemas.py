from uuid import UUID
from pydantic import BaseModel, Field
from typing import List

class WindowResult(BaseModel):
    id: int
    start: int
    end: int
    anxiety: float = Field(ge=0, le=10)

class VideoResult(BaseModel):
    videoId: UUID
    windows: List[WindowResult]
    overall: float = Field(ge=0, le=10)
    framesAnalyzed: int
    discardedWindows: int
    version: str = "1.0.0"
    sparklineB64: str 