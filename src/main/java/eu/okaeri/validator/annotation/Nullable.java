package eu.okaeri.validator.annotation;

import eu.okaeri.validator.policy.NullPolicy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated element is allowed to be null (used with {@link NullPolicy#NOT_NULL}). Accepts any type.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {
}
