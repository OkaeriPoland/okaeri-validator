package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.DecimalMin;

import java.lang.reflect.Type;
import java.util.Set;

public class DecimalMinProvider implements ValidationProvider<DecimalMin> {

    @Override
    public Class<DecimalMin> getAnnotation() {
        return DecimalMin.class;
    }

    @Override
    public Set<ConstraintViolation> validate(DecimalMin annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, annotation.value(), annotation.message(), (i) -> i >= 0);
    }
}
