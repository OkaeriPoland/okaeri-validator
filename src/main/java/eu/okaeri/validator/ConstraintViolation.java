package eu.okaeri.validator;

import lombok.*;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ConstraintViolation {
    private String field;
    private String message;
}
