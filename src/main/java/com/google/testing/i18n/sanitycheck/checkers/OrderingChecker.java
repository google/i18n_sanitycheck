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

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import com.ibm.icu.util.ULocale;

import org.junit.Assert;

/** An object that performs sanity check of the sorting order of a given list of strings. */
public class OrderingChecker extends CommonTokenizationChecker {

  @Override
  protected void makeCheck(Placeholder target, ImmutableList<String> tokenizedInput, ULocale locale,
      String message) {
    if (tokenizedInput.size() < 2) {
      // Test passed. Nothing to sort.
      return;
    }

    int strength = Collator.SECONDARY;
    if (!target.isLenient()) {
      if (!target.isStrict()) {
        strength = Collator.TERTIARY;
      } else {
        strength = Collator.IDENTICAL;
      }
    }
    // Init collators.
    Collator collator = Collator.getInstance(locale);
    ImmutableList.Builder<Collator> collators = ImmutableList.builder();
    collators.add(collator);
    try {
      Collator collator2 = (RuleBasedCollator) collator.clone();
      collator2.setStrength(strength);
      collators.add(collator2);
      RuleBasedCollator collator3 = (RuleBasedCollator) collator.clone();
      collator3.setStrength(strength);
      collator3.setAlternateHandlingShifted(true);
      collators.add(collator3);
      RuleBasedCollator collator4 = (RuleBasedCollator) collator.clone();
      collator4.setStrength(strength);
      collator4.setNumericCollation(true);
      collators.add(collator4);
      RuleBasedCollator collator5 = (RuleBasedCollator) collator.clone();
      collator5.setStrength(strength);
      collator5.setAlternateHandlingShifted(true);
      collator5.setNumericCollation(true);
      collators.add(collator5);
    } catch (CloneNotSupportedException e) {
      // Do nothing.
    }
    makeCheck(collators.build(), tokenizedInput, locale, message);
  }

  @VisibleForTesting
  void makeCheck(ImmutableList<Collator> collators, ImmutableList<String> tokenizedInput,
      ULocale locale, String message) {
    int position = 0;
    boolean failed = false;
    if (tokenizedInput != null) {
      main: for (int i = 1; i < tokenizedInput.size(); i++) {
        position = i;
        for (Collator testCollator : collators) {
          if (testCollator.compare(tokenizedInput.get(i - 1), tokenizedInput.get(i)) <= 0) {
            continue main;
          }
        }
        failed = true;
        break;
      }
    }
    String errorMessage = message != null ? message
        : String.format("List is not sorted for %s. Should have \"%s\" <= \"%s\" at position %s.",
            locale, tokenizedInput.get(position), tokenizedInput.get(position - 1), position);
    Assert.assertFalse(errorMessage, failed);
  }
}
