package eu.okaeri.validator;

import lombok.*;

@Data
@AllArgsConstructor
public class ConstraintViolation {
    private String field;
    private String message;
    private String type;
}
