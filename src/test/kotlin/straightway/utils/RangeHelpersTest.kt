/*
 * Copyright 2016 github.com/straightway
 *
 *  Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package straightway.utils

import org.junit.jupiter.api.Test
import straightway.testing.flow.Empty
import straightway.testing.flow.Equal
import straightway.testing.flow.False
import straightway.testing.flow.True
import straightway.testing.flow.Values
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class RangeHelpersTest {

    @Test
    fun `two disjoint ranges do not intersect`() =
            expect(1..2 intersectsWith 3..4 is_ False)

    @Test
    fun `two ranges intersect if the start of the second is within the first`() =
            expect(1..3 intersectsWith 2..4 is_ True)

    @Test
    fun `two ranges intersect if the end of the second is within the first`() =
            expect(1..3 intersectsWith 0..2 is_ True)

    @Test
    fun `two ranges intersect if the first fully contains the second`() =
            expect(1..4 intersectsWith 2..3 is_ True)

    @Test
    fun `two ranges intersect if the second fully contains the first`() =
            expect(2..3 intersectsWith 1..4 is_ True)

    @Test
    fun `two adjacent ranges intersect`() =
            expect(2..3 intersectsWith 3..4 is_ True)

    @Test
    fun `a range does not contain a disjoint range`() =
            expect((2 crangeTo 3).contains(4 crangeTo 5) is_ False)

    @Test
    fun `a range does contain a range it completely overlaps`() =
            expect((1 crangeTo 9).contains(4 crangeTo 5) is_ True)

    @Test
    fun `a range does not contain a range it only overlaps on the left`() =
            expect((3 crangeTo 9).contains(1 crangeTo 5) is_ False)

    @Test
    fun `a range does not contain a range it only overlaps on the right`() =
            expect((3 crangeTo 9).contains(5 crangeTo 12) is_ False)

    @Test
    fun `a range does not contain an empty range`() =
            expect((3 crangeTo 9).contains(5 crangeTo 4) is_ False)

    @Test
    fun `a range does not contain a single element range`() =
            expect((3 crangeTo 9).contains(5 crangeTo 5) is_ True)

    @Test
    @Suppress("EmptyRange", "InvalidRange")
    fun `empty range first does not intersect`() =
            expect(3..2 intersectsWith 1..4 is_ False)

    @Test
    @Suppress("EmptyRange", "InvalidRange")
    fun `empty range second does not intersect`() =
            expect(1..4 intersectsWith 3..2 is_ False)

    @Test
    fun `union of two disjoint ranges is a list of both`() =
            expect((1 crangeTo 2) u (3 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 2, 3 crangeTo 4))

    @Test
    fun `union of two equal ranges is a list containing one of them`() =
            expect((1 crangeTo 2) u (1 crangeTo 2) is_
                    Equal to_ Values(1 crangeTo 2))

    @Test
    fun `union of two intersecting ranges is a list containing the union of them 1`() =
            expect(1..3 u 2..4 is_
                    Equal to_ Values(1 crangeTo 4))

    @Test
    fun `union of two intersecting ranges is a list containing the union of them 2`() =
            expect((2 crangeTo 4) u (1 crangeTo 3) is_
                    Equal to_ Values(1 crangeTo 4))

    @Test
    fun `union of an empty with a non-empty range`() =
            expect((2 crangeTo 4) u (3 crangeTo 1) is_
                    Equal to_ Values(2 crangeTo 4))

    @Test
    fun `union of a non-empty with an empty range`() =
            expect((3 crangeTo 1) u (2 crangeTo 4) is_
                    Equal to_ Values(2 crangeTo 4))

    @Test
    fun `union of two empty ranges is empty`() =
            expect((3 crangeTo 1) u (4 crangeTo 2) is_ Empty)

    @Test
    fun `union of two Range collections`() =
            expect(listOf(1 crangeTo 2) u listOf(3 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 2, 3 crangeTo 4))

    @Test
    fun `intersection of two disjoint ranges is empty`() =
            expect((1 crangeTo 2) / (3 crangeTo 4) is_ Empty)

    @Test
    fun `intersection of two right overlapping ranges 1`() =
            expect((1 crangeTo 3) / (2 crangeTo 4) is_
                    Equal to_ Values(2 crangeTo 3))

    @Test
    fun `intersection of two left overlapping ranges 1`() =
            expect((1 crangeTo 3) / (0 crangeTo 2) is_
                    Equal to_ Values(1 crangeTo 2))

    @Test
    fun `intersection of two fully overlapping ranges 1`() =
            expect((1 crangeTo 3) / (0 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 3))

    @Test
    fun `intersection with left empty range is empty`() =
            expect((1 crangeTo 4) / (3 crangeTo 2) is_ Empty)

    @Test
    fun `intersection with right empty range is empty`() =
            expect((3 crangeTo 2) / (1 crangeTo 4) is_ Empty)

    @Test
    fun `difference from a disjoint range is identical`() =
            expect((3 crangeTo 7) - (8 crangeTo 9) is_ Equal to_ Values(3 crangeTo 7))

    @Test
    fun `difference from a range overlapping on the right`() =
            expect((3 crangeTo 7) - (5 crangeTo 9) is_ Equal to_ Values(3 crangeTo 5))

    @Test
    fun `difference from a range overlapping on the left`() =
            expect((3 crangeTo 7) - (1 crangeTo 5) is_ Equal to_ Values(5 crangeTo 7))

    @Test
    fun `difference from a completely overlapping range is empty`() =
            expect((3 crangeTo 7) - (1 crangeTo 9) is_ Empty)

    @Test
    fun `difference from a completely included range`() =
            expect((3 crangeTo 7) - (4 crangeTo 6) is_
                    Equal to_ Values(3 crangeTo 4, 6 crangeTo 7))

    @Test
    fun `difference from an equal range is empty`() =
            expect((3 crangeTo 7) - (3 crangeTo 7) is_ Empty)

    @Test
    fun `difference from a range of zero size is identical`() =
            expect((3 crangeTo 7) - (6 crangeTo 6) is_ Equal to_ Values(3 crangeTo 7))

    @Test
    fun `difference from an empty range is identical`() =
            expect((3 crangeTo 7) - (6 crangeTo 5) is_ Equal to_ Values(3 crangeTo 7))

    @Test
    fun `difference of two Range collections`() =
            expect(listOf(1 crangeTo 3) without listOf(2 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 2))

    // region Private

    private infix fun <T : Comparable<T>> T.crangeTo(other: T) = this..other

    // endregion
}