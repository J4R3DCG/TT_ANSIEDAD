package anxiety.detector.api.service.framework.util;

import java.util.regex.Pattern;

import anxiety.detector.api.service.domain.model.ValidationError;
import anxiety.detector.api.service.framework.exception.ErrorResponse;
import anxiety.detector.api.service.framework.exception.ValidationException;

public final class EmailValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private EmailValidator() { 
    }

    public static void validate(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throwValidation("Invalid email format", 1000);
        }
    }

    private static void throwValidation(String details, int code) {
        ValidationError ve = new ValidationError("Validation Error", details, code);
        throw new ValidationException(ErrorResponse.of(ve));
    }
}
