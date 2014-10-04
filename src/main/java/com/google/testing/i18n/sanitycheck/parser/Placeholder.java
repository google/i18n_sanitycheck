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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.testing.i18n.sanitycheck.checkers.Checker;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * An immutable object that represents a materialized sanity checkers placeholder like {date} or
 * {ignore}.
 * <p>
 * Contains following information:
 * <ul>
 * <li>name - which is date for {date} placeholder and etc.
 * <li>actualContent - actual value related to this placeholder.
 * <li>params - map of configuration settings for this specific placeholder. E.g. lenient=false pair
 * for {date:{lenient:false}}.
 * </ul>
 * <p>
 * The placeholder syntax is based on the simplyfied JSON and looks like this:
 * <ul>
 * <li>{PLACEHOLDER_NAME} is a simple placeholder with the name = PLACEHOLDER_NAME and without
 * additional parameters.
 * <li>{PLACEHOLDER_NAME:{PARAM1_NAME=PARAM1_VALUE}} is a placeholder with the name =
 * PLACEHOLDER_NAME and one additional parameter "PARAM1_NAME" with the value "PARAM1_VALUE". It is
 * also legal, but not necessary, to surround parameter names and values with "". <b>Having two
 * parameters with the same name is illegal.</b>
 * <li>{PLACEHOLDER_NAME:{PARAM1_NAME=PARAM1_VALUE,PARAM2_NAME=PARAM2_VALUE}} is an example of a
 * placeholder with multiple parameters.
 * </ul>
 * </p>
 */
public final class Placeholder {

  /**
   * An object that is a {@link Placeholder} builder. Think of it as a mutable {@link Placeholder}.
   */
  public static final class Builder {
    private String name;
    private String actualContent;
    private final Map<String, String> params = Maps.newHashMap();

    /**
     * Creates a new {@link Builder} with the given attributes.
     *
     * @param name of a placeholder
     * @param actualContent of a placeholder
     * @param params map of a placeholder
     */
    private Builder(String name, String actualContent, Map<String, String> params) {
      this.name = Preconditions.checkNotNull(name);
      this.actualContent = Preconditions.checkNotNull(actualContent);
      this.params.putAll(Preconditions.checkNotNull(params));
    }

    /**
     * Copy constructor that copies all data from a given placeholder.
     *
     * @param original is an instance of the {@link Placeholder} class
     */
    private Builder(Placeholder original) {
      this(original.name, original.actualContent, original.params);
    }

    /** Sets a name and returns this builder for chaining. */
    public Builder setName(String name) {
      this.name = Preconditions.checkNotNull(name);
      return this;
    }

    /** Sets an actual content and returns this builder for chaining. */
    public Builder setActualContent(String actualContent) {
      this.actualContent = Preconditions.checkNotNull(actualContent);
      return this;
    }

    /**
     * Puts all params from a given map to this placeholder and returns this builder for chaining.
     */
    public Builder putAllParams(Map<String, String> params) {
      this.params.putAll(Preconditions.checkNotNull(params));
      return this;
    }

    /** Deletes all params from this placeholder and returns this builder for chaining. */
    public Builder clearParams() {
      params.clear();
      return this;
    }

    /** Puts a given locale parameter to the placeholder and returns this builder for chaining. */
    public Builder putLocaleParam(String locale) {
      params.put(LOCALE_PARAM_NAME, Preconditions.checkNotNull(locale));
      return this;
    }

    /**
     * Puts a given isLenient parameter to the placeholder and returns this builder for chaining.
     */
    public Builder putLenientParam(boolean isLenient) {
      params.put(LENIENT_PARAM_NAME, String.valueOf(isLenient));
      return this;
    }

    /**
     * Puts a given isStrict parameter to the placeholder and returns this builder for chaining.
     */
    public Builder putStrictParam(boolean isStrict) {
      params.put(STRICT_PARAM_NAME, String.valueOf(isStrict));
      return this;
    }

    /**
     * Puts a given expectedValue parameter to the placeholder and returns this builder for
     * chaining.
     */
    public Builder putExpectedValueParam(String expectedValue) {
      params.put(VALUE_PARAM_NAME, Preconditions.checkNotNull(expectedValue));
      return this;
    }

    /**
     * Puts a given pattern parameter to the placeholder and returns this builder for chaining.
     */
    public Builder putPatternParam(String pattern) {
      params.put(PATTERN_PARAM_NAME, Preconditions.checkNotNull(pattern));
      return this;
    }

    /**
     * Puts a given skeleton parameter to the placeholder and returns this builder for chaining.
     */
    public Builder putSkeletonParam(String skeleton) {
      params.put(SKELETON_PARAM_NAME, Preconditions.checkNotNull(skeleton));
      return this;
    }

    /**
     * Puts a given splitter parameter to the placeholder and returns this builder for chaining.
     */
    public Builder putSplitterParam(String splitter) {
      params.put(SPLITTER_PARAM_NAME, Preconditions.checkNotNull(splitter));
      return this;
    }

    /** Build a {@link Placeholder} instance and returns it. */
    public Placeholder build() {
      return new Placeholder(name, actualContent, ImmutableMap.copyOf(params));
    }
  }

  /** Params that are accessible by means of getters in the {@link Placeholder} class. */
  private static final String LENIENT_PARAM_NAME = "lenient";
  private static final String LOCALE_PARAM_NAME = "locale";
  private static final String SKELETON_PARAM_NAME = "skeleton";
  private static final String SPLITTER_PARAM_NAME = "splitter";
  private static final String STRICT_PARAM_NAME = "strict";
  private static final String PATTERN_PARAM_NAME = "pattern";
  private static final String VALUE_PARAM_NAME = "value";

  private static final Gson GSON = new Gson();

  private final String name;
  private final String actualContent;
  private final ImmutableMap<String, String> params;

  /**
   * Creates a placeholder token from a given {@code tokenContent}. Could also return null if given
   * {@code tokenContext} does not satisfies the placeholder format. Default visibility, because
   * normally, should be used only by {@link Parser} class.
   *
   * @param tokenContent text representation of the placeholder
   * @param actualContent content of a placeholder in an expected string
   * @return instance of the {@link Placeholder}
   */
  @Nullable
  static final Placeholder buildPlaceholderToken(String tokenContent, String actualContent) {
    tokenContent = tokenContent.trim();
    // Should never be false.
    Preconditions.checkArgument(tokenContent.startsWith("{") && tokenContent.endsWith("}"));
    String placeholderName = null;
    Map<String, String> params = Maps.newHashMap();
    // Checks if pattern is complex. Looks like {placeholder:{param:value}}.
    try {
      if (tokenContent.contains(":")) {
        Type stringMap = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        Map<String, Map<String, String>> parsed = GSON.fromJson(tokenContent, stringMap);
        placeholderName = parsed.keySet().iterator().next();
        params = parsed.get(placeholderName);
      } else {
        placeholderName = tokenContent.substring(1, tokenContent.length() - 1);
        if (!placeholderName.equals(placeholderName.trim())) {
          return null;
        }
      }
    } catch (JsonSyntaxException e) {
      // Means that the token does not satisfy the placeholder syntax. Returns null.
      return null;
    }
    // Returns a valid placeholder only if a related checker exists.
    if (Checker.CHECKERS.containsKey(placeholderName)) {
      return Placeholder.builder(placeholderName, actualContent, params).build();
    } else {
      return null;
    }

  }

  /**
   * Creates new {@link Builder} instance with given parameters. If that is all you need, then just
   * call {@link Builder#build} after this method.
   *
   * @param name of a placeholder
   * @param actualContent of a placeholder
   * @return instance of the {@link Builder}
   */
  public static Builder builder(String name, String actualContent) {
    return builder(name, actualContent, Collections.<String, String>emptyMap());
  }

  /**
   * Creates new {@link Builder} instance with given parameters. If that is all you need, then just
   * call {@link Builder#build} after this method.
   *
   * @param name of a placeholder
   * @param actualContent of a placeholder
   * @param params map of a placeholder
   * @return instance of the {@link Builder}
   */
  public static Builder builder(String name, String actualContent, Map<String, String> params) {
    return new Builder(name, actualContent, params);
  }

  /**
   * Creates new {@link Builder} instance from a given {@link Placeholder} instance. If that is all
   * you need, then just call {@link Builder#build} after this method.
   *
   * @param original is a {@link Placeholder} instance that you want to use as a template for a new
   *        {@link Placeholder}
   * @return instance of the {@link Builder}
   */
  public static Builder builder(Placeholder original) {
    return new Builder(original);
  }

  private Placeholder(String name, String actualContent) {
    this(name, actualContent, ImmutableMap.<String, String>of());
  }

  private Placeholder(String name, String actualContent, ImmutableMap<String, String> params) {
    this.name = Preconditions.checkNotNull(name);
    this.actualContent = Preconditions.checkNotNull(actualContent);
    this.params = Preconditions.checkNotNull(params);
  }

  public String getActualContent() {
    return actualContent;
  }

  public String getName() {
    return name;
  }

  /**
   * Returns the value of the 'locale' flag in the given {@link Placeholder} instance. Useful if you
   * have a mixture of values formatted for different locales in your test case. a
   *
   * @return any value of the 'locale' flag within the give {@code target}
   */
  @Nullable
  public final String getLocale() {
    return params.get(LOCALE_PARAM_NAME);
  }

  /**
   * Returns a value of the 'lenient' flag in a given {@link Placeholder} instance. Switches off the
   * lenient mode for some sanity checkers.
   *
   * @return any value of the 'lenient' flag within a given {@code target}. If value was not
   *         specified, then default value 'true' would be returned
   */
  public final boolean isLenient() {
    if (params.containsKey(LENIENT_PARAM_NAME)) {
      return Boolean.valueOf(params.get(LENIENT_PARAM_NAME));
    }
    return true;
  }

  /**
   * Returns a value of the 'strict' flag in a given {@link Placeholder} instance. Some sanity
   * checkers have additional "strict" mode, which is stricter than just not lenient. Normally, if
   * both strict and lenient modes are on, then strict is ignored.
   *
   * @return any value of the 'strict' flag within a give {@code target}. If value was not
   *         specified, then default value 'false' would be returned
   */
  public final boolean isStrict() {
    return Boolean.valueOf(params.get(STRICT_PARAM_NAME));
  }

  /**
   * Returns the value of the 'value' flag in the given {@link Placeholder} instance. Some sanity
   * checkers allow you to set an expected value as well. For example number sanity checker can
   * check if "forty two" is a correct English number, but if you want to make sure that it is also
   * equals to 42, just use this parameter.
   *
   * @return any value of the 'value' flag within the give {@code target}
   */
  @Nullable
  public final String getExpectedValue() {
    return params.get(VALUE_PARAM_NAME);
  }

  /**
   * Returns a value of the 'pattern' flag in the given {@link Placeholder} instance. Currently
   * pattern is only used in the date time sanity checkers. It describes a pattern that satisfies an
   * actual date or time.
   *
   * @return any value of the 'pattern' flag within the give {@code target}
   */
  @Nullable
  public final String getPattern() {
    return params.get(PATTERN_PARAM_NAME);
  }

  /**
   * Returns a value of the 'skeleton' flag in the given {@link Placeholder} instance. Currently
   * skeleton is only used in the date time sanity checkers. It describes a skeleton that satisfies
   * an actual date or time. Read more about skeletons at
   * {@link com.ibm.icu.text.DateTimePatternGenerator#getBestPattern}.
   *
   * @return any value of the 'skeleton' flag within the give {@code target}
   */
  @Nullable
  public final String getSkeleton() {
    return params.get(SKELETON_PARAM_NAME);
  }

  /**
   * Returns the value of the 'splitter' flag in the given {@link Placeholder} instance. It is a
   * common parameter for multivalue checkers. By means of this parameter, an actual string will be
   * tokenized.
   *
   * @return any value of the 'splitter' flag within the give {@code target}
   */
  @Nullable
  public final String getSplitter() {
    return params.get(SPLITTER_PARAM_NAME);
  }
}
