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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.google.common.collect.ImmutableList;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.util.ULocale;

/** An object that performs sanity check of a formatted date time period string. */
class DateTimePeriodChecker implements Checker {

  private static final ImmutableList<String> DEFAULT_SPLITTERS =
      ImmutableList.of("\\-", "\\,", "\\ ");

  private final TimeDateChecker timeDateChecker =
      new TimeDateChecker(new DateTimeStringFormatProducer());

  @Override
  public void check(Placeholder target, ULocale locale, String message) throws AssertionError {
    String splitter = null;
    if (target.getSplitter() != null) {
      splitter = target.getSplitter();
    }
    String[] tokens = null;
    if (splitter != null) {
      tokens = target.getActualContent().split(splitter);
    } else {
      for (int i = 0; i < DEFAULT_SPLITTERS.size(); i++) {
        String[] tmpTokens = target.getActualContent().split(DEFAULT_SPLITTERS.get(i));
        if (tmpTokens.length == 2) {
          tokens = tmpTokens;
          break;
        }
      }
    }

    message = message != null ? message
        : String.format("Possible splitters for period %s were not located. "
            + "Please specify splitter explicitly by means of placeholder parameter \"splitter\"",
            target.getActualContent());
    assertNotNull(message, tokens);
    assertEquals(message, 2, tokens.length);

    for (String datePart : tokens) {
      Placeholder datePartPlaceholder =
          Placeholder.builder(target).setActualContent(datePart.trim()).build();
      timeDateChecker.check(datePartPlaceholder, locale, message);
    }
  }
}
