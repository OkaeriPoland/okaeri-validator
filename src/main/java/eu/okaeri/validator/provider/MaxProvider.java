package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Max;

import java.lang.reflect.Type;
import java.util.Set;

public class MaxProvider implements ValidationProvider<Max> {

    @Override
    public Class<Max> getAnnotation() {
        return Max.class;
    }

    @Override
    public Set<ConstraintViolation> validate(Max annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, annotation.value(), annotation.message(), (i) -> i <= 0);
    }
}
