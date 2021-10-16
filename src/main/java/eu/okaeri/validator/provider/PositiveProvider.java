package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Positive;

import java.lang.reflect.Type;
import java.util.Set;

public class PositiveProvider implements ValidationProvider<Positive> {

    @Override
    public Class<Positive> getAnnotation() {
        return Positive.class;
    }

    @Override
    public Set<ConstraintViolation> validate(Positive annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i > 0);
    }
}
