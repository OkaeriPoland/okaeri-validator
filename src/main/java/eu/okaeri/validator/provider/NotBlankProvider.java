package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NotBlank;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;
import eu.okaeri.validator.exception.ValidatorException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class NotBlankProvider implements ValidationProvider<NotBlank> {

    @Override
    public Class<NotBlank> getAnnotation() {
        return NotBlank.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull NotBlank annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {

        type = this.extractType(type, genericType);
        value = this.extractValue(value, type, genericType);

        if (value == null) {
            Set<ConstraintViolation> violations = new LinkedHashSet<>();
            violations.add(new ConstraintViolation(name, annotation.message(), this.getType()));
            return violations;
        }

        if (!CharSequence.class.isAssignableFrom(type)) {
            throw new ValidatorException("@NotBlank is not applicable for " + type + " (to validate #toString() output set useToString=true) [" + name + "]");
        }

        CharSequence sequence = (CharSequence) value;
        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            return Collections.emptySet();
        }

        Set<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(name, annotation.message(), this.getType()));
        return violations;
    }
}
