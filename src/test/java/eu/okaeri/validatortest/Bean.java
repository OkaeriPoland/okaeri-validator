package eu.okaeri.validatortest;

import eu.okaeri.validator.annotation.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

public class Bean {

    @Positive
    @PositiveOrZero
    @Negative
    @NegativeOrZero
    @Min(1)
    private int zero = 0;

    @Max(10)
    private long ten = 11;

    @Size(min = 1)
    private List<String> emptyListOfStrings = Collections.emptyList();

    @Size(min = 10)
    private String emptyString = "";

    @Size(max = 2)
    private String tooLongString = "aaaaaaaaa";

    @Pattern("[a-z]+")
    private String bigLettersOnly = "ABCD";

    @Size(min = 1)
    private String nullString = null;

    @NotBlank
    private String blank = "";

    @NotBlank
    private String blank2 = "  ";

    @NotBlank
    private String notReallyBlank = "xdd";

    @DecimalMin("5.99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999")
    private int six = 6;

    @Negative
    private Duration duration = Duration.ZERO;

    @Positive
    private Duration positiveDuration = Duration.ofSeconds(5);

    @Positive
    private int positiveInt = 1;
}
