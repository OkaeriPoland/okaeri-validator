package eu.okaeri.validator;

import java.util.Set;

public interface Validator {

    Set<ConstraintViolation> validate(Object object);

    Set<ConstraintViolation> validateProperty(Object object, String fieldName);

    Set<ConstraintViolation> validateValue(String fieldName, Object fieldValue);
}
