package eu.okaeri.validator.provider;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.exception.ValidatorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public interface ValidationProvider<T extends Annotation> {

    Class<T> getAnnotation();

    default boolean shouldValidate(Object target) {
        return this.extractAnnotation(target, this.getAnnotation()) != null;
    }

    Set<ConstraintViolation> validate(T annotation, Object annotationSource, Object value, Class<?> type, Type genericType, String name);

    default Set<ConstraintViolation> validate(Field field, Object value) {
        T annotation = field.getAnnotation(this.getAnnotation());
        return (annotation == null) ? Collections.emptySet() : this.validate(annotation, field, value, field.getType(), field.getGenericType(), field.getName());
    }

    default Set<ConstraintViolation> validate(Parameter parameter, Object value) {
        T annotation = parameter.getAnnotation(this.getAnnotation());
        return (annotation == null) ? Collections.emptySet() : this.validate(annotation, parameter, value, parameter.getType(), parameter.getParameterizedType(), parameter.getName());
    }

    default BigDecimal toBigDecimal(Object value, Class<?> type, Type genericType) {

        if (value == null) {
            return null;
        }

        if (Optional.class.isAssignableFrom(type) && (genericType instanceof ParameterizedType)) {

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?>) {
                type = (Class<?>) rawType;
                value = ((Optional) value).get();
            }
        }

        if (CharSequence.class.isAssignableFrom(type)) {
            return new BigDecimal(String.valueOf(value));
        }

        if (BigDecimal.class.isAssignableFrom(type)) {
            return (BigDecimal) value;
        }

        if (BigInteger.class.isAssignableFrom(type)) {
            return new BigDecimal(((BigInteger) value));
        }

        if ((type == byte.class) || (type == short.class) || (type == int.class) || (type == long.class) || (type == double.class) || (type == float.class)) {
            return new BigDecimal(String.valueOf(value));
        }

        if (Number.class.isAssignableFrom(type)) {
            return BigDecimal.valueOf(((Number) value).longValue());
        }

        return null;
    }

    default boolean isNullOrEmpty(Object value, Class<?> type, Type genericType) {
        return this.extractValue(value, type, genericType) == null;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    default Object extractValue(Object value, Class<?> type, Type genericType) {

        // just null
        if (value == null) {
            return null;
        }

        // check for optional
        if (Optional.class.isAssignableFrom(type)) {
            return ((Optional) value).get();
        }

        // not value
        return value;
    }

    default Class<?> extractType(Class<?> type, Type genericType) {

        if (genericType instanceof ParameterizedType) {

            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type rawType = parameterizedType.getRawType();

            if (rawType instanceof Class<?>) {
                return (Class<?>) rawType;
            }
        }

        return type;
    }

    default <A extends Annotation> A extractAnnotation(Object annotationSource, Class<A> type) {
        if (annotationSource instanceof Field) {
            return ((Field) annotationSource).getAnnotation(type);
        } else if (annotationSource instanceof Parameter) {
            return ((Parameter) annotationSource).getAnnotation(type);
        } else {
            throw new ValidatorException("Unknown annotation source: " + annotationSource);
        }
    }

    default Set<ConstraintViolation> compareBigDecimal(Object value,
                                                       String name,
                                                       Class<?> type,
                                                       Type genericType,
                                                       Object annotationValue,
                                                       String annotationMessage,
                                                       Predicate<Integer> predicate) {

        if (this.isNullOrEmpty(value, type, genericType)) {
            return Collections.emptySet();
        }

        BigDecimal objectValue = this.toBigDecimal(value, type, genericType);
        if (objectValue == null) {
            throw new ValidatorException("@DecimalMin is not applicable for " + type + " [" + name + "]");
        }

        BigDecimal decimal = this.toBigDecimal(annotationValue, annotationValue.getClass(), null);
        if (decimal == null) {
            throw new ValidatorException("@" + this.getClass().getSimpleName() + " value '" + annotationValue + "' is invalid [" + name + "]");
        }

        if (predicate.test(objectValue.compareTo(decimal))) {
            return Collections.emptySet();
        }

        String message = annotationMessage.replace("{value}", String.valueOf(decimal));
        Set<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(name, message));

        return violations;
    }
}
