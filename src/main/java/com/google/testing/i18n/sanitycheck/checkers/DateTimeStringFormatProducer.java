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

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.ULocale;

/**
 * Producer of all possible {@link DateFormat}s for date and time.
 * See {@link DateFormat#getDateTimeInstance}.
 */
class DateTimeStringFormatProducer implements TimeDateChecker.DateFormatProducer {

  private static final ImmutableList<Integer> FORMATS = ImmutableList.of(DateFormat.NONE,
      DateFormat.SHORT,
      DateFormat.MEDIUM,
      DateFormat.LONG,
      DateFormat.FULL,
      DateFormat.RELATIVE_FULL,
      DateFormat.RELATIVE_LONG,
      DateFormat.RELATIVE_MEDIUM,
      DateFormat.RELATIVE_SHORT);

  @Override
  public ImmutableList<DateFormat> get(Placeholder target, ULocale locale) {
    ImmutableList.Builder<DateFormat> result = ImmutableList.builder();
    for (Integer dateType : FORMATS) {
      for (Integer timeType : FORMATS) {
        if (dateType == timeType && dateType == DateFormat.NONE) {
          continue;
        }
        DateFormat dateFormat = DateFormat.getDateTimeInstance(dateType, timeType, locale);
        dateFormat.setLenient(target.isLenient());
        result.add(dateFormat);
      }
    }
    return result.build();
  }
}
