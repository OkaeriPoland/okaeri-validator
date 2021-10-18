package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a negative number or 0.
 * <p>
 * Supported types:
 * - BigDecimal
 * - BigInteger
 * - CharSequence
 * - primitives: byte, short, int, long, double*, float*
 * - Number: (eg. Byte, Short, Integer, Long, Double*, Float*)
 * - Duration
 * <p>
 * *Floating point values may be prone to the rounding errors.
 * <p>
 * `null` elements are considered valid.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NegativeOrZero {
    String message() default "value must be negative or zero";
}
