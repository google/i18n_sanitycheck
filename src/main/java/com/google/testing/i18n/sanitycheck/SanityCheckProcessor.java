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
package com.google.testing.i18n.sanitycheck;

import com.google.common.base.Preconditions;
import com.google.testing.i18n.sanitycheck.checkers.Checker;
import com.google.testing.i18n.sanitycheck.parser.Parser;
import com.google.testing.i18n.sanitycheck.parser.ParserException;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.util.ULocale;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * Utility methods that perform the sanity check and the comparison of the given strings.
 * One basic example of usage:
 * <p>
 * <code>
 * String myOutputString = myGeneratorMethod();   // method that generates "Today is 11/30/05"
 * SanityCheckProcessor.assertI18nSanityCheck("Today is {datetime}", myOutputString, ULocale.US);
 * </code>
 */
public final class SanityCheckProcessor {

  private static final Parser PARSER = new Parser();

  /** Do not instantiate */
  private SanityCheckProcessor() {}

  /**
   * Asserts that a given {@code actual} value satisfies a given {@code expected} pattern for a
   * given locale.
   *
   * @param expected pattern-based string
   * @param actual string that you want to test
   */
  public static void assertI18nSanityCheck(String expected, String actual) {
    assertI18nSanityCheck(null, expected, actual, (ULocale) null);
  }

  /**
   * Asserts that a given {@code actual} value satisfies a given {@code expected} pattern for a
   * given locale.
   *
   * @param expected pattern-based string
   * @param actual string that you want to test
   * @param locale to test with. If null, then default en_US locale is used
   */
  public static void assertI18nSanityCheck(String expected, String actual,
      @Nullable ULocale locale) {
    assertI18nSanityCheck(null, expected, actual, locale);
  }

  /**
   * Asserts that a given {@code actual} value satisfies a given {@code expected} pattern for a
   * given locale.
   *
   * @param expected pattern-based string
   * @param actual string that you want to test
   * @param locale to test with. If null, then default en_US locale is used
   */
  public static void assertI18nSanityCheck(String expected, String actual,
      @Nullable Locale locale) {
    assertI18nSanityCheck(null, expected, actual, ULocale.forLocale(locale));
  }

  /**
   * Asserts that a given {@code actual} value satisfies a given {@code expected} pattern for a
   * given locale.
   *
   * @param message custom message to be shown instead of the automatically produced one. The
   *        automatic one would stored in the 'cause' part of the thrown exception
   * @param expected pattern-based string
   * @param actual string that you want to test
   * @param locale to test with. If null, then default en_US locale is used
   */
  public static void assertI18nSanityCheck(@Nullable String message, String expected, String actual,
      @Nullable Locale locale) {
    assertI18nSanityCheck(message, expected, actual, ULocale.forLocale(locale));
  }

  /**
   * Asserts that a given {@code actual} value satisfies a given {@code expected} pattern for a
   * given locale.
   *
   * @param message custom message to be shown instead of the automatically produced one. The
   *        automatic one would stored in the 'cause' part of the thrown exception
   * @param expected pattern-based string
   * @param actual string that you want to test
   * @param locale to test with. If null, then default en_US locale is used
   */
  public static void assertI18nSanityCheck(@Nullable String message, String expected, String actual,
      @Nullable ULocale locale) {
    Preconditions.checkNotNull(expected);
    Preconditions.checkNotNull(actual);
    locale = locale != null ? locale : ULocale.US;
    try {
      // Parses an original text with respect to the expected pattern and returns the list of the
      // Placeholder entities as a result.
      List<Placeholder> parsedPattern = PARSER.parse(actual, expected);
      // Walks through all parsed tokens and performs sanity check of each one of them.
      for (Placeholder placeholder : parsedPattern) {
        ULocale placeholderLocale = locale;
        if (placeholder.getLocale() != null) {
          placeholderLocale = new ULocale(placeholder.getLocale());
        }
        Checker.CHECKERS.get(placeholder.getName()).check(placeholder, placeholderLocale, message);
      }
    } catch (ParserException e) {
      if (message.isEmpty()) {
        throw new AssertionError(e.getMessage(), e);
      } else {
        throw new AssertionError(message, e);
      }
    }
  }
}
