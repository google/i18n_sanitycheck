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

import com.google.common.collect.ImmutableList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An object that expects two related strings, actual and expected, as an input and produces
 * {@link Placeholder}s as a result.
 * <p>
 * <ul>
 * <li>actual is any string that is produced by a code user wants to test. It should satisfy a
 * pattern specified in the expected string. If not, then {@link ParserException} will be thrown.
 * <li>expected is a placeholder based pattern to parse the actual string.
 * </ul>
 * <p>
 * All of the placeholders in the expected string will be extracted, associated to the actual string
 * parts and returned as a result during the parsing procedure.
 * <p>
 * The syntax of the expected string is following:
 * <ul>
 * <li>It could be a simple string without any special markers, with static text only. In this case
 * parser just compares actual and expected strings and throws the {@link ParserException} if they
 * are not equal.
 * <li>It could be a static text mixed with one or more placeholders (Please read more about
 * placeholders syntax in the {@link Placeholder} class javadoc). In this case, the parser will try
 * to associate specific part of the actual message with any single placeholder from the expected
 * string. After that, static parts of the expected message are compared with related parts of the
 * actual message.
 * <li>Even if a part of the expected string satisfies a placeholder syntax, it will be recognized
 * as one if and only if this placeholder is listed in the
 * {@link com.google.testing.i18n.sanitycheck.checkers.Checker#CHECKERS} map. If not, then this part
 * of the expected text is interpreted as a static text.
 * <li>Regex constructions are legal in the expected string. They will be escaped and interpreted as
 * static text.
 * <li>It is also possible to escape { and } characters by means of \{ and \} constructions.
 * </ul>
 * <p>
 */
public class Parser {

  /** Precompiled regular expression to detect placeholders. */
  private static final Pattern PLACEHOLDER_PATTERN =
      Pattern.compile("((?<!\\\\)\\{.*?(?<!\\\\)\\}+)", Pattern.MULTILINE | Pattern.DOTALL);

  /** String representation of a lazy regex group that satisfies any character. */
  private static final String LAZY_GROUP = "(.*?)";

  /** String representation of a greedy regex group that satisfies any character. */
  private static final String GREEDY_GROUP = "(.*)";

  /**
   * Parses a given {@code expected} string and returns a list of the {@link Placeholder} instances
   * that were found.
   *
   * @param actual golden data string that should satisfy a given {@code expected} pattern.
   * @param expected pattern that describes a given {@code actual} data.
   * @return list of {@link Placeholder} entities.
   * @throws ParserException would be thrown if {@code expected} format is wrong or {@code actual}
   *         does not satisfies {@code expected} format
   */
  public ImmutableList<Placeholder> parse(String actual, String expected) throws ParserException {
    Matcher placeholderMatcher = PLACEHOLDER_PATTERN.matcher(expected);
    String extractionPatternText = buildExtractionPattern(placeholderMatcher, expected);
    placeholderMatcher.reset();
    ImmutableList<String> rawPlaceholders = extractPlaceholders(placeholderMatcher);
    Pattern extractionPattern = Pattern.compile(extractionPatternText);

    // Extracting actual values from the given expected result.
    Matcher extractionMatcher = extractionPattern.matcher(actual);
    int extractedGroups = extractionMatcher.groupCount();
    ImmutableList.Builder<Placeholder> placeholderTokens = ImmutableList.builder();
    if (extractionMatcher.find()) {
      if (extractedGroups != rawPlaceholders.size()) {
        // Should never happen. The number of groups is always equal to the number of placeholders.
        throw new ParserException(
            String.format("Actual result does not satisfies the expected one."));
      }
      for (int i = 1; i <= extractedGroups; i++) {
        String rawPlaceholder = rawPlaceholders.get(i - 1);
        String actualPlaceholderContent = extractionMatcher.group(i);
        if (rawPlaceholder.equals(actualPlaceholderContent)) {
          continue;
        }
        placeholderTokens.add(buildPlaceholder(rawPlaceholder, actualPlaceholderContent));
      }
      return placeholderTokens.build();
    }
    throw new ParserException(String.format("Actual result does not satisfies the expected one. "
        + "Actual text \"%s\" is not parsable by means of the generated pattern \"%s\"", actual,
        expected));
  }

  /**
   * Builds a regular expression based on a given expected string. This regular, if applied to an
   * actual string, produces a set of actual values related to any single placeholder in the
   * expected string.
   *
   * @param placeholderMatcher is a given expected string parsed by means of
   *        {@code PLACEHOLDER_PATTERN} pattern
   * @param expected pattern that describes a given {@code actual} data
   * @return a regular expression that could be used to parse an actual string
   */
  private String buildExtractionPattern(Matcher placeholderMatcher, String expected) {
    StringBuilder extractionPatternText = new StringBuilder("(?s)(?m)");
    int position = 0;
    while (placeholderMatcher.find()) {
      extractionPatternText.append(Pattern.quote(
          replaceEscapedCharacters(expected.substring(position, placeholderMatcher.start()))));
      if (placeholderMatcher.hitEnd()) {
        extractionPatternText.append(GREEDY_GROUP);
      } else {
        extractionPatternText.append(LAZY_GROUP);
      }

      position = placeholderMatcher.end();
    }
    extractionPatternText.append(
        Pattern.quote(replaceEscapedCharacters(expected.substring(position))));
    extractionPatternText.append("$");
    return extractionPatternText.toString();
  }

  /**
   * Goes through a parsed expected string, extracts all placeholders located there and combines
   * them to a result list in the same order as they appear in the expected string.
   *
   * @param placeholderMatcher is a given expected string parsed by means of
   *        {@code PLACEHOLDER_PATTERN} pattern
   * @return an immutable list of all the placeholders presented in the expected string
   */
  private ImmutableList<String> extractPlaceholders(Matcher placeholderMatcher) {
    ImmutableList.Builder<String> result = ImmutableList.builder();
    while (placeholderMatcher.find()) {
      result.add(replaceEscapedCharacters(placeholderMatcher.group()));
    }
    return result.build();
  }

  /**
   * Builds a {@link Placeholder} instance.
   *
   * @param rawPlaceholder is a string representation of a placeholder. E.g. {date:{lenient:false}}
   * @param placeholderContent is a string value from an actual string, that is related to this
   *        placeholder
   * @return new instance of {@link Placeholder}
   * @throws ParserException problem happened during the placeholder creation
   */
  private Placeholder buildPlaceholder(String rawPlaceholder, String placeholderContent)
      throws ParserException {
    Placeholder placeholder = Placeholder.buildPlaceholderToken(rawPlaceholder, placeholderContent);
    if (placeholder != null) {
      return placeholder;
    }
    throw new ParserException(String.format("Actual result doesn't satisfy an expected one. "
        + "The placeholder \"%s\" is not valid within the system, and the related text \"%s\" "
        + "is not equals to the \"%s\"", rawPlaceholder, placeholderContent, rawPlaceholder));
  }

  /**
   * Replaces '\{' with '{' and '\}' with '}' in a given string.
   *
   * @param toReplace string than may contain elements to replace
   * @return string with replaced elements
   */
  private String replaceEscapedCharacters(String toReplace) {
    return toReplace.replace("\\{", "{").replace("\\}", "}");
  }
}
