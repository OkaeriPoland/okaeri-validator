package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NegativeOrZero;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

public class NegativeOrZeroProvider implements ValidationProvider<NegativeOrZero> {

    @Override
    public Class<NegativeOrZero> getAnnotation() {
        return NegativeOrZero.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull NegativeOrZero annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {
        return this.compareBigDecimal(value, name, type, genericType, 0, annotation.message(), (i) -> i <= 0);
    }
}
