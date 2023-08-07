package eu.okaeri.validator;


import eu.okaeri.validator.policy.NullPolicy;
import eu.okaeri.validator.provider.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
        this.register(new ValidProvider(this));
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
        Set<ConstraintViolation> violations = this.validationProviders.values().stream()
            .filter(provider -> provider.shouldValidate(field))
            .flatMap(provider -> provider.validate(field, fieldValue).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
        violations.addAll(this.validateCascading(field.getName(), field.getAnnotatedType(), fieldValue));
        return violations;
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
        Set<ConstraintViolation> violations = this.validationProviders.values().stream()
            .filter(provider -> provider.shouldValidate(parameter))
            .flatMap(provider -> provider.validate(parameter, value).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
        violations.addAll(this.validateCascading(parameter.getName(), parameter.getAnnotatedType(), value));
        return violations;
    }

    private Set<ConstraintViolation> validateCascading(@NonNull String fieldName, @NonNull AnnotatedType annotatedObjectType, Object object) {
        if (!(annotatedObjectType instanceof AnnotatedParameterizedType)) {
            return new LinkedHashSet<>();
        }
        AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) annotatedObjectType;

        if (!(parameterizedType.getType() instanceof ParameterizedType)) {
            return new LinkedHashSet<>();
        }
        Class<?> objectType = (Class<?>) ((ParameterizedType) parameterizedType.getType()).getRawType();

        AnnotatedType[] actualTypes = parameterizedType.getAnnotatedActualTypeArguments();
        if (Collection.class.isAssignableFrom(objectType)) {
            return this.validateCollection(fieldName, actualTypes[0], (Collection<?>) object);
        } else if (Map.class.isAssignableFrom(objectType)) {
            return this.validateMap(fieldName, actualTypes[0], actualTypes[1], (Map<?, ?>) object);
        }
        return new LinkedHashSet<>();
    }

    @Override
    public Set<ConstraintViolation> validateCollection(@NonNull String fieldName, @NonNull AnnotatedType annotatedValueType, @NonNull Collection<?> collection) {
        Type valueType = annotatedValueType.getType();
        if (!(valueType instanceof Class<?>)) {
            return new LinkedHashSet<>();
        }

        AtomicInteger index = new AtomicInteger(0);
        return collection.stream()
            .flatMap(element -> this.validateElement(fieldName, (Class<?>) valueType, annotatedValueType, element).stream()
                .peek(elementViolation -> elementViolation.setPath("[" + index.getAndIncrement() + "]")))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateMap(@NonNull String fieldName, @NonNull AnnotatedType annotatedKeyType, @NonNull AnnotatedType annotatedValueType, @NonNull Map<?, ?> map) {
        Type keyType = annotatedKeyType.getType();
        Type valueType = annotatedValueType.getType();
        if (!(keyType instanceof Class<?>) || !(valueType instanceof Class<?>)) {
            return new LinkedHashSet<>();
        }

        return map.entrySet().stream()
            .flatMap(entry -> Stream.concat(
                this.validateElement(fieldName, (Class<?>) keyType, annotatedKeyType, entry.getKey()).stream()
                    .peek(elementViolation -> elementViolation.setPath("[" + entry.getKey() + "]")),
                this.validateElement(fieldName, (Class<?>) valueType, annotatedValueType, entry.getValue()).stream()
                    .peek(elementViolation -> elementViolation.setPath("[" + entry.getKey() + "].value"))
            ))
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Set<ConstraintViolation> validateElement(@NonNull String fieldName, @NonNull Class<?> type, @NonNull AnnotatedType annotatedType, @NonNull Object element) {
        return this.validationProviders.values().stream()
            .filter(provider -> provider.shouldValidate(annotatedType))
            .flatMap(provider -> provider.validate(annotatedType, element, type, type, fieldName).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
