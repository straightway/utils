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

class BinaryConverterTest_shortConversion {

    @Test
    fun `getShort for empty bytes yields 0`() =
            Given {
                byteArrayOf()
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ 0)
            }

    @Test
    fun `getShort yields correct partial result`() =
            Given {
                byteArrayOf(1)
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ 1)
            }

    @Test
    fun `getShort yield correct result`() =
            Given {
                byteArrayOf(1, 2)
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ 0x0102)
            }

    @Test
    fun `getShort yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0)
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ Short.MIN_VALUE)
            }

    @Test
    fun `getShort yields correct maximal result`() =
            Given {
                byteArrayOf(0x7f.toByte(), 0xff.toByte())
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ Short.MAX_VALUE)
            }

    @Test
    fun `getShort yields correct ordinary result`() =
            Given {
                byteArrayOf(0x0a.toByte(), 0xbc.toByte())
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ 0x0abc)
            }

    @Test
    fun `getShort takes only the first four bytes`() =
            Given {
                byteArrayOf(1, 2, 3, 4, 5)
            } when_ {
                getShort()
            } then {
                expect(it.result is_ Equal to_ 0x0102)
            }

    @Test
    fun `getUnsignedShort yields correct maximal result`() =
            Given {
                byteArrayOf(0xff.toByte(), 0xff.toByte())
            } when_ {
                getUnsignedShort()
            } then {
                expect(it.result is_ Equal to_ 0xffff)
            }

    @Test
    fun `getUnsignedShort yields correct minimal result`() =
            Given {
                byteArrayOf(0x80.toByte(), 0, 0, 0)
            } when_ {
                getUnsignedShort()
            } then {
                expect(it.result is_ Equal to_ 0x8000)
            }

    @Test
    fun `getUnsignedShort yields correct partial result`() =
            Given {
                byteArrayOf(1)
            } when_ {
                getUnsignedShort()
            } then {
                expect(it.result is_ Equal to_ 1)
            }

    @Test
    fun `toByteArray of Short`() =
            Given { 0x1234.toShort() } when_ { toByteArray() } then {
                expect(it.result contentEquals byteArrayOf(0x12, 0x34))
            }

    @Test
    fun `Short_toByteArray is reciproke of getShort`() =
            Given { -12345.toShort() } when_ { toShort().toByteArray() } then {
                expect(this is_ Equal to_ it.result.getShort())
            }

    @Test
    fun `Short_toByteArray is reciproke of getShort for small values`() =
            Given { 1.toShort() } when_ { toByteArray() } then {
                expect(this is_ Equal to_ it.result.getShort())
            }
}