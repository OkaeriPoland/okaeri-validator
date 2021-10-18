package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;
import eu.okaeri.validator.annotation.PositiveOrZero;

import java.lang.reflect.Type;
import java.util.Set;

public class PositiveOrZeroProvider implements ValidationProvider<PositiveOrZero> {

    @Override
    public Class<PositiveOrZero> getAnnotation() {
        return PositiveOrZero.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull PositiveOrZero annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i >= 0);
    }
}
