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

class BinaryConverterTest_longConversion {

    @Test
    fun `getLong for empty bytes yields 0`() =
            Given {
                byteArrayOf()
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ 0L)
            }

    @Test
    fun `getLong yields correct partial result`() =
            Given {
                byteArrayOf(1)
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ 1L)
            }

    @Test
    fun `getLong yield correct result`() =
            Given {
                byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x56, 0x78, 0x9a.toByte(), 0xbc.toByte())
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ 0x0102030456789abcL)
            }

    @Test
    fun `getLong yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0, 0, 0, 0, 0, 0, 0)
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ Long.MIN_VALUE)
            }

    @Test
    fun `getLong yields correct maximal result`() =
            Given {
                byteArrayOf(
                        0x7f.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte())
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ Long.MAX_VALUE)
            }

    @Test
    fun `getLong takes only the first eight bytes`() =
            Given {
                byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
            } when_ {
                getLong()
            } then {
                expect(it.result is_ Equal to_ 0x0102030405060708)
            }

    @Test
    fun `getUnsignedLong yields correct maximal result`() =
            Given {
                byteArrayOf(
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte(),
                        0xff.toByte())
            } when_ {
                getUnsignedLong()
            } then {
                expect(it.result is_ Equal to_ Long.MAX_VALUE)
            }

    @Test
    fun `getUnsignedLong yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0, 0, 0, 0, 0, 0, 0)
            } when_ {
                getUnsignedLong()
            } then {
                expect(it.result is_ Equal to_ 0L)
            }

    @Test
    fun `getUnsignedLong yields correct partial result`() =
            Given {
                byteArrayOf(1)
            } when_ {
                getUnsignedLong()
            } then {
                expect(it.result is_ Equal to_ 1L)
            }

    @Test
    fun `toByteArray of Long`() =
            Given { 0x123456789abcdef0L } when_ { toByteArray() } then {
                expect(it.result contentEquals byteArrayOf(
                        0x12,
                        0x34,
                        0x56,
                        0x78,
                        0x9a.toByte(),
                        0xbc.toByte(),
                        0xde.toByte(),
                        0xf0.toByte()))
            }

    @Test
    fun `Long_toByteArray is reciproke of getLong`() =
            Given { -1234567812345678L } when_ { toByteArray() } then {
                expect(this is_ Equal to_ it.result.getLong())
            }

    @Test
    fun `Long_toByteArray is reciproke of getLong for small values`() =
            Given { 1L } when_ { toByteArray() } then {
                expect(this is_ Equal to_ it.result.getLong())
            }
}