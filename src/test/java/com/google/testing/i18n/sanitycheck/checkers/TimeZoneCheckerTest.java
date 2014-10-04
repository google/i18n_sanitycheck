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

import com.ibm.icu.text.TimeZoneFormat;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Test class for {@link TimeZoneChecker} */
@RunWith(JUnit4.class)
public class TimeZoneCheckerTest extends TestCase {

  @Test
  public void testCheck() {
    TimeZoneChecker checker = new TimeZoneChecker();
    for (int i = 0; i < 5; i++) {
      ULocale testLocale = TestUtils.getRandomLocale();
      TimeZoneFormat format = TimeZoneFormat.getInstance(testLocale);
      Placeholder testToken =
          Placeholder.builder("timezone", format.format(TimeZone.GMT_ZONE)).build();
      checker.check(testToken, testLocale, null);
    }
  }
}
