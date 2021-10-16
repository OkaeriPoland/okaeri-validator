package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.PositiveOrZero;

import java.lang.reflect.Type;
import java.util.Set;

public class PositiveOrZeroProvider implements ValidationProvider<PositiveOrZero> {

    @Override
    public Class<PositiveOrZero> getAnnotation() {
        return PositiveOrZero.class;
    }

    @Override
    public Set<ConstraintViolation> validate(PositiveOrZero annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i >= 0);
    }
}
