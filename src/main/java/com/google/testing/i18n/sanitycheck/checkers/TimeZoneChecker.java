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

import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.TimeZoneFormat;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;

import org.junit.Assert;

import java.text.ParseException;

/** An object that performs sanity check of a formatted time zone. */
public class TimeZoneChecker implements Checker {

  @Override
  public void check(Placeholder target, ULocale locale, String message) {
    TimeZoneFormat formatter = TimeZoneFormat.getInstance(locale);
    String value = target.getExpectedValue();
    try {
      TimeZone timeZone = formatter.parse(target.getActualContent());
      if (timeZone == null) {
        String errorMessage = message != null ? message
            : String.format("Timezone \"%s\" is not valid", target.getActualContent());
        Assert.fail(errorMessage);
      }
      if (value != null) {
        String errorMessage = message != null ? message
            : String.format("Timezone \"%s\" is not equal to the expected timezone %s",
                target.getActualContent(), value);
        Assert.assertEquals(errorMessage, value, timeZone.getDisplayName());
      }
    } catch (ParseException e) {
      String errorMessage = message != null ? message
          : String.format("Timezone \"%s\" is not valid", target.getActualContent());
      Assert.fail(errorMessage);
    }
  }
}
