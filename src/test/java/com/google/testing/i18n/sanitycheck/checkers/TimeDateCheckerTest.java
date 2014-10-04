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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.google.testing.i18n.sanitycheck.TestUtils;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Date;

/** Tests the {@link TimeStringFormatProducer} class functionality. */
@RunWith(JUnit4.class)
public class TimeDateCheckerTest extends TestCase {

  private final DateFormat mockFormat = mock(DateFormat.class);
  private final TimeDateChecker checker =
      new TimeDateChecker(new TimeDateChecker.DateFormatProducer() {
        @Override
        public ImmutableList<DateFormat> get(Placeholder target, ULocale locale) {
          return ImmutableList.of(mockFormat);
        }
      });

  private final Date mockDate = new Date();

  @After
  @Override
  public void tearDown() {
    reset(mockFormat);
  }

  @Test
  public void testCheck_Simple() throws ParseException {
    final Placeholder testToken = Placeholder.builder("time", "22:22").build();
    when(mockFormat.parse("22:22")).thenReturn(mockDate);
    checker.check(testToken, ULocale.CANADA, null);
    verify(mockFormat).parse("22:22");
  }

  @Test
  public void testCheck_Strict() throws ParseException {
    final Placeholder testToken =
        Placeholder.builder("time", "22:22").putStrictParam(true).build();
    when(mockFormat.parse("22:22")).thenReturn(mockDate);
    when(mockFormat.format(any(Date.class), any(StringBuffer.class), any(FieldPosition.class)))
        .thenReturn(new StringBuffer("22:22"));
    checker.check(testToken, ULocale.CANADA, null);
    verify(mockFormat).parse("22:22");
    verify(mockFormat).format(eq(mockDate), any(StringBuffer.class), any(FieldPosition.class));
  }

  @Test
  public void testCheck_SimpleInvalid() throws ParseException {
    boolean failed = false;
    try {
      final Placeholder testToken = Placeholder.builder("time", "test").build();
      when(mockFormat.parse("test")).thenThrow(new ParseException("Test exception", 1));
      checker.check(testToken, ULocale.CANADA, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Case must fail", failed);
    verify(mockFormat).parse("test");
  }

  @Test
  public void testCheck_StrictInvalid() throws ParseException {
    boolean failed = false;
    try {
      final Placeholder testToken =
          Placeholder.builder("time", "22:22").putStrictParam(true).build();
      when(mockFormat.parse("22:22")).thenReturn(mockDate);
      when(mockFormat.format(any(Date.class), any(StringBuffer.class), any(FieldPosition.class)))
          .thenReturn(new StringBuffer("22:23"));
      checker.check(testToken, ULocale.CANADA, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Case must fail", failed);
    verify(mockFormat).parse("22:22");
    verify(mockFormat).format(eq(mockDate), any(StringBuffer.class), any(FieldPosition.class));
  }

  @Test
  public void testCheck_PatternLenient() {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    final Placeholder testToken = Placeholder
        .builder("date", simpleDateFormat.format(TestUtils.generateRandomDate()))
        .putPatternParam("dd/MM/yyyy HH:mm").build();
    checker.check(testToken, ULocale.GERMAN, null);
  }

  @Test
  public void testCheck_PatternBasedNotLenient() {
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    final Placeholder testToken = Placeholder
        .builder("date", simpleDateFormat.format(TestUtils.generateRandomDate()))
        .putPatternParam("dd/MM/yyyy HH:mm").putLenientParam(false).build();
    checker.check(testToken, ULocale.GERMAN, null);
  }

  @Test
  public void testCheck_ValueBestMatch() throws ParseException {
    // Use special checker with multiple data formats to verity best match strategy
    final TimeDateChecker multiChecker =
        new TimeDateChecker(new TimeDateChecker.DateFormatProducer() {
          @Override
          public ImmutableList<DateFormat> get(Placeholder target, ULocale locale) {
            return ImmutableList.of(mockFormat, mockFormat, mockFormat);
          }
        });
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("d MMMM yyyy, HH:mm:ss");
    final Date date = TestUtils.generateRandomDate();
    final String formattedDate = simpleDateFormat.format(date);
    when(mockFormat.parse(formattedDate))
        .thenReturn(mockDate, null, date);
    final Placeholder testToken = Placeholder
        .builder("datetime", formattedDate)
        .putExpectedValueParam(String.format("%d", date.getTime())).build();
    multiChecker.check(testToken, ULocale.GERMAN, null);
  }
}
