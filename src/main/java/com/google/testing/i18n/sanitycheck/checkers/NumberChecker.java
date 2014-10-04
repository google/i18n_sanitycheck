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
import com.google.common.collect.ImmutableList;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

import org.junit.Assert;

import java.text.ParseException;
import java.util.List;

import javax.annotation.Nullable;

/** An object that performs sanity check of a formatted number. */
public class NumberChecker implements Checker {

  @Override
  public void check(Placeholder target, ULocale locale, String message) {
    List<NumberFormat> formatters = ImmutableList.of(NumberFormat.getCurrencyInstance(locale),
        NumberFormat.getInstance(locale),
        NumberFormat.getIntegerInstance(locale),
        NumberFormat.getNumberInstance(locale),
        NumberFormat.getPercentInstance(locale),
        NumberFormat.getScientificInstance(locale),
        new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT),
        new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.ORDINAL),
        new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.DURATION),
        new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.NUMBERING_SYSTEM));

    check(formatters, target, locale, message);
  }

  @VisibleForTesting
  void check(List<NumberFormat> formatters, Placeholder target, ULocale locale,
      @Nullable String message) {
    boolean passed = false;
    for (NumberFormat formatter : formatters) {
      try {
        if (formatter instanceof RuleBasedNumberFormat) {
          ((RuleBasedNumberFormat) formatter).setLenientParseMode(target.isLenient());
        }
        formatter.setParseStrict(!target.isLenient());
        Number parsed = formatter.parse(target.getActualContent());
        if (parsed != null) {
          String value = target.getExpectedValue();
          if (value != null) {
            NumberFormat format = NumberFormat.getInstance();
            Number expected = format.parse(value);
            String errorMessage = message != null ? message
                : String.format("Parsed number \"%s\" is not equal to the expected value %s",
                    parsed, expected);
            Assert.assertEquals(errorMessage, expected, parsed);
          }
          passed = true;
          break;
        }
      } catch (ParseException e) {
        // Do nothing.
      }
    }
    String errorMessage = message != null ? message
        : String.format("\"%s\" does not satisfies to any numeric format for %s",
            target.getActualContent(), locale.toString());
    Assert.assertTrue(errorMessage, passed);
  }
}
