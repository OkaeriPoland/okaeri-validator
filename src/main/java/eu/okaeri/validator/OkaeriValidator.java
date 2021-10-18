package eu.okaeri.validator;


import eu.okaeri.validator.policy.NullPolicy;
import eu.okaeri.validator.provider.*;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OkaeriValidator implements Validator {

    private final NullPolicy nullPolicy;
    private final Map<Class<? extends Annotation>, ValidationProvider<?>> validationProviders = new ConcurrentHashMap<>();

    public static OkaeriValidator of() {
        return of(NullPolicy.NULLABLE);
    }

    public static OkaeriValidator of(NullPolicy nullPolicy) {
        return new OkaeriValidator(nullPolicy);
    }

    protected OkaeriValidator(NullPolicy nullPolicy) {
        this.nullPolicy = nullPolicy;
        this.register(new DecimalMaxProvider());
        this.register(new DecimalMinProvider());
        this.register(new MaxProvider());
        this.register(new MinProvider());
        this.register(new NegativeOrZeroProvider());
        this.register(new NegativeProvider());
        this.register(new NotBlankProvider());
        this.register(new NotNullProvider(this.nullPolicy));
        this.register(new PatternProvider());
        this.register(new PositiveOrZeroProvider());
        this.register(new PositiveProvider());
        this.register(new SizeProvider());
    }

    @Override
    public <T extends Annotation> OkaeriValidator register(@NonNull ValidationProvider<T> validationProvider) {
        this.validationProviders.put(validationProvider.getAnnotation(), validationProvider);
        return this;
    }

    @Override
    public Set<ConstraintViolation> validate(@NonNull Object object) {
        return Arrays.stream(object.getClass().getFields())
                .flatMap(field -> this.validatePropertyValue(object.getClass(), field.getName(), this.fieldValueOrNull(object, field)).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateProperty(@NonNull Object object, @NonNull String fieldName) {
        return this.validatePropertyValue(object.getClass(), fieldName, this.fieldValueOrNull(object, fieldName));
    }

    @Override
    public Set<ConstraintViolation> validatePropertyValue(@NonNull Class<?> type, @NonNull String fieldName, Object fieldValue) {
        return null; // TODO
    }

    @Override
    public Set<ConstraintViolation> validateParameters(@NonNull Parameter[] parameters, @NonNull Object[] values) {
        return null; // TODO
    }

    private Object fieldValueOrNull(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
            return null;
        }
    }

    private Object fieldValueOrNull(Object object, Field field) {
        try {
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }
}
