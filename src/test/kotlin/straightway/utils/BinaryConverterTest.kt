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
import straightway.testing.flow.Equal
import straightway.testing.flow.expect
import straightway.testing.flow.is_
import straightway.testing.flow.to_

class BinaryConverterTest {

    @Test
    fun `getInt for empty bytes yields 0`() =
            Given {
                byteArrayOf()
            } when_ {
                getInt()
            } then {
                expect(it.result is_ Equal to_ 0)
            }

    @Test
    fun `getInt yield correct result`() =
            Given {
                byteArrayOf(1, 2, 3, 4)
            } when_ {
                getInt()
            } then {
                expect(it.result is_ Equal to_ 0x01020304)
            }

    @Test
    fun `getInt yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0, 0, 0)
            } when_ {
                getInt()
            } then {
                expect(it.result is_ Equal to_ Int.MIN_VALUE)
            }

    @Test
    fun `getInt yields correct maximal result`() =
            Given {
                byteArrayOf(0x7f.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte())
            } when_ {
                getInt()
            } then {
                expect(it.result is_ Equal to_ Int.MAX_VALUE)
            }

    @Test
    fun `getInt takes only the first four bytes`() =
            Given {
                byteArrayOf(1, 2, 3, 4, 5)
            } when_ {
                getInt()
            } then {
                expect(it.result is_ Equal to_ 0x01020304)
            }

    @Test
    fun `getPositiveInt yields correct maximal result`() =
            Given {
                byteArrayOf(0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte())
            } when_ {
                getPositiveInt()
            } then {
                expect(it.result is_ Equal to_ Int.MAX_VALUE)
            }


    @Test
    fun `getPositiveInt yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0, 0, 0)
            } when_ {
                getPositiveInt()
            } then {
                expect(it.result is_ Equal to_ 0)
            }
}