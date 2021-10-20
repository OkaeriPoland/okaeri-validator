package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;
import eu.okaeri.validator.exception.ValidatorException;
import eu.okaeri.validator.policy.NullPolicy;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
public class NotNullProvider implements ValidationProvider<NotNull> {

    private final NullPolicy nullPolicy;

    @Override
    public Class<NotNull> getAnnotation() {
        return NotNull.class;
    }

    @Override
    public boolean shouldValidate(Object target) {
        return true;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull NotNull notNull, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {

        Boolean canBeNull = null;
        Nullable nullable = this.extractAnnotation(annotationSource, Nullable.class);

        // no NotNull annotation
        if (notNull == null) {
            if (this.nullPolicy == NullPolicy.NOT_NULL) {
                //noinspection RedundantIfStatement
                if (nullable == null) {
                    canBeNull = false;
                } else {
                    canBeNull = true;
                }
            } else if (this.nullPolicy == NullPolicy.NULLABLE) {
                canBeNull = true;
            }
        }
        // NotNull annotation
        else {
            canBeNull = false;
        }

        // unexpected case
        if (canBeNull == null) {
            throw new ValidatorException("unexpected case for @NotNull @Nullable and NullPolicy occured");
        }

        // can be null or is not null, pass
        if (canBeNull || (value != null)) {
            return Collections.emptySet();
        }

        // null
        String message = (notNull != null)
                ? notNull.message()
                : "value cannot be null";

        Set<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(name, message, this.getType()));

        return violations;
    }
}
