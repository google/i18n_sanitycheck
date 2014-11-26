i18n-sanitychecker is a library that allows developer to write unit-test for
locale-sensitive functions without relying on "golden data".

i18n-sanitychecker provides only one public method to match actual strings
against expected patterns. Patterns may contain placeholders for date, time,
numbers, lists. The method behaves similar to other assert methods of JUnit.

#Usage

SanityCheckProcessor.assertI18nSanityCheck(pattern, actual_string, locale);


#Examples

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

#Placeholders

Placeholder | Description
-------------------------
{ignore}    | Ignores everything within this placeholder.
{time}      | Checks if formatted time satisfies ICU standards. Optional parameters:
{date}      | Checks if formatted date satisfies the ICU standards.
{datetime}  | Merges 2 and 3 together. Has same optional parameters.
{number}    | Checks if formatted number satisfies the ICU standards. ould be used to check currency, rational, scientific, numbers and spelled numbers.
{locale}    | Checks if locale code matches.
{timezone}  | Checks if timezone matches.
{sorting}   | Checks if the elements within the placeholder were sorted correctly according to the locale rules.
{period}    | checks if date time period is correct.

#Additional parameters for placeholders
Parameter | Apples To                              | Description
----------------------------------------------------------------
pattern   | date, time, datetime                   | ICU formatting pattern (see ICU documentation for details).
skeleton  | date, time, datetime                   | ICU skeleton - less restrictive than pattern
value     | date, time, datetime, number           | actual value in numeric locale-independent format. For date and time - milliseconds since Jan 1, 1970
lenient   | date, time, datetime, number, timezone | Relax format validation rules
strict    | sorting                                | Apply more strict sorting rules (see ICU documentation)

