package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;
import eu.okaeri.validator.annotation.Pattern;
import eu.okaeri.validator.exception.ValidatorException;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PatternProvider implements ValidationProvider<Pattern> {

    @Override
    public Class<Pattern> getAnnotation() {
        return Pattern.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull Pattern annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {

        type = this.extractType(type, genericType);
        value = this.extractValue(value, type, genericType);

        if (value == null) {
            return Collections.emptySet();
        }

        CharSequence objectValue = null;
        if (CharSequence.class.isAssignableFrom(type)) {
            objectValue = (CharSequence) value;
        } else if (annotation.useToString()) {
            objectValue = String.valueOf(value);
        }

        if (objectValue == null) {
            throw new ValidatorException("@Pattern is not applicable for " + type + " (to validate #toString() output set useToString=true) [" + name + "]");
        }

        String patternStr = annotation.value();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr);

        if (pattern.matcher(objectValue).matches()) {
            return Collections.emptySet();
        }

        String message = annotation.message().replace("{value}", patternStr);
        Set<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(name, message));

        return violations;
    }
}
