package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.annotation.Size;
import eu.okaeri.validator.exception.ValidatorException;
import lombok.SneakyThrows;

import java.lang.reflect.Type;
import java.util.*;

public class SizeProvider implements ValidationProvider<Size> {

    @Override
    public Class<Size> getAnnotation() {
        return Size.class;
    }

    @Override
    @SneakyThrows
    public Set<ConstraintViolation> validate(Size annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name) {

        type = this.extractType(type, genericType);
        value = this.extractValue(value, type, genericType);

        if (value == null) {
            return Collections.emptySet();
        }

        Integer size = null;
        if (Collection.class.isAssignableFrom(type)) {
            size = ((Collection) value).size();
        } else if (Map.class.isAssignableFrom(type)) {
            size = ((Map) value).size();
        } else if (CharSequence.class.isAssignableFrom(type)) {
            size = ((CharSequence) value).length();
        } else if (type.isArray()) {
            size = ((Object[]) value).length;
        }

        if (size == null) {
            throw new ValidatorException("@Size is not applicable for " + type + " [" + name + "]");
        }

        int min = annotation.min();
        int max = annotation.max();

        if ((size >= min) && (size <= max)) {
            return Collections.emptySet();
        }

        String message = annotation.message()
                .replace("{min}", String.valueOf(min))
                .replace("{max}", String.valueOf(max));

        Set<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(name, message));

        return violations;
    }
}
