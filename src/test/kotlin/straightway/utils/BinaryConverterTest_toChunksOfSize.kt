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
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class BinaryConverterTest_toChunksOfSize {

    @Test
    fun `empty binary array is cut to empty list of chunks`() =
            Given {
                byteArrayOf()
            } when_ {
                toChunksOfSize(1)
            } then {
                expect(it.result is_ Empty)
            }

    @Test
    fun `small binary array is cut to one chunk`() =
            Given {
                byteArrayOf(1)
            } when_ {
                toChunksOfSize(1)
            } then {
                expect(it.result is_ Equal to_ arrayOf(byteArrayOf(1)))
            }

    @Test
    fun `binary array is cut to two chunks`() =
            Given {
                byteArrayOf(1, 2)
            } when_ {
                toChunksOfSize(1)
            } then {
                expect(it.result is_ Equal to_ arrayOf(byteArrayOf(1), byteArrayOf(2)))
            }

    @Test
    fun `binary array is cut to chunks larger than one byte`() =
            Given {
                byteArrayOf(1, 2, 3)
            } when_ {
                toChunksOfSize(2)
            } then {
                expect(it.result is_ Equal to_ arrayOf(byteArrayOf(1, 2), byteArrayOf(3)))
            }
}