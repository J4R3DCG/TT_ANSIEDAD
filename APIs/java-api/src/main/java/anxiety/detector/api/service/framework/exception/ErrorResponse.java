package anxiety.detector.api.service.framework.exception;

import anxiety.detector.api.service.domain.model.ValidationError;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public final class ErrorResponse {

    private final List<ValidationError> errors;
    private final LocalDateTime timestamp = LocalDateTime.now();

    public static ErrorResponse of(ValidationError... errs) {
        return new ErrorResponse(List.of(errs));
    }

    public ErrorResponse(List<ValidationError> errors) {
        this.errors = Collections.unmodifiableList(errors);
    }

    public List<ValidationError> getErrors() { 
        return errors; 
    }
    
    public LocalDateTime getTimestamp() { 
        return timestamp; 
    }
}
