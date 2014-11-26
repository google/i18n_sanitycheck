i18n-sanitychecker is a library that allows developer to write unit-test for
locale-sensitive functions without relying on "golden data".

i18n-sanitychecker provides only one public method to match actual strings
against expected patterns. Patterns may contain placeholders for date, time,
numbers, lists. The method behaves similar to other assert methods of JUnit.

#USAGE

SanityCheckProcessor.assertI18nSanityCheck(pattern, actual_string, locale);


#EXAMPLES

1. Check that DOM element contains date and time in any format for Japanese:
```java
SanityCheckProcessor.assertI18nSanityCheck("{datetime}",
    dom.getSingleNodeText("//abbr[@class='timestamp']"), ULocale.JAPAN);
```
2. Check that string contains date in any format suitable for German:
```java
SanityCheckProcessor.assertI18nSanityCheck(
    "Todays is {date}", formatted_string, Locale.GERMAN);
```
3. Check that value is a date formatted according the pattern:
```java
SanityCheckProcessor.assertI18nSanityCheck(
    "{datetime:{pattern:'dd-MM-yy'}}", value, currentLocale)
```

#PLACEHOLDERS

* {ignore}  Ignores everything within this placeholder.
* {time}  Checks if formatted time satisfies ICU standards.
          Optional parameters:
           - pattern - date time pattern in ICU format
* {date}  Checks if formatted date satisfies the ICU standards.
          Optional parameters:
            - pattern - date time pattern in ICU format
            - skeleton - date time skeleton that would be used for
                         pattern generation. See ICU documentation for
                         details.
            - value - the numeric value of date time you are expecting.
                      Number of milliseconds since Jan 1, 1970.
* {datetime}  Merges 2 and 3 together. Has same optional parameters.
* {number}  Checks if formatted number satisfies the ICU standards.
            Could be used to check currency, rational, scientific, numbers
            and spelled numbers.
            Optional parameters:
              - value - expected numeric value.
* {locale}  Checks if locale code matches.
* {timezone}  Checks if timezone matches.
              Optional parameters:
                - value - actual timezone definition to compare to.
* {sorting}   Checks if the elements within the placeholder were sorted correctly
              according to the locale rules.
              Optional parameters:
              - strict - if strict then collator strength set to
              - lenient - See ICU documentation for details.
* {period} - checks if date time period is correct.

