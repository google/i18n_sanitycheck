/*
 *  Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.testing.i18n.sanitycheck.checkers;

import com.google.common.collect.ImmutableList;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DateTimePatternGenerator;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.ULocale;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nullable;

/** An object that sanity checks a date time related string. */
final class TimeDateChecker implements Checker {

  /**
   * A utility that produces the {@link DateFormat}s that correspond to a given {@link Placeholder}
   * and {@link ULocale}.
   */
  static interface DateFormatProducer {

    /** Returns the appropriate {@link DateFormat}s for the given input. */
    ImmutableList<DateFormat> get(Placeholder target, ULocale locale);
  }

  private static final Logger logger = Logger.getLogger(TimeDateChecker.class.getCanonicalName());

  private final DateFormatProducer dateFormatsProducer;

  public TimeDateChecker(DateFormatProducer dateFormatsProducer) {
    this.dateFormatsProducer = dateFormatsProducer;
  }

  @Override
  public void check(Placeholder target, ULocale locale, String message) throws AssertionError {
    String pattern = target.getPattern();
    String skeleton = target.getSkeleton();
    String value = target.getExpectedValue();
    Number expected = null;

    if (value != null) {
        try {
          expected = NumberFormat.getInstance().parse(value);
        } catch (ParseException e) {
          throw new AssertionError(
              String.format("The \"%s\" is an invalid value parameter for the date time checkers. "
                + "Please specify numeric representation of date (see Date.getTime())", value));
        }
    }
    if (pattern != null) {
      makePatternBasedCheck(target, pattern, locale, expected);
    } else if (skeleton != null) {
      DateTimePatternGenerator patternGenerator = DateTimePatternGenerator.getInstance(locale);
      pattern = patternGenerator.getBestPattern(skeleton);
      makePatternBasedCheck(target, pattern, locale, expected);
    } else {
      makeDateTimeCheck(target, locale, expected);
    }
  }

  /**
   * Checks the given formatted date by means of the {@link DateFormat} instances from
   * {@code getFormatters} method. If one of the {@link DateFormat} instances parsed the given
   * datetime correctly, then check considered as successful.
   *
   * @param target instance of the {@link Placeholder}
   * @param locale to use during the sanity check
   * @param expected is a numeric representation of expected date
   * @throws AssertionError
   */
  private void makeDateTimeCheck(Placeholder target, ULocale locale, @Nullable Number expected)
      throws AssertionError {
    // This variable is needed to distinguish two error cases: none of formats matches, or there are
    // matching formats, but expected value does not match.
    boolean matching = false;
    for (DateFormat formatter : dateFormatsProducer.get(target, locale)) {
      Date result = checkFormat(target, formatter);
      if (result != null) {
        matching = true;
        if (checkExpectedValue(result, expected)) {
          return;
        }
      }
    }
    String errorMessage;
    // Compose error message for assertion based on failure case.
    if (matching) {
      errorMessage = String.format("The expected value '%d' does not match parsed data.", expected);
    } else {
      errorMessage = String.format("'%s' does not satisfy any format of locale %s.",
          target.getActualContent(), locale.toString());
    }
    throw new AssertionError(errorMessage);
  }

  /**
   * Checks the date time format based on the user provided pattern.
   *
   * @param target instance of the {@link Placeholder}
   * @param pattern is a string representation of the date time pattern
   * @param locale to use during the sanity check
   * @param expected is a numeric representation of expected date
   * @throws AssertionError
   */
  private void makePatternBasedCheck(Placeholder target, String pattern,
      ULocale locale, @Nullable Number expected) throws AssertionError {
    DateFormat formatter = new SimpleDateFormat(pattern, locale);
    formatter.setLenient(target.isLenient());

    Date result = checkFormat(target, formatter);
    if (result != null) {
      if (!checkExpectedValue(result, expected)) {
        throw new AssertionError(String.format(
            "Expected value '%s' does not match parsed value '%s' for pattern '%s'.",
            expected, result.getTime()));
      }
    } else {
      throw new AssertionError(String.format("'%s' does not satisfy specified pattern '%s'.",
          target.getActualContent(), pattern));
    }
  }

  /**
   * Checks the date time format against specificed formatter.
   *
   * @param target instance of the {@link Placeholder}
   * @param formatter is DateFormat instance to check against
   * @return a parsed date time if target matches format
   */
  @Nullable
  private Date checkFormat(Placeholder target, DateFormat formatter) {
    Date result = null;
    try {
      result = formatter.parse(target.getActualContent());
      if (result != null) {
        if (target.isStrict()) {
          String formattedDate = formatter.format(result);
          if (!formattedDate.equals(target.getActualContent())) {
            result = null;
          }
        }
      }
    } catch (ParseException e) {
      // Just log, result is null by default.
       logger.log(Level.FINE, String.format(
           "Not critical exception happened during the date time sanity check for input %s",
           target.getActualContent()), e);
    } catch (UnsupportedOperationException e) {
      // Just log. Relative date time parsing is not implemented yet.
      logger.log(Level.FINE, "Relative datetime parsing is not implemented yet");
    }
    return result;
  }

  /**
   * Checks the date against expected value.
   *
   * @param date instance of {@link Date}
   * @param expected is a numerical representation of expected date
   * @return true if date matches expected value, false otherwise
   */
  private boolean checkExpectedValue(Date date, @Nullable Number expected) {
    return (expected == null) || expected.equals(date.getTime());
  }
}
