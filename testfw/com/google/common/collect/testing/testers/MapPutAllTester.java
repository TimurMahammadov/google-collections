/*
 * Copyright (C) 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.common.collect.testing.testers;

import com.google.common.collect.testing.AbstractMapTester;
import com.google.common.collect.testing.MinimalCollection;
import com.google.common.collect.testing.features.CollectionSize;
import static com.google.common.collect.testing.features.CollectionSize.ZERO;
import com.google.common.collect.testing.features.MapFeature;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_KEYS;
import static com.google.common.collect.testing.features.MapFeature.ALLOWS_NULL_VALUES;
import static com.google.common.collect.testing.features.MapFeature.SUPPORTS_PUT_ALL;

import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A generic JUnit test which tests {@code putAll} operations on a map. Can't be
 * invoked directly; please see
 * {@link com.google.common.collect.testing.MapTestSuiteBuilder}.
 *
 * @author Chris Povirk
 * @author Kevin Bourrillion
 */
@SuppressWarnings("unchecked") // too many "unchecked generic array creations"
public class MapPutAllTester<K, V> extends AbstractMapTester<K, V> {
  private List<Entry<K, V>> containsNullKey;
  private List<Entry<K, V>> containsNullValue;

  @Override protected void setUp() throws Exception {
    super.setUp();
    containsNullKey = singletonList(entry(null, samples.e3.getValue()));
    containsNullValue = singletonList(entry(samples.e3.getKey(), null));
  }

  @MapFeature.Require(SUPPORTS_PUT_ALL)
  public void testPutAll_supportedNothing() {
    getMap().putAll(emptyMap());
    expectUnchanged();
  }

  @MapFeature.Require(absent = SUPPORTS_PUT_ALL)
  public void testPutAll_unsupportedNothing() {
    try {
      getMap().putAll(emptyMap());
    } catch (UnsupportedOperationException tolerated) {
    }
    expectUnchanged();
  }

  @MapFeature.Require(SUPPORTS_PUT_ALL)
  public void testPutAll_supportedNonePresent() {
    putAll(createDisjointCollection());
    expectAdded(samples.e3, samples.e4);
  }

  @MapFeature.Require(absent = SUPPORTS_PUT_ALL)
  public void testPutAll_unsupportedNonePresent() {
    try {
      putAll(createDisjointCollection());
      fail("putAll(nonePresent) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
    expectMissing(samples.e3, samples.e4);
  }

  @MapFeature.Require(SUPPORTS_PUT_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testPutAll_supportedSomePresent() {
    putAll(MinimalCollection.of(samples.e3, samples.e0));
    expectAdded(samples.e3);
  }

  @MapFeature.Require(absent = SUPPORTS_PUT_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testPutAll_unsupportedSomePresent() {
    try {
      putAll(MinimalCollection.of(samples.e3, samples.e0));
      fail("putAll(somePresent) should throw");
    } catch (UnsupportedOperationException expected) {
    }
    expectUnchanged();
  }

  @MapFeature.Require(absent = SUPPORTS_PUT_ALL)
  @CollectionSize.Require(absent = ZERO)
  public void testPutAll_unsupportedAllPresent() {
    try {
      putAll(MinimalCollection.of(samples.e0));
    } catch (UnsupportedOperationException tolerated) {
    }
    expectUnchanged();
  }

  @MapFeature.Require({SUPPORTS_PUT_ALL,
      ALLOWS_NULL_KEYS})
  public void testPutAll_nullKeySupported() {
    putAll(containsNullKey);
    expectAdded(containsNullKey.get(0));
  }

  @MapFeature.Require(value = SUPPORTS_PUT_ALL,
      absent = ALLOWS_NULL_KEYS)
  public void testAdd_nullKeyUnsupported() {
    try {
      putAll(containsNullKey);
      fail("putAll(containsNullKey) should throw");
    } catch (NullPointerException expected) {
    }
    expectUnchanged();
    expectNullKeyMissingWhenNullKeysUnsupported(
        "Should not contain null key after unsupported " +
        "putAll(containsNullKey)");
  }

  @MapFeature.Require({SUPPORTS_PUT_ALL,
      ALLOWS_NULL_VALUES})
  public void testPutAll_nullValueSupported() {
    putAll(containsNullValue);
    expectAdded(containsNullValue.get(0));
  }

  @MapFeature.Require(value = SUPPORTS_PUT_ALL,
      absent = ALLOWS_NULL_VALUES)
  public void testAdd_nullValueUnsupported() {
    try {
      putAll(containsNullValue);
      fail("putAll(containsNullValue) should throw");
    } catch (NullPointerException expected) {
    }
    expectUnchanged();
    expectNullValueMissingWhenNullValuesUnsupported(
        "Should not contain null value after unsupported " +
        "putAll(containsNullValue)");
  }

  @MapFeature.Require(SUPPORTS_PUT_ALL)
  public void testPutAll_nullCollectionReference() {
    try {
      getMap().putAll(null);
      fail("putAll(null) should throw NullPointerException");
    } catch (NullPointerException expected) {
    }
  }

  private Map<K, V> emptyMap() {
    return Collections.emptyMap();
  }

  private void putAll(Iterable<Entry<K, V>> entries) {
    Map<K, V> map = new LinkedHashMap<K, V>();
    for (Entry<K, V> entry : entries) {
      map.put(entry.getKey(), entry.getValue());
    }
    getMap().putAll(map);
  }
}
