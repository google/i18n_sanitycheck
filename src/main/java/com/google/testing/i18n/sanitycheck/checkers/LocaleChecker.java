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

import com.google.common.annotations.VisibleForTesting;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.util.ULocale;

import org.junit.Assert;

import javax.annotation.Nullable;

/** An object that performs sanity check of a formatted locale string. */
public class LocaleChecker implements Checker {

  @Override
  public void check(Placeholder target, ULocale locale, String message) {
    ULocale parsedLocale = new ULocale(target.getActualContent());
    check(parsedLocale, target, message);
  }

  @VisibleForTesting
  void check(ULocale parsedLocale, Placeholder target, @Nullable String message) {
    String value = target.getExpectedValue();
    if (value != null) {
      message = message != null ? message
          : String.format("Expected locale \"%s\" is not equal to %s", value,
              parsedLocale.toString());
      Assert.assertEquals(message, value, parsedLocale.toString());
    } else {
      message = message != null ? message
          : String.format("Locale \"%s\" is wrong", target.getActualContent());
      Assert.assertFalse(message, parsedLocale.getISO3Country().isEmpty()
          && parsedLocale.getISO3Language().isEmpty());
    }
  }
}
