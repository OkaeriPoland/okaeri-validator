package eu.okaeri.validatortest;

import eu.okaeri.validator.ConstraintViolation;
import eu.okaeri.validator.OkaeriValidator;
import eu.okaeri.validator.policy.NullPolicy;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestValidator {

    @Test
    public void test_validator_1() {
        OkaeriValidator validator = OkaeriValidator.of(Bean.class, NullPolicy.NOT_NULL);
        Set<ConstraintViolation> violations = validator.validate(new Bean());
        violations.forEach(violation -> System.out.println(violation.getField() + ": " + violation.getMessage()));
        assertEquals(9, violations.size());
    }

    @Test
    public void test_expression_concept_1() {
        this.test("x > 20 && x < 50", 10);
        this.test("x > 20 && x < 51", 25);
        this.test("x > 20 && x < 52", 50);
        this.test("x > 20 && x < 50", 50);
        this.test("x > 20 && x < 50", 50);
        this.test("x > 20 && x < 50", 50);
        this.test("x > 20 && x < 50", 50);
    }

    @SneakyThrows
    public void test(String expression, double value) {
        long start = System.nanoTime();
        BooleanValidatorExpression compiled = this.compile(expression);
        System.out.println(compiled.getClass().getCanonicalName());
        boolean aaa = compiled.eval(value);
        long took = System.nanoTime() - start;
        System.out.println(took + " ns " + TimeUnit.NANOSECONDS.toMillis(took) + " ms");
        System.out.println(aaa);
        System.out.println();
    }

    public BooleanValidatorExpression compile(String expression) throws Exception {
        return Expression.of(expression)
                .input(double.class)
                .result(boolean.class)
                .compile(BooleanValidatorExpression.class);
    }
}
