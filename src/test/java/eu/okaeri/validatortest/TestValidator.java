package eu.okaeri.validatortest;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.OkaeriValidator;
import eu.okaeri.validator.policy.NullPolicy;

import java.util.Set;

public final class TestValidator {

    public static void main(String[] args) {
        OkaeriValidator validator = OkaeriValidator.of(Bean.class, NullPolicy.NOT_NULL);
        Set<ConstraintViolation> violations = validator.validate(new Bean());
        violations.forEach(violation -> System.out.println(violation.getField() + ": " + violation.getMessage()));
    }
}
