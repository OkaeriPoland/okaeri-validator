package eu.okaeri.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class ConstraintViolation {
    private String field;
    private String path;
    private String message;
    private String type;

    public ConstraintViolation(String field, String message, String type) {
        this(field, null, message, type);
    }

    public String prepareElement() {
        return this.field + (this.path != null ? this.path : "");
    }
}
