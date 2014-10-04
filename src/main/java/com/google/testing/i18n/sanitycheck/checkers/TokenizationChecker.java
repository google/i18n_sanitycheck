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

import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.util.ULocale;

import org.junit.Assert;

import java.util.Arrays;

/**
 * An object that validates the correctness of a string's tokenization. Based on ICU
 * {@link BreakIterator} class and inherits all its weaknesses. Don't use for ko locale because
 * {@link BreakIterator} is not working correctly for this locale.
 */
public class TokenizationChecker extends CommonTokenizationChecker {

  @Override
  protected void makeCheck(Placeholder target, ImmutableList<String> tokenizedInput, ULocale locale,
      String message) {
    StringBuilder untokenizedString = new StringBuilder();
    String expected = target.getExpectedValue();
    if (expected.isEmpty()) {
      for (String token : tokenizedInput) {
        untokenizedString.append(token);
      }
    } else {
      untokenizedString.append(expected);
    }

    ImmutableList<String> tokensFromICU =
        getTokens(untokenizedString.toString(), BreakIterator.getWordInstance(locale));
    String errorMessage = message != null ? message
        : String.format(
            "The tokenization %s for the text \"%s\" doesn't appear to be valid for %s."
            + "Should be more like: %s", Arrays.toString(tokenizedInput.toArray(new String[0])),
            untokenizedString.toString(), locale.getDisplayLanguage(),
            Arrays.toString(tokensFromICU.toArray(new String[0])));
    Assert.assertEquals(errorMessage, tokensFromICU, tokenizedInput);
  }

  /**
   * Tokenizes the given text using the {@link BreakIterator}. If the language is not supported or
   * the text cannot be tokenized it is returned as is.
   *
   * @param text The text to be tokenized
   * @param breakIterator is an instance of the {@link BreakIterator} class you want to use for
   *        tokenization
   * @return list of tokens extracted from the given text
   */
  @VisibleForTesting
  ImmutableList<String> getTokens(String text, BreakIterator breakIterator) {
    breakIterator.setText(text);
    ImmutableList.Builder<String> result = ImmutableList.builder();
    int start = breakIterator.first();
    for (int end = breakIterator.next(); end != BreakIterator.DONE; start = end,
        end = breakIterator.next()) {
      result.add(text.substring(start, end));
    }
    return result.build();
  }
}
