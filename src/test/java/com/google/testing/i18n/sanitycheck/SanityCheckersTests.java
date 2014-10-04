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

import static com.google.testing.i18n.sanitycheck.SanityCheckProcessor.assertI18nSanityCheck;

import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Integration test for i18n sanity checkers.
 */
@RunWith(JUnit4.class)
public class SanityCheckersTests extends TestCase {

  private final String lnSeparator = System.getProperty("line.separator");

  /**
   * Tests a lot of real life examples of i18n sanity checkers. These are examples, so it would be
   * required to change them with following ICU updates.
   */
  @Test
  public void testIntegration_Simple() {
    assertI18nSanityCheck("Simple Check", "Simple Check", ULocale.US);
  }

  @Test
  public void testIntegration_Currency() {
    assertI18nSanityCheck("{number}", "￥1,235", ULocale.JAPAN);
  }

  @Test
  public void testIntegration_MultiplePlaceholdersDateAndTime() {
    assertI18nSanityCheck(
        "{greetings} Ann I will meet you at {time:{locale:'en-US'}}, {date}. And {ignore}",
        "{greetings} Ann I will meet you at 11:30 am, January 12, 2014. And something else.",
        ULocale.US);
  }

  @Test
  public void testIntegration_StaticRegexAndNumber() {
    assertI18nSanityCheck(
        "У * [0-9]+ меня есть {number:{value:120}} яблок. И {number} из них я отдам тебе.",
        "У * [0-9]+ меня есть сто двадцать яблок. И 5 из них я отдам тебе.", new ULocale("ru"));
  }

  @Test
  public void testIntegration_NumberValue() {
    assertI18nSanityCheck("Schumacher came {number:{value:1}}", "Schumacher came first",
        ULocale.UK);
  }

  @Test
  public void testIntegration_CurrencyValue() {
    assertI18nSanityCheck("明日は{number:{value:15.6}}を費やすでしょう。", "明日は15.6円を費やすでしょう。",
        ULocale.JAPANESE);
  }

  @Test
  public void testIntegration_DateTimePeriod() {
    assertI18nSanityCheck("Event continues from {period:{splitter:'till'}}",
        "Event continues from January 12, 2014 till 11:30 am", ULocale.US);
  }

  @Test
  public void testIntegration_PatternBasedDate() {
    assertI18nSanityCheck("{datetime:{pattern:'dd/MM/yyyy HH:mm'}}", "14/13/2013 22:15",
        ULocale.GERMAN);
  }

  @Test
  public void testIntegration_PatternBasedDateNonlenient() {
    assertI18nSanityCheck("{datetime:{pattern:'dd/MM/yyyy HH:mm', lenient:false}}",
        "14/12/2013 22:15", ULocale.CHINA);
  }

  @Test
  public void testIntegration_SkeletonBasedDate() {
    assertI18nSanityCheck("{datetime:{skeleton:'MMMMddHmm'}}", "14. Oktober 8:58", ULocale.GERMAN);
  }

  @Test
  public void testIntegration_SkeletonBasedDateWithValue() {
    assertI18nSanityCheck("{datetime:{skeleton:'MMMMddHmm', value:'24767880000'}}",
        "14. Oktober 8:58", ULocale.GERMAN);
  }

  @Test
  public void testIntegration_RomanNumbers() {
    assertI18nSanityCheck("{number:{value:'2013'}}", "MMXIII", ULocale.US);
  }

  @Test
  public void testIntegration_NativeNumbersFa() {
    assertI18nSanityCheck("{number}", "١٦٨", new ULocale("fa"));
  }

  @Test
  public void testIntegration_NativeNumbersTe() {
    assertI18nSanityCheck("{number}", "౧౩౮", new ULocale("te"));
  }

  @Test
  public void testIntegration_NativeNumbersBn() {
    assertI18nSanityCheck("{number}", "৪২", new ULocale("bn"));
  }

  @Test
  public void testIntegration_CustomLocaleInPlaceholder() {
    assertI18nSanityCheck("Ответ {number:{locale:'ru'}}", "Ответ сорок два", new ULocale("ar"));
  }

  @Test
  public void testIntegration_Locale() {
    assertI18nSanityCheck("{locale}, {locale}, {locale:{value:'zh_CN_#HANS'}}",
        "ar-XB, en_XA, zh_CN_#Hans", ULocale.getDefault());
  }

  @Test
  public void testIntegration_TimeZone() {
    assertI18nSanityCheck("{timezone:{value:'Eastern Time'}}", "ET", ULocale.US);
  }

  @Test
  public void testIntegration_SortingSimple() {
    assertI18nSanityCheck("{sorting}", "a,b,c,d,e", ULocale.US);
  }

  @Test
  public void testIntegration_SortingCustomSplitter() {
    assertI18nSanityCheck("{sorting}",
        "a" + lnSeparator + "b" + lnSeparator + "c" + lnSeparator + "d" + lnSeparator + "e",
        ULocale.US);
  }

  @Test
  public void testIntegration_SortingJa() {
    assertI18nSanityCheck("{sorting}", "Tokyo,こんにちは,東京", ULocale.JAPANESE);
  }

  @Test
  public void testIntegration_SortingJaCustomSplitter() {
    assertI18nSanityCheck("{sorting:{splitter:'-'}}", "Tokyo-こんにちは-東京", ULocale.JAPANESE);
  }

  @Test
  public void testIntegration_SortingLenient() {
    assertI18nSanityCheck("{sorting}", "a,A,B,b", ULocale.US);
  }

  @Test
  public void testIntegration_Nonlenient() {
    assertI18nSanityCheck("{sorting:{lenient:false}}", "a,A,b,B", ULocale.US);
  }

  @Test
  public void testIntegration_Strict() {
    assertI18nSanityCheck("{sorting:{lenient:false,strict:true}}", "a-b,ab,aB", ULocale.US);
  }

  @Test
  public void testIntegration_PhoneUs() {
    assertI18nSanityCheck("{phone}", "+64 3 331 6666", ULocale.US);
  }

  @Test
  public void testIntegration_PhoneUk() {
    assertI18nSanityCheck("{phone}", "01x1 650 2245", ULocale.UK);
  }

  @Test
  public void testIntegration_PhoneUsNonlenient() {
    assertI18nSanityCheck("{phone:{lenient:false}}", "+1 650 224 2244", ULocale.US);
  }

  @Test
  public void testIntegration_PhoneZhStrict() {
    assertI18nSanityCheck("{phone:{lenient:false, strict:true}}", "+86 (0755) 22445566",
        ULocale.CHINA);
  }

  @Test
  public void testIntegration_XMLExample() {
    assertI18nSanityCheck("<data>" + lnSeparator + "<name>{ignore}</name>" + lnSeparator
        + "<dob>{date:{locale:'ja'}}</dob>" + lnSeparator + "<phone>{phone}<phone>" + lnSeparator
        + "</data>", "<data>" + lnSeparator + "<name>Zapp</name>" + lnSeparator
        + "<dob>2008年12月31日</dob>" + lnSeparator + "<phone>+1 650 224 2244<phone>" + lnSeparator
        + "</data>", ULocale.US);
  }

  @Test
  public void testIntegration_UnknownPlaceholder() {
    assertI18nSanityCheck("Something {unknown}", "Something {unknown}", ULocale.US);
  }

  @Test
  public void testIntegration_TokenizationTh() {
    assertI18nSanityCheck("{tokenization}", "สัปดาห์,ที่,ส", new ULocale("th"));
  }

  @Test
  public void testIntegration_TokenizationJa() {
    assertI18nSanityCheck("{tokenization}", "自民党,総裁,選挙", ULocale.JAPANESE);
  }

  @Test
  public void testIntegration_TokenizationZh() {
    assertI18nSanityCheck("{tokenization}", "冒,天下,之,大,不,韪", new ULocale("zh"));
  }

  @Test
  public void testIntegration_InvalidNumberWrongValue() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("Schumacher came {number:{value:2}}", "Schumacher came first",
          ULocale.UK);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Invalid value test. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidNumberWrongLocale() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("Schumacher came {number}", "Schumacher came first", new ULocale("fa"));
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Invalid locale. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidNonlenientDate() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{date:{lenient:false, pattern:'dd/MM/yyyy HH:mm'}}",
          "14/13/2013 22:15", ULocale.CANADA);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Invalid month. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidShortPhone() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{phone:{lenient:false}}", "+1 650 224 2", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Invalid phone number. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidSortingWrongOrder() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{sorting:{splitter:'-'}}", "東京-Tokyo-こんにちは", ULocale.JAPANESE);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong sorting order. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidSortingNonlenientWrongOrder() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{sorting:{lenient:false}}", "a,A,B,b", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong sorting order. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidSortingStrictWrongOrder() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{sorting:{lenient:false, strict:true}}", "ab,a-b,aB", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong sorting order. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidLocale() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{locale}", "zz-ZZ", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Invalid locale. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidDateWrongLocale() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{date}", "January 12, 2014", ULocale.GERMANY);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong locale. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidDateWrongStaticText() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{date} is coming!", "January 12, 2014 is coming!!!", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong static text. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidUnknownPlaceholder() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("Something {unknown}", "Something here", ULocale.US);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Unknown placeholder. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidTokenizationTh() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{tokenization}", "สัปดาห์ที่ส", new ULocale("th"));
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong tokenization. Should fail.", failed);
  }

  @Test
  public void testIntegration_InvalidTokenizationJa() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{tokenization}", "自民 党総裁 選挙", ULocale.JAPANESE);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong tokenization. Should fail.", failed);
  }


  @Test
  public void testIntegration_InvalidTokenizationZh() {
    boolean failed = false;
    try {
      assertI18nSanityCheck("{tokenization}", "冒 天下 之大 不 韪", new ULocale("zh"));
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Wrong tokenization. Should fail.", failed);
  }
}
