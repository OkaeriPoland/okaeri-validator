package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be lower or equal to the specified maximum.
 *
 * Supported types:
 * - BigDecimal
 * - BigInteger
 * - Byte (+ byte)
 * - Short (+ short)
 * - Integer (+ int)
 * - Long (+ long)
 *
 *  `null` elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Max {
    long value();
    String message() default "value must be equal to or smaller than {value}";
}
