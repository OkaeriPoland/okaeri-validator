package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a number whose value must be higher or equal to the specified minimum.
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
public @interface Min {
    long value();
    String message() default "value must be equal to or greater than {value}";
}
