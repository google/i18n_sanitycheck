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

import com.google.common.collect.ImmutableMap;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.util.ULocale;

import javax.annotation.Nullable;

/**
 * An object that performs sanity check of formatted strings with respect to the a locale. The
 * checker object is a singleton within the system, so implementations should not hold any state
 * information.
 */
public interface Checker {

  /** Sanity checkers that are registered in the system. */
  public static final ImmutableMap<String, Checker> CHECKERS = ImmutableMap
      .<String, Checker>builder()
      .put("ignore", new IgnoreChecker())
      .put("time", new TimeDateChecker(new TimeStringFormatProducer()))
      .put("date", new TimeDateChecker(new DateStringFormatProducer()))
      .put("datetime", new TimeDateChecker(new DateTimeStringFormatProducer()))
      //.put("phone", new PhoneNumberChecker())
      .put("number", new NumberChecker())
      .put("timezone", new TimeZoneChecker())
      .put("locale", new LocaleChecker())
      .put("sorting", new OrderingChecker())
      .put("tokenization", new TokenizationChecker())
      .put("period", new DateTimePeriodChecker())
      .build();

  /**
   * Checks if a given {@link Placeholder} contains validly formatted information for a given
   * locale.
   *
   * @param target {@link Placeholder} instance that contains complete information about a formatted
   *        string that should be checked
   * @param locale to perform sanity check with respect to
   * @param message is a user preferred message for errors
   * @throws AssertionError thrown if check is failed
   */
  public void check(Placeholder target, ULocale locale, @Nullable String message)
      throws AssertionError;
}
