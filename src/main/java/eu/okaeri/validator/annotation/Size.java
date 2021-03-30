package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element size must be between the specified boundaries (included).
 *
 * Supported types:
 * - CharSequence (length of character sequence is evaluated)
 * - Collection (collection size is evaluated)
 * - Map (map size is evaluated)
 * - Array (array length is evaluated)
 *
 * `null` elements are considered valid.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "size must be between {min} and {max}";
}
