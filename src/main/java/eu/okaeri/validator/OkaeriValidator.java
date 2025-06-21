package eu.okaeri.validator;


import eu.okaeri.validator.policy.NullPolicy;
import eu.okaeri.validator.provider.*;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OkaeriValidator implements Validator {

    private final Map<Class<? extends Annotation>, ValidationProvider<?>> validationProviders = new ConcurrentHashMap<>();

    protected OkaeriValidator(NullPolicy nullPolicy) {
        this.register(new DecimalMaxProvider());
        this.register(new DecimalMinProvider());
        this.register(new MaxProvider());
        this.register(new MinProvider());
        this.register(new NegativeOrZeroProvider());
        this.register(new NegativeProvider());
        this.register(new NotBlankProvider());
        this.register(new NotNullProvider(nullPolicy));
        this.register(new PatternProvider());
        this.register(new PositiveOrZeroProvider());
        this.register(new PositiveProvider());
        this.register(new SizeProvider());
    }

    public static OkaeriValidator of() {
        return of(NullPolicy.NULLABLE);
    }

    public static OkaeriValidator of(NullPolicy nullPolicy) {
        return new OkaeriValidator(nullPolicy);
    }

    @Override
    public <T extends Annotation> OkaeriValidator register(@NonNull ValidationProvider<T> validationProvider) {
        this.validationProviders.put(validationProvider.getAnnotation(), validationProvider);
        return this;
    }

    @Override
    public Map<Class<? extends Annotation>, ValidationProvider<?>> getRegisteredProviders() {
        return Collections.unmodifiableMap(this.validationProviders);
    }

    @Override
    public Set<ConstraintViolation> validate(@NonNull Object object) {
        return Arrays.stream(object.getClass().getDeclaredFields())
            .flatMap(field -> this.validateProperty(object, field).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    @SneakyThrows
    public Set<ConstraintViolation> validateProperty(@NonNull Object object, @NonNull String fieldName) {
        return this.validateProperty(object, object.getClass().getDeclaredField(fieldName));
    }

    @Override
    @SneakyThrows
    public Set<ConstraintViolation> validateProperty(@NonNull Object object, @NonNull Field field) {
        field.setAccessible(true);
        Object value = field.get(object);
        return this.validatePropertyValue(object.getClass(), field, value);
    }

    @Override
    @SneakyThrows
    public Set<ConstraintViolation> validatePropertyValue(@NonNull Class<?> type, @NonNull String fieldName, Object fieldValue) {
        return this.validatePropertyValue(type, type.getDeclaredField(fieldName), fieldValue);
    }

    @Override
    public Set<ConstraintViolation> validatePropertyValue(@NonNull Class<?> type, @NonNull Field field, Object fieldValue) {
        return this.validationProviders.values().stream()
            .filter(provider -> provider.shouldValidate(field))
            .flatMap(provider -> provider.validate(field, fieldValue).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateParameters(@NonNull Parameter[] parameters, @NonNull Object[] values) {
        return IntStream
            .range(0, parameters.length)
            .mapToObj(i -> this.validateParameter(parameters[i], values[i]))
            .flatMap(Collection::stream)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateParameter(@NonNull Parameter parameter, Object value) {
        return this.validationProviders.values().stream()
            .filter(provider -> provider.shouldValidate(parameter))
            .flatMap(provider -> provider.validate(parameter, value).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
