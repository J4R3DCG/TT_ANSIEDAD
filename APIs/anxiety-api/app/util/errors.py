import logging
from fastapi import HTTPException, status

log = logging.getLogger("app")

def proto_error(code: int, detail: str, reason: str):
    log.error("%s – %s", detail, reason)
    raise HTTPException(
        status_code=code,
        detail=detail,
        headers={"x-reason": reason}
    )
