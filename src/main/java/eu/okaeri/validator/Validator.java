package eu.okaeri.validator;

import lombok.NonNull;

import java.lang.reflect.Parameter;
import java.util.Set;

public interface Validator {

    Set<ConstraintViolation> validate(@NonNull Object object);

    Set<ConstraintViolation> validateProperty(@NonNull Object object, @NonNull String fieldName);

    Set<ConstraintViolation> validatePropertyValue(@NonNull Class<?> type, @NonNull String fieldName, Object fieldValue);

    Set<ConstraintViolation> validateParameters(@NonNull Parameter[] parameters, @NonNull Object[] values);
}
