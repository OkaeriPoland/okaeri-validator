package eu.okaeri.validator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element match the specified regular expression. The regular expression follows the Java regular expression conventions see {@link java.util.regex.Pattern}.
 * It is possible to check for element's {@link Object#toString()} value using {@link Pattern#useToString()} set to true.
 *
 * Supported types:
 *  - CharSequence
 *
 * `null` elements are considered valid.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern {
    String value();
    boolean useToString() default false;
    String message() default "value must match {value}";
}
