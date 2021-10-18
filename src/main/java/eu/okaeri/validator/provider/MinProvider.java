package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Min;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class MinProvider implements ValidationProvider<Min> {

    @Override
    public Class<Min> getAnnotation() {
        return Min.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull Min annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {
        return this.compareBigDecimal(value, name, type, genericType, annotation.value(), annotation.message(), (i) -> i >= 0);
    }
}
