package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NegativeOrZero;

import java.lang.reflect.Type;
import java.util.Set;

public class NegativeOrZeroProvider implements ValidationProvider<NegativeOrZero> {

    @Override
    public Class<NegativeOrZero> getAnnotation() {
        return NegativeOrZero.class;
    }

    @Override
    public Set<ConstraintViolation> validate(NegativeOrZero annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i <= 0);
    }
}
