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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;

import com.ibm.icu.text.BreakIterator;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

/** Test class for {@link TokenizationChecker} */
@RunWith(JUnit4.class)
public class TokenizationCheckerTest extends TestCase {

  private final TokenizationChecker checker = new TokenizationChecker();
  private final BreakIterator mockBreakIterator = mock(BreakIterator.class);

  @After
  @Override
  public void tearDown() {
    reset(mockBreakIterator);
  }

  @Test
  public void testCheck_Simple() {
    when(mockBreakIterator.first()).thenReturn(0);
    when(mockBreakIterator.next())
    .thenReturn(1)
    .thenReturn(2)
    .thenReturn(3)
    .thenReturn(BreakIterator.DONE);
    String toTokenize = "123";
    List<String> result = checker.getTokens(toTokenize, mockBreakIterator);
    assertEquals(ImmutableList.of("1", "2", "3"), result);
    verify(mockBreakIterator).setText(toTokenize);
    verify(mockBreakIterator).first();
    verify(mockBreakIterator, times(4)).next();
  }

  @Test
  public void testCheck_OneToken() {
    when(mockBreakIterator.first()).thenReturn(0);
    when(mockBreakIterator.next()).thenReturn(3).thenReturn(BreakIterator.DONE);
    String toTokenize = "123";
    List<String> result = checker.getTokens(toTokenize, mockBreakIterator);
    assertEquals(ImmutableList.of("123"), result);
    verify(mockBreakIterator).setText(toTokenize);
    verify(mockBreakIterator).first();
    verify(mockBreakIterator, times(2)).next();
  }

  @Test
  public void testCheck_EmptyString() {
    when(mockBreakIterator.first()).thenReturn(0);
    when(mockBreakIterator.next()).thenReturn(0).thenReturn(BreakIterator.DONE);
    String toTokenize = "";
    List<String> result = checker.getTokens(toTokenize, mockBreakIterator);
    assertEquals(ImmutableList.of(""), result);
    verify(mockBreakIterator).setText(toTokenize);
    verify(mockBreakIterator).first();
    verify(mockBreakIterator, times(2)).next();
  }

  @Test
  public void testCheck_DifferentLengthTokens() {
    when(mockBreakIterator.first()).thenReturn(0);
    when(mockBreakIterator.next())
    .thenReturn(1)
    .thenReturn(3)
    .thenReturn(5)
    .thenReturn(BreakIterator.DONE);
    String toTokenize = "12345";
    List<String> result = checker.getTokens(toTokenize, mockBreakIterator);
    assertEquals(ImmutableList.of("1", "23", "45"), result);
    verify(mockBreakIterator).setText(toTokenize);
    verify(mockBreakIterator).first();
    verify(mockBreakIterator, times(4)).next();
  }
}
