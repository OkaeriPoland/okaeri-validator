package eu.okaeri.validator;


import eu.okaeri.validator.policy.NullPolicy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OkaeriValidator implements Validator {

    private final NullPolicy nullPolicy;

    public static OkaeriValidator of() {
        return of(NullPolicy.NULLABLE);
    }

    public static OkaeriValidator of(NullPolicy nullPolicy) {
        return new OkaeriValidator(nullPolicy);
    }

    @Override
    public Set<ConstraintViolation> validate(@NonNull Object object) {
        return Arrays.stream(object.getClass().getFields())
                .flatMap(field -> this.validatePropertyValue(object.getClass(), field.getName(), this.fieldValueOrNull(object, field)).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateProperty(@NonNull Object object, @NonNull String fieldName) {
        return this.validatePropertyValue(object.getClass(), fieldName, this.fieldValueOrNull(object, fieldName));
    }

    @Override
    public Set<ConstraintViolation> validatePropertyValue(@NonNull Class<?> type, @NonNull String fieldName, Object fieldValue) {
        return null; // TODO
    }

    @Override
    public Set<ConstraintViolation> validateParameters(@NonNull Parameter[] parameters, @NonNull Object[] values) {
        return null; // TODO
    }

    private Object fieldValueOrNull(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            return null;
        }
    }

    private Object fieldValueOrNull(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }
}
