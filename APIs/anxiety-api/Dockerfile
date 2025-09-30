FROM python:3.11-slim

ENV PYTHONUNBUFFERED=1 \
    TF_CPP_MIN_LOG_LEVEL=2 \
    \
    GUNI_WORKERS=1 \
    GUNI_TIMEOUT=600

WORKDIR /app

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
      libgl1 \
      libglib2.0-0 && \
    rm -rf /var/lib/apt/lists/*
    
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app app
COPY logging.ini .

VOLUME ["/app/model"]

EXPOSE 5000

ENTRYPOINT ["bash", "-c", \
  "gunicorn \
     -k uvicorn.workers.UvicornWorker \
     --bind 0.0.0.0:5000 \
     --workers ${GUNI_WORKERS} \
     --timeout ${GUNI_TIMEOUT} \
     --preload \
     app.main:app"]
