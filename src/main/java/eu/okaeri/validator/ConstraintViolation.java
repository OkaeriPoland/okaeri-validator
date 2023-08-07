package eu.okaeri.validator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstraintViolation {
    private final ViolationPath path;
    private String message;
    private String type;

    private boolean cascading;

    public ConstraintViolation(String field, String element, String message, String type) {
        this(new ViolationPath(field, element), message, type, false);
    }

    public ConstraintViolation(String field, String message, String type) {
        this(field, null, message, type);
    }

    @Deprecated
    public String getField() {
        return this.path.getField();
    }

    @Deprecated
    public void setField(String field) {
        this.path.setField(field);
    }
}
