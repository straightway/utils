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

class ByteHelpersTest {

    @Test
    fun `toIntUnsigned of positive value is equal`() =
            expect(byteOf(1).toIntUnsigned() is_ Equal to_ 1)

    @Test
    fun `toIntUnsigned of negative value is equal`() =
        expect(byteOf(-128).toIntUnsigned() is_ Equal to_ 0x80)

    @Test
    fun `toIntUnsigned of -1 is 0xff`() =
        expect(byteOf(-1).toIntUnsigned() is_ Equal to_ 0xff)

    @Test
    fun `toIntUnsigned of -2 is 0xfe`() =
        expect(byteOf(-2).toIntUnsigned() is_ Equal to_ 0xfe)

    @Test
    fun `toIntUnsigned of -127 is 0x81`() =
        expect(byteOf(-127).toIntUnsigned() is_ Equal to_ 0x81)

    @Test
    fun `toHex for 0 yields 00`() =
            expect(byteOf(0).toHex() is_ Equal to_ "00")

    @Test
    fun `toHex for 1 yields 01`() =
            expect(byteOf(1).toHex() is_ Equal to_ "01")

    @Test
    fun `toHex for 12 yields 0c`() =
            expect(byteOf(12).toHex() is_ Equal to_ "0c")

    @Test
    fun `toHex for 16 yields 10`() =
            expect(byteOf(16).toHex() is_ Equal to_ "10")
    @Test
    fun `toHex for 255 yields ff`() =
            expect(byteOf(255).toHex() is_ Equal to_ "ff")

    @Test
    fun `toHexBlocks of empty array is empty`() =
            expect(byteArrayOf().toHexBlocks(2) is_ Equal to_ "")

    @Test
    fun `toHexBlocks of two element array`() =
            expect(byteArrayOf(0xff.toByte(), 0).toHexBlocks(2) is_ Equal to_ "ff 00")

    @Test
    fun `toHexBlocks with multiple lines`() =
            expect(byteArrayOf(0xff.toByte(), 0, 1).toHexBlocks(2) is_ Equal to_ "ff 00\n01")

    private fun byteOf(v: Int) = v.toByte()
}