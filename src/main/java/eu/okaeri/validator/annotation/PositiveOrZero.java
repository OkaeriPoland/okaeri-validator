package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element must be a positive number or 0.
 *
 * Supported types:
 * - BigDecimal
 * - BigInteger
 * - CharSequence
 * - primitives: byte, short, int, long, double*, float*
 * - Number: (eg. Byte, Short, Integer, Long, Double*, Float*)
 * - Duration
 *
 * *Floating point values may be prone to the rounding errors.
 *
 * `null` elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PositiveOrZero {
    String message() default "value must be positive or zero";
}
