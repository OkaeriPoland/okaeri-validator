package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.DecimalMax;

import java.lang.reflect.Type;
import java.util.Set;

public class DecimalMaxProvider implements ValidationProvider<DecimalMax> {

    @Override
    public Class<DecimalMax> getAnnotation() {
        return DecimalMax.class;
    }

    @Override
    public Set<ConstraintViolation> validate(DecimalMax annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, annotation.value(), annotation.message(), (i) -> i <= 0);
    }
}
