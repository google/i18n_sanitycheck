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

import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the {@link LocaleChecker} class functionality. */
@RunWith(JUnit4.class)
public class LocaleCheckerTest extends TestCase {

  final LocaleChecker checker = new LocaleChecker();

  @Test
  public void testCheck_Valid() {
    ULocale mockLocale = TestUtils.getRandomLocale();
    final Placeholder testToken = Placeholder.builder(mockLocale.toString(), "locale").build();
    checker.check(mockLocale, testToken, null);
  }

  @Test
  public void testCheck_ValidWithValue() {
    ULocale mockLocale = TestUtils.getRandomLocale();
    final Placeholder testToken = Placeholder.builder(mockLocale.toString(), "locale")
        .putLocaleParam(mockLocale.toString()).build();
    checker.check(mockLocale, testToken, null);
  }

  @Test
  public void testCheck_Invalid() {
    boolean failed = false;
    try {
      final Placeholder testToken = Placeholder.builder("zz_ZZ", "locale").build();
      checker.check(new ULocale("zz_ZZ"), testToken, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Case must fail", failed);
  }

  @Test
  public void testCheck_InvalidWithValue() {
    boolean failed = false;
    try {
      ULocale mockLocale = TestUtils.getRandomLocale();
      final Placeholder testToken = Placeholder.builder(mockLocale.toString(), "locale")
          .putExpectedValueParam("zz_ZZ").build();
      checker.check(mockLocale, testToken, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Case must fail", failed);
  }
}
