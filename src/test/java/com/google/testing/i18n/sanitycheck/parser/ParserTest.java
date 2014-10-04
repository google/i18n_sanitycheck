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
package com.google.testing.i18n.sanitycheck.parser;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/**
 * Tests the {@link Parser} class functionality.
 */
@RunWith(JUnit4.class)
public class ParserTest extends TestCase {

  private final String lnSeparator = System.getProperty("line.separator");
  private final Parser parser = new Parser();
  
  @Test
  public void testParser_Simple() throws ParserException {
    List<Placeholder> tokens = parser.parse(
        "te{st test_date, test_time, test_datetime and locale. "
        + "{unknown_placeholder} and some tex}t.date",
        "te\\{st {ignore}, {ignore}, {ignore} and {ignore:{param:'anyparam'}}. "
        + "{unknown_placeholder} and some tex}t.{ignore}");
    assertEquals("Size of parsed array should be 5.", 5, tokens.size());
    assertEquals("test_date", tokens.get(0).getActualContent());
    assertEquals("test_time", tokens.get(1).getActualContent());
    assertEquals("test_datetime", tokens.get(2).getActualContent());
    assertEquals("locale", tokens.get(3).getActualContent());
    assertEquals("date", tokens.get(4).getActualContent());
  }
  
  @Test
  public void testParser_Regex() throws ParserException {
    List<Placeholder> tokens = parser.parse(
        "test (.*?) test_date, test_time, test_datetime and [0-9]+ locale. "
        + "{unknown_placeholder} and some text.date",
        "test (.*?) {ignore}, {ignore}, {ignore} and [0-9]+ {ignore:{param:'anyparam'}}. "
        + "{unknown_placeholder} and some text.{ignore}");
    assertEquals("Size of parsed array should be 5.", 5, tokens.size());
    assertEquals("test_date", tokens.get(0).getActualContent());
    assertEquals("test_time", tokens.get(1).getActualContent());
    assertEquals("test_datetime", tokens.get(2).getActualContent());
    assertEquals("locale", tokens.get(3).getActualContent());
    assertEquals("date", tokens.get(4).getActualContent());
  }
  
  @Test
  public void testParser_SkipablePlaceholder() throws ParserException {
    List<Placeholder> tokens = parser.parse("test test_date, test_time, {ignore} and locale. "
        + "{unknown_placeholder} and some text.date",
        "test {ignore}, {ignore}, {ignore} and {ignore:{param:'anyparam'}}. "
        + "{unknown_placeholder} and some text.{ignore}");
    assertEquals("Size of parsed array should be 4.", 4, tokens.size());
    assertEquals("test_date", tokens.get(0).getActualContent());
    assertEquals("test_time", tokens.get(1).getActualContent());
    assertEquals("locale", tokens.get(2).getActualContent());
    assertEquals("date", tokens.get(3).getActualContent());
  }
  
  @Test
  public void testParser_MultilineStaticText() throws ParserException {
    List<Placeholder> tokens = parser.parse("test test_date, test_time, test_datetime and locale."
        + lnSeparator + "{unknown_placeholder} " + lnSeparator + "and some text."
        + lnSeparator + "date",
        "test {ignore}, {ignore}, {ignore} and {ignore:{param:'anyparam'}}."
        + lnSeparator + "{unknown_placeholder} " + lnSeparator + "and some text." + lnSeparator
        + "{ignore}");
    assertEquals("Size of parsed array should be 5.", 5, tokens.size());
    assertEquals("test_date", tokens.get(0).getActualContent());
    assertEquals("test_time", tokens.get(1).getActualContent());
    assertEquals("test_datetime", tokens.get(2).getActualContent());
    assertEquals("locale", tokens.get(3).getActualContent());
    assertEquals("date", tokens.get(4).getActualContent());
  }
  
  @Test
  public void testParser_MultilinePlaceholderContentSingleVal() throws ParserException {
    List<Placeholder> tokens =
        parser.parse("a" + lnSeparator + "b" + lnSeparator + "c", "{ignore}");
    assertEquals("Size of parsed array should be 1.", 1, tokens.size());
    assertEquals("a" + lnSeparator + "b" + lnSeparator + "c",
        tokens.get(0).getActualContent());
  }
  
  @Test
  public void testParser_MultilinePlaceholderContentManyVal() throws ParserException {
    List<Placeholder> tokens = parser.parse("test test" + lnSeparator
        + "date, test_time, test_datetime and locale." + lnSeparator
        + "{unknown_placeholder} " + lnSeparator + "and some text.",
        "test {ignore}, {ignore}, {ignore} and {ignore:{param:'anyparam'}}." + lnSeparator
        + "{unknown_placeholder} " + lnSeparator + "and some text.");
    assertEquals("Size of parsed array should be 4.", 4, tokens.size());
    assertEquals("test" + lnSeparator + "date", tokens.get(0).getActualContent());
    assertEquals("test_time", tokens.get(1).getActualContent());
    assertEquals("test_datetime", tokens.get(2).getActualContent());
    assertEquals("locale", tokens.get(3).getActualContent());
  }
  
  @Test
  public void testParser_RegexCurvedBracketsEscaped() throws ParserException {
    List<Placeholder> tokens =
        parser.parse("* ? + [ ( ) { } ^ $ | \\ .", "* ? + [ ( ) \\{ \\} ^ $ | \\ .");
    assertEquals("Size of parsed array should be 0.", 0, tokens.size());
  }
  
  @Test
  public void testParser_RegexCurvedBracketsNotEscaped() throws ParserException {
    List<Placeholder> tokens =
        parser.parse("* ? + [ ( ) { } ^ $ | \\ .", "* ? + [ ( ) { } ^ $ | \\ .");
    assertEquals("Size of parsed array should be 0.", 0, tokens.size());
  }
}
