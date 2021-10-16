package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Negative;

import java.lang.reflect.Type;
import java.util.Set;

public class NegativeProvider implements ValidationProvider<Negative> {

    @Override
    public Class<Negative> getAnnotation() {
        return Negative.class;
    }

    @Override
    public Set<ConstraintViolation> validate(Negative annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i < 0);
    }
}
