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
package com.google.testing.i18n.sanitycheck;

import com.google.common.collect.Lists;

import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Utility methods for testing purposes.
 */
public class TestUtils {

  private static final Random rand = new Random(42);

  private static final List<ULocale> PREDEF_LOCALES = Lists.newArrayList(ULocale.CANADA,
      ULocale.CANADA_FRENCH,
      ULocale.CHINA,
      ULocale.FRANCE,
      ULocale.GERMAN,
      ULocale.ITALIAN,
      ULocale.JAPAN,
      ULocale.KOREAN,
      ULocale.SIMPLIFIED_CHINESE,
      ULocale.TAIWAN,
      ULocale.US,
      ULocale.UK,
      new ULocale("ru"),
      new ULocale("ar"),
      new ULocale("fa"),
      new ULocale("ml"),
      new ULocale("en-XA"),
      new ULocale("ar_XB"));

  /**
   * Generates random date.
   *
   * @return instance of the {@link Date} that could not be null.
   */
  public static Date generateRandomDate() {
    Date result = new Date(nextLong(System.currentTimeMillis() * 2));
    return result;
  }

  /**
   * Generates random number formatted by the random formatter with respect to the given locale.
   *
   * @param locale is instance of the {@link ULocale} that is required to produce formatted number.
   * @return string representation of the formatted random number.
   */
  public static String generateRandomFormattedNumber(ULocale locale) {
    String result = "0";
    int type = rand.nextInt(8);
    NumberFormat formatter;
    switch (type) {
      case 0:
        formatter = NumberFormat.getCurrencyInstance(locale);
        result = formatter.format(rand.nextFloat());
        break;
      case 1:
        formatter = NumberFormat.getIntegerInstance(locale);
        result = formatter.format(rand.nextInt());
        break;
      case 2:
        formatter = NumberFormat.getPercentInstance(locale);
        result = formatter.format(rand.nextFloat());
        break;
      case 3:
        formatter = NumberFormat.getScientificInstance(locale);
        result = formatter.format(rand.nextFloat());
        break;
      case 4:
        formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
        result = formatter.format(rand.nextFloat());
        break;
      case 5:
        formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.DURATION);
        result = formatter.format(rand.nextInt());
        break;
      case 6:
        formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.ORDINAL);
        result = formatter.format(rand.nextInt());
        break;
      case 7:
        formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.NUMBERING_SYSTEM);
        result = formatter.format(rand.nextInt());
        break;
      default:
        break;
    }
    return result;
  }

  /**
   * Returns a random locale from the PREDEF_LOCALES list.
   *
   * @return instance of {@link ULocale}.
   */
  public static ULocale getRandomLocale() {
    int index = rand.nextInt(PREDEF_LOCALES.size());
    return PREDEF_LOCALES.get(index);
  }

  /**
   * Returns random long within the bounds of 0 and n.
   *
   * @param n is an upper limit.
   * @return random long number.
   */
  public static long nextLong(long n) {
    long bits, val;
    do {
      bits = (rand.nextLong() << 1) >>> 1;
      val = bits % n;
    } while (bits - val + (n - 1) < 0L);
    return val;
  }
}
