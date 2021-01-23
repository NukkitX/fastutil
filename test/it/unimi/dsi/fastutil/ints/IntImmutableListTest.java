/*
 * Copyright (C) 2017-2020 Sebastiano Vigna
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

package it.unimi.dsi.fastutil.ints;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

@SuppressWarnings("static-method")
public class IntImmutableListTest {

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEmptyListIsDifferentFromEmptySet() {
		assertFalse(IntLists.EMPTY_LIST.equals(IntSets.EMPTY_SET));
		assertFalse(IntSets.EMPTY_SET.equals(IntLists.EMPTY_LIST));
	}


	@Test
	public void testConstructors() {
		final int[] a = new int[] { 0, 1, 2 };
		assertEquals(IntArrayList.wrap(a), new IntImmutableList(a));
		assertEquals(IntArrayList.wrap(a), new IntImmutableList(a, 0, a.length));
	}

	@Test
	public void testOf() {
		final IntImmutableList l = IntImmutableList.of(0, 1, 2);
		assertEquals(IntArrayList.wrap(new int[] { 0, 1, 2 }), l);
		final int[] a = new int[] { 0, 1, 2 };
		assertEquals(IntArrayList.wrap(a), IntImmutableList.of(a));
	}

	@Test
	public void testOfEmpty() {
		final IntImmutableList l = IntImmutableList.of();
		assertTrue(l.isEmpty());
	}

	@Test
	public void testOfSingleton() {
		final IntImmutableList l = IntImmutableList.of(0);
		assertEquals(IntArrayList.wrap(new int[] { 0 }), l);
	}


	@Test
	public void testToList() {
		final IntImmutableList baseList = IntImmutableList.of(2, 380, 1297);
		// Also conveniently serves as a test of the intStream and spliterator.
		final IntImmutableList transformed = IntImmutableList.toList(baseList.intStream().map(i -> i + 40));
		assertEquals(IntImmutableList.of(42, 420, 1337), transformed);
	}
	
	@Test
	public void testEquals_AnotherImmutableList() {
		final IntImmutableList baseList = IntImmutableList.of(2, 380, 1297);
		assertEquals(IntImmutableList.of(2, 380, 1297), baseList);
		assertNotEquals(IntImmutableList.of(42, 420, 1337), baseList);
	}

	@Test
	public void testEquals_Sublist() {
		final IntImmutableList l1 = IntImmutableList.of(0, 1, 2, 3);
		final IntImmutableList l2 = IntImmutableList.of(5, 0, 1, 2, 3, 4);
		final IntList sl2 = l2.subList(1, 5);
		assertEquals(l1, sl2);
	}

	@Test
	public void testEquals_OtherListImpl() {
		final IntImmutableList baseList = IntImmutableList.of(2, 380, 1297);
		assertEquals(IntArrayList.of(2, 380, 1297), baseList);
		assertNotEquals(IntArrayList.of(42, 420, 1337), baseList);
	}

	@Test
	public void testSpliteratorTrySplit() {
		final IntImmutableList baseList = IntImmutableList.of(0, 1, 2, 3, 72, 5, 6);
		final IntSpliterator willBeSuffix = baseList.spliterator();
		assertEquals(baseList.size(), willBeSuffix.getExactSizeIfKnown());
		// Rather non-intuitively for finite sequences (but makes perfect sense for infinite ones),
		// the spec demands the original spliterator becomes the suffix and the new Spliterator becomes the prefix.
		final IntSpliterator prefix = willBeSuffix.trySplit();
		// No assurance of where we split, but where ever it is it should be a perfect split into a prefix and suffix.
		final java.util.stream.IntStream suffixStream = java.util.stream.StreamSupport.intStream(willBeSuffix, false);
		final java.util.stream.IntStream prefixStream = java.util.stream.StreamSupport.intStream(prefix, false);

		final IntImmutableList prefixList = IntImmutableList.toList(prefixStream);
		final IntImmutableList suffixList = IntImmutableList.toList(suffixStream);
		assertEquals(baseList.size(), prefixList.size() + suffixList.size());
		assertEquals(baseList.subList(0, prefixList.size()), prefixList);
		assertEquals(baseList.subList(prefixList.size(), baseList.size()), suffixList);
		final IntArrayList recombinedList = new IntArrayList(baseList.size());
		recombinedList.addAll(prefixList);
		recombinedList.addAll(suffixList);
		assertEquals(baseList, recombinedList);
	}

	@Test
	public void testSubList_testEquals_ImmutableList() {
		final IntImmutableList l = IntImmutableList.of(0, 1, 2, 3);
		final IntList sl = l.subList(0, 3);
		assertEquals(IntImmutableList.of(0, 1, 2), sl);
		assertNotEquals(IntImmutableList.of(0, 1, 3), sl);
	}

	@Test
	public void testSubList_testEquals_Sublist() {
		final IntImmutableList l1 = IntImmutableList.of(0, 1, 2, 3);
		final IntImmutableList l2 = IntImmutableList.of(5, 0, 1, 2, 3, 4);
		final IntList sl1 = l1.subList(0, 3);
		final IntList sl2 = l2.subList(1, 4);
		assertEquals(sl1, sl2);
		final IntList sl3 = l2.subList(0, 3);
		assertNotEquals(sl1, sl3);
	}

	@Test
	public void testSubList_testEquals_OtherListImpl() {
		final IntImmutableList l = IntImmutableList.of(0, 1, 2, 3);
		final IntList sl = l.subList(0, 3);
		assertEquals(IntArrayList.of(0, 1, 2), sl);
		assertNotEquals(IntArrayList.of(0, 1, 3), sl);
	}

	@Test
	public void testSubList_testToArray() {
		final IntImmutableList l = IntImmutableList.of(0, 1, 2, 3);
		final IntList sl = l.subList(0, 3);
		assertArrayEquals(new int[] { 0, 1, 2 }, sl.toIntArray());
	}
	
	@Test
	public void testSubList_testSubSubList() {
		final IntImmutableList l = IntImmutableList.of(0, 1, 2, 3, 4);
		final IntList sl = l.subList(1, 4);
		final IntList ssl = sl.subList(1, 2);
		assertEquals(IntImmutableList.of(1, 2, 3), sl);
		assertEquals(IntImmutableList.of(2), ssl);
	}

	@Test
	public void testSubList_testSpliteratorTrySplit() {
		final IntList baseList = IntImmutableList.of(0, 1, 2, 3, 72, 5, 6).subList(1, 5); // 1, 2, 3, 72
		final IntSpliterator willBeSuffix = baseList.spliterator();
		assertEquals(baseList.size(), willBeSuffix.getExactSizeIfKnown());
		// Rather non-intuitively for finite sequences (but makes perfect sense for infinite ones),
		// the spec demands the original spliterator becomes the suffix and the new Spliterator becomes the prefix.
		final IntSpliterator prefix = willBeSuffix.trySplit();
		// No assurance of where we split, but where ever it is it should be a perfect split into a prefix and suffix.
		final java.util.stream.IntStream suffixStream = java.util.stream.StreamSupport.intStream(willBeSuffix, false);
		final java.util.stream.IntStream prefixStream = java.util.stream.StreamSupport.intStream(prefix, false);

		final IntImmutableList prefixList = IntImmutableList.toList(prefixStream);
		final IntImmutableList suffixList = IntImmutableList.toList(suffixStream);
		assertEquals(baseList.size(), prefixList.size() + suffixList.size());
		assertEquals(baseList.subList(0, prefixList.size()), prefixList);
		assertEquals(baseList.subList(prefixList.size(), baseList.size()), suffixList);
		final IntArrayList recombinedList = new IntArrayList(baseList.size());
		recombinedList.addAll(prefixList);
		recombinedList.addAll(suffixList);
		assertEquals(baseList, recombinedList);
	}
}
