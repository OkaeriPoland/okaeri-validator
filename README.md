# Okaeri Validator

![License](https://img.shields.io/github/license/OkaeriPoland/okaeri-validator)
![Total lines](https://img.shields.io/tokei/lines/github/OkaeriPoland/okaeri-validator)
![Repo size](https://img.shields.io/github/repo-size/OkaeriPoland/okaeri-validator)
![Contributors](https://img.shields.io/github/contributors/OkaeriPoland/okaeri-validator)
[![Discord](https://img.shields.io/discord/589089838200913930)](https://discord.gg/hASN5eX)

Simple Java Bean field validator inspired by Java EE validation practices. 
The library is partially complaint but not expected to be. The main goal is relatively small source code size (~15kB) as opposed 
to using hibernate-validator with Jakarta EE which ends at 2MB of additional jar size.

## Installation
### Maven
Add repository to the `repositories` section:
```xml
<repository>
    <id>okaeri-repo</id>
    <url>https://storehouse.okaeri.eu/repository/maven-public/</url>
</repository>
```
Add dependency to the `dependencies` section:
```xml
<dependency>
  <groupId>eu.okaeri</groupId>
  <artifactId>okaeri-validator</artifactId>
  <version>1.2.3</version>
</dependency>
```
### Gradle
Add repository to the `repositories` section:
```groovy
maven { url "https://storehouse.okaeri.eu/repository/maven-public/" }
```
Add dependency to the `maven` section:
```groovy
implementation 'eu.okaeri:okaeri-validator:1.2.3'
```

## Example Bean

```java
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
```

## Example Validation

```java
public final class TestValidator {

    public static void main(String[] args) {
        // alternatively skip second argument to allow all fields Nullable by default
        OkaeriValidator validator = OkaeriValidator.of(Bean.class, NullPolicy.NOT_NULL);
        Set<ConstraintViolation> violations = validator.validate(new Bean());
        violations.forEach(violation -> System.out.println(violation.getField() + ": " + violation.getMessage()));
    }
}
```

## Annotations

### Comparison
| Jakarta EE | Okaeri Validator |
|-|-|
| @AssertFalse | @Pattern(value = "false", useToString=true)* |
| @AssertTrue | @Pattern(value = "true", useToString=true)* |
| @DecimalMax | @DecimalMax |
| @DecimalMin | @DecimalMin |
| @Digits | @Pattern(value = "custom regex", useToString=true)* |
| @Email | @Pattern("custom regex") |
| @Future | None |
| @FutureOrPresent | None |
| @Min(x) | @Min(x) |
| @Min(x) | @Max(x) |
| @Negative | @Max(-1)** |
| @NegativeOrZero | @Max(0) |
| @NotBlank | @NotBlank |
| @NotEmpty | @Size(min = 1) |
| @NotNull | @NotNull |
| @Null | None |
| @Past | None |
| @Pattern(regexp = value) | @Pattern(value) |
| @Positive | @Min(1)** |
| @PositiveOrZero | @Min(0) |
| @Size(min=x, max=y) | @Size(min=x, max=y) |

*using `useToString=true` may yield unexpected results, eg. `CustomObject#toString()` may return "false" but object value in fact is not boolean `false`.

**currently support for checking if floating point value is positive/negative is limited to values that include zero. `@DecimalMin` and `@DecimalMax` may be used as hacky fallback (`0.000..001`).

### Additional
| Annotation | Description |
|-|-|
| @Nullable | Allows value to be null when `NullPolicy.NOT_NULL` is used |
