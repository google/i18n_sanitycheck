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

import com.google.common.collect.ImmutableList;
import com.google.testing.i18n.sanitycheck.parser.Placeholder;

import com.ibm.icu.util.ULocale;

import org.junit.Assert;

import java.util.List;

/**
 * An abstract class that provides the common logic for checkers that work with a list of tokenized
 * strings. It tokenizes an input string by means of the default or user provided delimiter, builds
 * a list from tokens and sends it to the abstract method {@link #makeCheck} for further validation.
 */
public abstract class CommonTokenizationChecker implements Checker {

  private static final ImmutableList<String> DEFAULT_SPLITTERS =
      ImmutableList.of(System.getProperty("line.separator"), "\\;", "\\,", "\\ ");

  @Override
  public void check(Placeholder target, ULocale locale, String message) {
    String splitter = target.getSplitter();
    String[] tokens = null;
    if (splitter != null) {
      tokens = target.getActualContent().split(splitter);
    } else {
      List<String> splitters = getSplitters();
      int longestLength = 0;
      for (int i = 0; i < splitters.size(); i++) {
        String[] tmpTokens = target.getActualContent().split(splitters.get(i));
        if (longestLength < tmpTokens.length) {
          longestLength = tmpTokens.length;
          tokens = tmpTokens;
        }
      }
    }
    String errorMessage = message != null ? message
        : "No possible splitters were detected. "
        + "Please specify splitter explicitly by means of placeholder parameter \"splitter\"";
    Assert.assertNotNull(errorMessage, tokens);

    makeCheck(target, ImmutableList.copyOf(tokens), locale, message);
  }

  /**
   * Returns the list of the string splitters for tokenization.
   *
   * @return should return the list of splitters actual for this checker
   */
  protected List<String> getSplitters() {
    return DEFAULT_SPLITTERS;
  }

  /**
   * Performs the sanity check of the list of the tokenized strings.
   *
   * @param target {@link Placeholder} instance that contains complete information about formatted
   *        string that should be checked and some modifiers
   * @param tokenizedInput list of strings to be checked
   */
  protected abstract void makeCheck(Placeholder target, ImmutableList<String> tokenizedInput,
      ULocale locale, String message);

}
