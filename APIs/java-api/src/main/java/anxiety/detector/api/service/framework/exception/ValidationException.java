package anxiety.detector.api.service.framework.exception;

public class ValidationException extends RuntimeException {

    private final ErrorResponse errorResponse;

    public ValidationException(ErrorResponse errorResponse) {
        super("Validation error");      
        this.errorResponse = errorResponse;
    }

    public ErrorResponse getErrorResponse() { 
      return errorResponse; 
    }
}
