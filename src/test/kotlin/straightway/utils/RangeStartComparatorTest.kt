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
import straightway.testing.flow.Equal
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class RangeStartComparatorTest {

    @Test
    fun `compare null ranges`() =
            expect(RangeStartComparator<Int>().compare(null, null) is_ Equal to_ 0)

    @Test
    fun `compare null range with non-null range`() =
            expect(RangeStartComparator<Int>().compare(null, 1..2) is_ Equal to_ 1)

    @Test
    fun `compare non-null range with null range`() =
            expect(RangeStartComparator<Int>().compare(1..2, null) is_ Equal to_ -1)

    @Test
    fun `compare two non-null ranges with equal start`() =
            expect(RangeStartComparator<Int>().compare(1..2, 1..3) is_ Equal to_ 0)

    @Test
    fun `compare two non-null ranges with different start`() =
            expect(RangeStartComparator<Int>().compare(2..3, 1..3) is_ Equal to_ 1)
}