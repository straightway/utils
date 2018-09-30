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
import straightway.error.Panic
import straightway.testing.flow.Equal
import straightway.testing.flow.Throw
import straightway.testing.flow.does
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class ComparableHelpersTest {

    private data class TestComparable(val i: Int) : Comparable<TestComparable> {
        override fun compareTo(other: TestComparable) = i.compareTo(other.i)
    }

    @Test
    fun `min returns smallest item`() =
            expect(min(TestComparable(1), TestComparable(2), TestComparable(3))
                           is_ Equal to_ TestComparable(1))

    @Test
    fun `max returns greatest item`() =
            expect(max(TestComparable(1), TestComparable(2), TestComparable(3))
                           is_ Equal to_ TestComparable(3))

    @Test
    fun `extreme without arguments panics`() =
            expect({ extreme<TestComparable> { _, _ -> true } } does Throw.type<Panic>())

    @Test
    fun `extreme returns the item which is ahead of all others`() =
            expect(extreme(TestComparable(1), TestComparable(2), TestComparable(3)) {
                a, _ -> a == TestComparable(2)
            } is_ Equal to_ TestComparable(2))

    @Test
    fun `extreme true returns first item`() =
            expect(extreme(TestComparable(1), TestComparable(2), TestComparable(3)) {
                _, _ -> true
            } is_ Equal to_ TestComparable(1))

    @Test
    fun `extreme false returns last item`() =
            expect(extreme(TestComparable(1), TestComparable(2), TestComparable(3)) {
                _, _ -> false
            } is_ Equal to_ TestComparable(3))
}