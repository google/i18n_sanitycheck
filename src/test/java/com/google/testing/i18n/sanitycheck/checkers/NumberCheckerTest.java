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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;

/** Tests the {@link NumberChecker} class functionality. */
@RunWith(JUnit4.class)
public class NumberCheckerTest extends TestCase {

  final NumberChecker checker = new NumberChecker();
  final NumberFormat mockFormat = mock(NumberFormat.class);

  @After
  @Override
  public void tearDown() {
    reset(mockFormat);
  }

  @Test
  public void testCheck_ValidLenient() throws ParseException {
    final Placeholder testToken = Placeholder.builder("number", "42").build();
    when(mockFormat.parse("42")).thenReturn(42L);
    checker.check(Lists.newArrayList(mockFormat), testToken, ULocale.CANADA, null);
    verify(mockFormat).setParseStrict(false);
  }

  @Test
  public void testCheck_ValidNonLenient() throws ParseException {
    final Placeholder testToken =
        Placeholder.builder("number", "42").putLenientParam(false).build();
    when(mockFormat.parse("42")).thenReturn(42L);
    checker.check(Lists.newArrayList(mockFormat), testToken, ULocale.CANADA, null);
    verify(mockFormat).setParseStrict(true);
  }

  @Test
  public void testCheck_VaidWithValue() throws ParseException {
    final Placeholder testToken =
        Placeholder.builder("number", "42").putExpectedValueParam("42").build();
    when(mockFormat.parse("42")).thenReturn(42L);
    checker.check(Lists.newArrayList(mockFormat), testToken, ULocale.CANADA, null);
    verify(mockFormat).setParseStrict(false);
  }

  @Test
  public void testCheck_InvalidSimple() throws ParseException {
    boolean failed = false;
    try {
      final Placeholder testToken = Placeholder.builder("number", "asd").build();
      when(mockFormat.parse("asd")).thenThrow(new ParseException("Test exception", 1));
      checker.check(Lists.newArrayList(mockFormat), testToken, ULocale.CANADA, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("This case must fail", failed);
    verify(mockFormat).setParseStrict(false);
  }

  @Test
  public void testCheck_InvalidValue() throws ParseException {
    boolean failed = false;
    try {
      final Placeholder testToken =
          Placeholder.builder("number", "asd").putExpectedValueParam("43").build();
      when(mockFormat.parse("42")).thenReturn(42L);
      checker.check(Lists.newArrayList(mockFormat), testToken, ULocale.CANADA, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("This case must fail", failed);
    verify(mockFormat).setParseStrict(false);
  }
}
