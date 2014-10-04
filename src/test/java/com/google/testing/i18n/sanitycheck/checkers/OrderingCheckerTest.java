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

import com.ibm.icu.text.Collator;
import com.ibm.icu.util.ULocale;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/** Tests the {@link OrderingChecker} class functionality. */
@RunWith(JUnit4.class)
public class OrderingCheckerTest extends TestCase {

  private final OrderingChecker checker = new OrderingChecker();
  private final Collator mockCollator = mock(Collator.class);

  @After
  @Override
  public void tearDown() {
    reset(mockCollator);
  }

  @Test
  public void testCheck_Simple() {
    when(mockCollator.compare("a", "b")).thenReturn(-1);
    when(mockCollator.compare("b", "c")).thenReturn(-1);
    when(mockCollator.compare("c", "d")).thenReturn(-1);

    checker.makeCheck(ImmutableList.of(mockCollator), ImmutableList.of("a", "b", "c", "d"),
        ULocale.CANADA, null);

    verify(mockCollator).compare("a", "b");
    verify(mockCollator).compare("b", "c");
    verify(mockCollator).compare("c", "d");
  }

  @Test
  public void testCheck_SimilarElements() {
    when(mockCollator.compare("a", "a")).thenReturn(0);
    when(mockCollator.compare("a", "a")).thenReturn(0);
    when(mockCollator.compare("a", "b")).thenReturn(-1);

    checker.makeCheck(ImmutableList.of(mockCollator), ImmutableList.of("a", "a", "a", "b"),
        ULocale.CANADA, null);

    verify(mockCollator, times(2)).compare("a", "a");
    verify(mockCollator).compare("a", "b");
  }

  @Test
  public void testCheck_Invalid() {
    when(mockCollator.compare("a", "b")).thenReturn(-1);
    when(mockCollator.compare("b", "d")).thenReturn(-1);
    when(mockCollator.compare("d", "c")).thenReturn(1);

    boolean failed = false;
    try {
      checker.makeCheck(ImmutableList.of(mockCollator), ImmutableList.of("a", "b", "d", "c", "e"),
          ULocale.CANADA, null);
    } catch (AssertionError e) {
      failed = true;
    }
    assertTrue("Case should fail.", failed);
    verify(mockCollator).compare("a", "b");
    verify(mockCollator).compare("b", "d");
    verify(mockCollator).compare("d", "c");
  }
}
