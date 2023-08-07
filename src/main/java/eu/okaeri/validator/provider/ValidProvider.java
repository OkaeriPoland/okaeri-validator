package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.Validator;
import eu.okaeri.validator.annotation.NotNull;
import eu.okaeri.validator.annotation.Nullable;
import eu.okaeri.validator.annotation.Valid;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ValidProvider implements ValidationProvider<Valid> {

    private final Validator validator;

    @Override
    public Class<Valid> getAnnotation() {
        return Valid.class;
    }

    @Override
    public Set<ConstraintViolation> validate(@NotNull Valid annotation, @Nullable Object annotationSource, @Nullable Object value, @NotNull Class<?> type, @NotNull Type genericType, @NotNull String name) {
        return this.validator.validate(value).stream()
            .peek(violation -> violation.setCascading(true))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
