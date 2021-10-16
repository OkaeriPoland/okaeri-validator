package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Min;

import java.lang.reflect.Type;
import java.util.Set;

public class MinProvider implements ValidationProvider<Min> {

    @Override
    public Class<Min> getAnnotation() {
        return Min.class;
    }

    @Override
    public Set<ConstraintViolation> validate(Min annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, annotation.value(), annotation.message(), (i) -> i >= 0);
    }
}
