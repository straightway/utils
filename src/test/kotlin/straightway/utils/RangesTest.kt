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
import straightway.testing.bdd.Given
import straightway.testing.flow.Empty
import straightway.testing.flow.Equal
import straightway.testing.flow.Values
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class RangesTest {

    @Test
    fun `construct Ranges of given values`() =
            expect(Ranges(1 crangeTo 2, 3 crangeTo 4) is_ Equal to_
                    Values(1 crangeTo 2, 3 crangeTo 4))

    @Test
    fun `construct empty Ranges`() =
            expect(Ranges<Int>() is_ Empty)

    @Test
    fun `construct from iterable`() =
            expect(Ranges(listOf(1 crangeTo 2, 4 crangeTo 5).asIterable()) is_ Equal
                    to_ Values(1 crangeTo 2, 4 crangeTo 5))

    @Test
    fun `toString yields proper presentation`() =
            expect(Ranges(listOf(1 crangeTo 2, 4 crangeTo 5)).toString() is_ Equal
                    to_ "Ranges[1..2, 4..5]")

    @Test
    fun `range is included in empty Ranges`() =
            Given {
                Ranges<Int>()
            } when_ {
                this += (1 crangeTo 2)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 2))
            }

    @Test
    fun `zero size range is ignored when including it`() =
            Given {
                Ranges<Int>()
            } when_ {
                this += (1 crangeTo 1)
            } then {
                expect(this is_ Empty)
            }

    @Test
    fun `range intersecting with existing ranges are merged`() =
        Given {
            Ranges(1 crangeTo 3, 4 crangeTo 6)
        } when_ {
            this += (2 crangeTo 5)
        } then {
            expect(this is_ Equal to_ Values(1 crangeTo 6))
        }

    @Test
    fun `plusAssign range already included has no effect`() =
            Given {
                Ranges(1 crangeTo 4)
            } when_ {
                this += (2 crangeTo 3)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 4))
            }

    @Test
    fun `union of two Ranges objects`() =
            expect(Ranges(1 crangeTo 3, 9 crangeTo 10) + Ranges(2 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 4, 9 crangeTo 10))

    @Test
    fun `union of Ranges object with single range`() =
            expect(Ranges(1 crangeTo 3, 9 crangeTo 10) + (2 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 4, 9 crangeTo 10))

    @Test
    fun `including a set of ranges includes all contained ranges`() =
            Given {
                Ranges(1 crangeTo 2)
            } when_ {
                this += listOf(3 crangeTo 4, 5 crangeTo 6)
            } then {
                expect(this is_ Equal to_
                        Values(1 crangeTo 2, 3 crangeTo 4, 5 crangeTo 6))
            }

    @Test
    fun `minusAssign single existing range yields empty Ranges`() =
            Given {
                Ranges(1 crangeTo 4)
            } when_ {
                this -= (0 crangeTo 5)
            } then {
                expect(this is_ Empty)
            }

    @Test
    fun `minusAssign zero size range has no effect`() =
            Given {
                Ranges(1 crangeTo 4)
            } when_ {
                this -= (3 crangeTo 3)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 4))
            }

    @Test
    fun `minusAssign non intersecting range has no effect`() =
            Given {
                Ranges(1 crangeTo 4)
            } when_ {
                this -= (5 crangeTo 6)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 4))
            }

    @Test
    fun `minusAssign intersection from single existing range`() =
            Given {
                Ranges(1 crangeTo 4)
            } when_ {
                this -= (2 crangeTo 5)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 2))
            }

    @Test
    fun `minusAssign intersection from span of existing ranges`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 5, 6 crangeTo 9)
            } when_ {
                this -= (2 crangeTo 7)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 2, 7 crangeTo 9))
            }

    @Test
    fun `minusAssign exact range`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 5, 6 crangeTo 9)
            } when_ {
                this -= (4 crangeTo 5)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 3, 6 crangeTo 9))
            }

    @Test
    fun `minusAssign from second range`() =
            Given {
                Ranges(4 crangeTo 7, 9 crangeTo 14)
            } when_ {
                minusAssign(8 crangeTo 12)
            } then {
                expect(this is_ Equal to_ Values(4 crangeTo 7, 12 crangeTo 14))
            }

    @Test
    fun `excluding a set of ranges includes all contained ranges`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 9)
            } when_ {
                this -= (listOf(2 crangeTo 5, 8 crangeTo 10))
            } then {
                expect(this is_ Equal to_
                        Values(1 crangeTo 2, 5 crangeTo 8))
            }

    @Test
    fun `difference of two Ranges objects`() =
            expect(Ranges(1 crangeTo 3) - Ranges(2 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 2))

    @Test
    fun `difference of a ranges object from a single range`() =
            expect(Ranges(1 crangeTo 3) - (2 crangeTo 4) is_
                    Equal to_ Values(1 crangeTo 2))
    @Test
    fun `divAssign of disjoint sets is empty`() =
            Given {
                Ranges(1 crangeTo 2)
            } when_ {
                this /= (3 crangeTo 4)
            } then {
                expect(this is_ Empty)
            }

    @Test
    fun `divAssign of fully overlapping is identity`() =
            Given {
                Ranges(1 crangeTo 2)
            } when_ {
                this /= (0 crangeTo 4)
            } then {
                expect(this is_ Equal to_ Values(1 crangeTo 2))
            }

    @Test
    fun `divAssign of partly overlapping range`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 6, 8 crangeTo 9)
            } when_ {
                this /= (2 crangeTo 5)
            } then {
                expect(this is_ Equal to_ Values(2 crangeTo 3, 4 crangeTo 5))
            }

    @Test
    fun `divAssign with ranges collection computes intersections`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 6, 8 crangeTo 10)
            } when_ {
                this /= listOf(2 crangeTo 5, 8 crangeTo 9)
            } then {
                expect(this is_ Equal to_ Values(2 crangeTo 3, 4 crangeTo 5, 8 crangeTo 9))
            }

    @Test
    fun `div with ranges collection computes intersections`() =
            Given {
                Ranges(1 crangeTo 3, 4 crangeTo 6, 8 crangeTo 10)
            } when_ {
                this /= listOf(2 crangeTo 5, 8 crangeTo 9)
            } then {
                expect(this is_ Equal to_ Values(2 crangeTo 3, 4 crangeTo 5, 8 crangeTo 9))
            }

    @Test
    fun `intersection with last range fully included`() =
            Given {
                Ranges(1 crangeTo 3, 8 crangeTo 10, 4 crangeTo 6)
            } when_ {
                this /= (8 crangeTo 9)
            } then {
                expect(this is_ Equal to_ Values(8 crangeTo 9))
            }

    @Test
    fun `size equals size of ranges`() =
            Given {
                Ranges(1 crangeTo 3, 8 crangeTo 10, 4 crangeTo 6)
            } when_ {
                size
            } then {
                expect(it.result is_ Equal to_ 3)
            }

    private infix fun <T : Comparable<T>> T.crangeTo(other: T) = this..other
}