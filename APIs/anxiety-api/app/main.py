from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.api.endpoints import router as v1_router
import logging, logging.config, pathlib, sys
logging.config.fileConfig(pathlib.Path(__file__).parent.parent / "logging.ini", disable_existing_loggers=False)

app = FastAPI(title="Anxiety Prototype API", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(v1_router, prefix="/api")

@app.get("/health", tags=["system"])
async def health():
    return {"status": "up"}