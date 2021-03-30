package eu.okaeri.validatortest;

import eu.okaeri.validator.annotation.*;

import java.util.Collections;
import java.util.List;

public class Bean {

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
}
