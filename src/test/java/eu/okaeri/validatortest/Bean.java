package eu.okaeri.validatortest;

import eu.okaeri.validator.annotation.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.plaf.ListUI;

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

    private List<@Size(min = 4) String> listWithTooShortString = Arrays.asList("a", "bb", "ccc");

    private Map<@Size(min = 1, max = 16) String, @Positive Integer> mapWithTooLongAndNegativeValue = Collections.singletonMap("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", -1);

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

    @Valid
    private InnerBean innerBean = new InnerBean();

    private static class InnerBean {

        @NotNull
        private String nullValue = null;

        @Negative
        private int positiveInt = 1;

        @Size(min = 10)
        private List<@PositiveOrZero Integer> allNegativeAndTooShortList = Arrays.asList(-1, -2, -3);

        @Valid
        private InnerInnerBean innerInnerBean = new InnerInnerBean();

        private static class InnerInnerBean {

            @Negative
            private Duration positiveDuration = Duration.ofSeconds(5);

            @Size(min = 10)
            private Set<@PositiveOrZero Integer> allNegativeSet = Collections.singleton(-1);

        }

    }
}
