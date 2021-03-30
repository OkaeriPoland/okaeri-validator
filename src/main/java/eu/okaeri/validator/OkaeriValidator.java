package eu.okaeri.validator;


import eu.okaeri.validator.annotation.*;
import eu.okaeri.validator.exception.ValidatorException;
import eu.okaeri.validator.policy.DefaultNullPolicy;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class OkaeriValidator implements Validator {

    private Class<?> clazz;
    private List<Field> fields;
    private DefaultNullPolicy nullPolicy;

    private Optional<Field> fieldByName(String name) {
        return this.fields.stream()
                .filter(field -> name.equals(field.getName()))
                .findAny();
    }

    private Object fieldValueOrNull(Object object, String name) {
        return this.fieldByName(name)
                .map(field -> {
                    try {
                        boolean accessible = field.isAccessible();
                        field.setAccessible(true);
                        Object value = field.get(object);
                        field.setAccessible(accessible);
                        return value;
                    } catch (IllegalAccessException ignored) {
                        return null;
                    }
                })
                .orElse(null);
    }

    private void validateIfApplicable(Object bean) {
        if (this.clazz.isAssignableFrom(bean.getClass())) {
            return;
        }
        throw new ValidatorException("cannot validate " + bean.getClass() + " with the validator created for " + this.clazz);
    }

    public static OkaeriValidator of(Class<?> clazz) {
        return of(clazz, DefaultNullPolicy.NULLABLE);
    }

    public static OkaeriValidator of(Class<?> clazz, DefaultNullPolicy nullPolicy) {
        OkaeriValidator validator = new OkaeriValidator();
        validator.clazz = clazz;
        validator.fields = Arrays.asList(clazz.getDeclaredFields());
        validator.nullPolicy = nullPolicy;
        return validator;
    }

    @Override
    public Set<ConstraintViolation> validate(Object object) {
        this.validateIfApplicable(object);
        return this.fields.stream()
                .flatMap(field -> this.validateProperty(object, field.getName()).stream())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public Set<ConstraintViolation> validateProperty(Object object, String fieldName) {
        this.validateIfApplicable(object);
        return this.validateValue(fieldName, this.fieldValueOrNull(object, fieldName));
    }

    @Override
    public Set<ConstraintViolation> validateValue(String fieldName, Object fieldValue) {

        Optional<Field> fieldOptional = this.fieldByName(fieldName);
        if (!fieldOptional.isPresent()) {
            throw new ValidatorException("no field with name " + fieldName + " available in " + this.clazz);
        }

        Field field = fieldOptional.get();
        Class<?> fieldType = field.getType();
        Set<ConstraintViolation> violations = new LinkedHashSet<>();

        violations.addAll(this.validateNotNull(field, fieldValue, fieldType));
        violations.addAll(this.validateSize(field, fieldValue, fieldType));
        violations.addAll(this.validateMin(field, fieldValue, fieldType));
        violations.addAll(this.validateMax(field, fieldValue, fieldType));
        violations.addAll(this.validatePattern(field, fieldValue, fieldType));
        violations.addAll(this.validateNotBlank(field, fieldValue, fieldType));

        return violations;
    }

    @SneakyThrows
    protected Set<ConstraintViolation> validateNotNull(Field field, Object fieldValue, Class<?> fieldType) {

        Boolean canBeNull = null;
        NotNull notNullAnnotation = field.getAnnotation(NotNull.class);
        Nullable nullableAnnotation = field.getAnnotation(Nullable.class);

        // no NotNull annotation
        if (notNullAnnotation == null) {
            if (this.nullPolicy == DefaultNullPolicy.NOT_NULL) {
                //noinspection RedundantIfStatement
                if (nullableAnnotation == null) {
                    canBeNull = false;
                } else {
                    canBeNull = true;
                }
            }
            else if (this.nullPolicy == DefaultNullPolicy.NULLABLE) {
                canBeNull = true;
            }
        }
        // NotNull annotation
        else {
            canBeNull = false;
        }

        // unexpected case
        if (canBeNull == null) {
            throw new ValidatorException("unexpected case for @NotNull @Nullable and DefaultNullPolicy occured");
        }

        // can be null or is not null, pass
        if (canBeNull || (fieldValue != null)) {
            return Collections.emptySet();
        }

        // null
        String message = (notNullAnnotation != null)
                ? notNullAnnotation.message()
                : "value cannot be null";

        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), message));

        return violations;
    }

    @SneakyThrows
    protected Set<ConstraintViolation> validateSize(Field field, Object fieldValue, Class<?> fieldType) {

        if (fieldValue == null) {
            return Collections.emptySet();
        }

        Size sizeAnnotation = field.getAnnotation(Size.class);
        if (sizeAnnotation == null) {
            return Collections.emptySet();
        }

        Integer size = null;
        if (Collection.class.isAssignableFrom(fieldType)) {
            size = ((Collection) fieldValue).size();
        } else if (Map.class.isAssignableFrom(fieldType)) {
            size = ((Map) fieldValue).size();
        } else if (CharSequence.class.isAssignableFrom(fieldType)) {
            size = ((CharSequence) fieldValue).length();
        } else if (fieldType.isArray()) {
            Field lengthField = fieldType.getField("length");
            size = (Integer) lengthField.get(fieldValue);
        }

        if (size == null) {
            throw new ValidatorException("@Size is not applicable for " + fieldType + " [" + field.getName() + "]");
        }

        int min = sizeAnnotation.min();
        int max = sizeAnnotation.max();

        if ((size >= min) && (size <= max)) {
            return Collections.emptySet();
        }

        String message = sizeAnnotation.message()
                .replace("{min}", String.valueOf(min))
                .replace("{max}", String.valueOf(max));

        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), message));

        return violations;
    }

    protected Set<ConstraintViolation> validateMin(Field field, Object fieldValue, Class<?> fieldType) {

        if (fieldValue == null) {
            return Collections.emptySet();
        }

        Min minAnnotation = field.getAnnotation(Min.class);
        if (minAnnotation == null) {
            return Collections.emptySet();
        }

        BigDecimal objectValue = null;
        if (BigDecimal.class.isAssignableFrom(fieldType)) {
            objectValue = (BigDecimal) fieldValue;
        } else if (BigInteger.class.isAssignableFrom(fieldType)) {
            objectValue = new BigDecimal(((BigInteger) fieldValue));
        } else if (Byte.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
            objectValue = BigDecimal.valueOf(((Number) fieldValue).longValue());
        } else if ((fieldType == byte.class) || (fieldType == short.class) || (fieldType == int.class) || (fieldType == long.class)) {
            objectValue = BigDecimal.valueOf(Long.parseLong(fieldValue.toString()));
        }

        if (objectValue == null) {
            throw new ValidatorException("@Min is not applicable for " + fieldType + " [" + field.getName() + "]");
        }

        BigDecimal value = BigDecimal.valueOf(minAnnotation.value());
        if (objectValue.compareTo(value) >= 0) {
            return Collections.emptySet();
        }

        String message = minAnnotation.message()
                .replace("{value}", String.valueOf(value));

        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), message));

        return violations;
    }

    protected Set<ConstraintViolation> validateMax(Field field, Object fieldValue, Class<?> fieldType) {

        if (fieldValue == null) {
            return Collections.emptySet();
        }

        Max maxAnnotation = field.getAnnotation(Max.class);
        if (maxAnnotation == null) {
            return Collections.emptySet();
        }

        BigDecimal objectValue = null;
        if (BigDecimal.class.isAssignableFrom(fieldType)) {
            objectValue = (BigDecimal) fieldValue;
        } else if (BigInteger.class.isAssignableFrom(fieldType)) {
            objectValue = new BigDecimal(((BigInteger) fieldValue));
        } else if (Byte.class.isAssignableFrom(fieldType) || Short.class.isAssignableFrom(fieldType) || Integer.class.isAssignableFrom(fieldType) || long.class.isAssignableFrom(fieldType)) {
            objectValue = BigDecimal.valueOf(((Number) fieldValue).longValue());
        } else if ((fieldType == byte.class) || (fieldType == short.class) || (fieldType == int.class) || (fieldType == long.class)) {
            objectValue = BigDecimal.valueOf(Long.parseLong(fieldValue.toString()));
        }

        if (objectValue == null) {
            throw new ValidatorException("@Max is not applicable for " + fieldType + " [" + field.getName() + "]");
        }

        BigDecimal value = BigDecimal.valueOf(maxAnnotation.value());
        if (objectValue.compareTo(value) <= 0) {
            return Collections.emptySet();
        }

        String message = maxAnnotation.message()
                .replace("{value}", String.valueOf(value));

        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), message));

        return violations;
    }

    protected Set<ConstraintViolation> validatePattern(Field field, Object fieldValue, Class<?> fieldType) {

        if (fieldValue == null) {
            return Collections.emptySet();
        }

        Pattern patternAnnotation = field.getAnnotation(Pattern.class);
        if (patternAnnotation == null) {
            return Collections.emptySet();
        }

        CharSequence objectValue = null;
        if (CharSequence.class.isAssignableFrom(fieldType)) {
            objectValue = (CharSequence) fieldValue;
        } else if (patternAnnotation.useToString()) {
            objectValue = fieldValue.toString();
        }

        if (objectValue == null) {
            throw new ValidatorException("@Pattern is not applicable for " + fieldType + " (to validate #toString() output set useToString=true) [" + field.getName() + "]");
        }

        String patternStr = patternAnnotation.value();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(patternStr);

        if (pattern.matcher(objectValue).matches()) {
            return Collections.emptySet();
        }

        String message = patternAnnotation.message().replace("{value}", patternStr);
        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), message));

        return violations;
    }

    protected Set<ConstraintViolation> validateNotBlank(Field field, Object fieldValue, Class<?> fieldType) {

        NotBlank notBlankAnnotation = field.getAnnotation(NotBlank.class);
        if (notBlankAnnotation == null) {
            return Collections.emptySet();
        }

        if (fieldValue == null) {
            HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
            violations.add(new ConstraintViolation(field.getName(), notBlankAnnotation.message()));
            return violations;
        }

        if (!CharSequence.class.isAssignableFrom(fieldType)) {
            throw new ValidatorException("@NotBlank is not applicable for " + fieldType + " (to validate #toString() output set useToString=true) [" + field.getName() + "]");
        }

        CharSequence sequence = (CharSequence) fieldValue;
        for (int i = 0; i < sequence.length(); i++) {
            char c = sequence.charAt(i);
            if (Character.isWhitespace(c)) {
                continue;
            }
            return Collections.emptySet();
        }

        HashSet<ConstraintViolation> violations = new LinkedHashSet<>();
        violations.add(new ConstraintViolation(field.getName(), notBlankAnnotation.message()));
        return violations;
    }

    protected OkaeriValidator() {
    }
}
