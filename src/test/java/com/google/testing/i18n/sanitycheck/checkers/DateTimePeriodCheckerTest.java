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

import com.google.testing.i18n.sanitycheck.TestUtils;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the {@link DateTimePeriodChecker} class functionality. */
@RunWith(JUnit4.class)
public class DateTimePeriodCheckerTest extends TestCase {

  private final DateTimePeriodChecker checker = new DateTimePeriodChecker();

  @Test
  public void testCheck_Simple() {
    // TODO(antkrumin): Rewrite with mocks.
    DateFormat dateFormat1 =
        DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, ULocale.JAPAN);
    DateFormat dateFormat2 =
        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT, ULocale.JAPAN);
    String actualValue = dateFormat1.format(TestUtils.generateRandomDate()) + " :::: "
        + dateFormat2.format(TestUtils.generateRandomDate());
    Placeholder testToken =
        Placeholder.builder("date", actualValue).putSplitterParam("::::").build();
    checker.check(testToken, ULocale.JAPAN, null);
  }

  @Test
  public void testCheck_Short() {
    DateFormat dateFormat1 =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, ULocale.US);
    DateFormat dateFormat2 =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, ULocale.US);
    String actualValue = dateFormat1.format(TestUtils.generateRandomDate()) + " - "
        + dateFormat2.format(TestUtils.generateRandomDate());
    Placeholder testToken = Placeholder.builder("date", actualValue).build();
    checker.check(testToken, ULocale.US, null);
  }

  @Test
  public void testCheck_WordSplitter() {
    DateFormat dateFormat1 =
        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new ULocale("ml"));
    DateFormat dateFormat2 =
        DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new ULocale("ml"));
    String actualValue = dateFormat1.format(TestUtils.generateRandomDate()) + " till "
        + dateFormat2.format(TestUtils.generateRandomDate());
    Placeholder testToken =
        Placeholder.builder("date", actualValue).putSplitterParam("till").build();
    checker.check(testToken, new ULocale("ml"), null);
  }
}
