package eu.okaeri.validator;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConstraintViolation {
    private String field;
    private String message;
    private String type;
}
