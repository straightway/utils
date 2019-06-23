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
import straightway.testing.flow.*

class StringFormatterTest {

    @Test
    fun `for normal object, toString is called`() =
            expect(3.formatted() is_ Equal to_ "3")

    @Test
    fun `null value yields null string representation`() =
            expect(null.formatted() is_ Equal to_ "<null>")

    @Test
    fun `collection yields string with elements in bracked`() =
            expect(listOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `array yields string with elements in bracked`() =
            expect(arrayOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `ByteArray yields string with elements in bracked`() =
            expect(byteArrayOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `CharArray yields string with elements in bracked`() =
            expect(charArrayOf('1', '2', '3').formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `ShortArray yields string with elements in bracked`() =
            expect(shortArrayOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `IntArray yields string with elements in bracked`() =
            expect(intArrayOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `LongArray yields string with elements in bracked`() =
            expect(longArrayOf(1, 2, 3).formatted() is_ Equal to_ "[1, 2, 3]")

    @Test
    fun `FloatArray yields string with elements in bracked`() =
            expect(floatArrayOf(1.0F, 2.0F, 3.0F).formatted() is_ Equal to_ "[1.0, 2.0, 3.0]")

    @Test
    fun `DoubleArray yields string with elements in bracked`() =
            expect(doubleArrayOf(1.0, 2.0, 3.0).formatted() is_ Equal to_ "[1.0, 2.0, 3.0]")

    @Test
    @Suppress("BooleanLiteralArgument")
    fun `BooleanArray yields string with elements in bracked`() =
            expect(booleanArrayOf(true, false).formatted() is_ Equal to_ "[true, false]")

    @Test
    fun `collection of arrays formats its elements properly`() =
            expect(listOf(arrayOf(1)).formatted() is_ Equal to_ "[[1]]")

    @Test
    fun `map with array as key formats its elements properly`() =
            expect(mapOf(arrayOf(1) to 2, arrayOf(3) to 4).formatted()
                    is_ Equal to_ "{[1]=2, [3]=4}")

    @Test
    fun `map with array as value formats its elements properly`() =
            expect(mapOf(2 to arrayOf(1), 4 to arrayOf(3)).formatted()
                    is_ Equal to_ "{2=[1], 4=[3]}")

    @Test
    fun `string is formatted with quotes`() =
            expect("Hello".formatted() is_ Equal to_ "\"Hello\"")

    @Test
    fun `LongRange is formatted with borders`() =
            expect((1L..5L).formatted() is_ Equal to_ "1..5")

    @Test
    fun `Values with array inside is properly formatted`() =
            expect(Values(byteArrayOf(1, 2), byteArrayOf(3)).formatted() is_ Equal
                    to_ "Values[[1, 2], [3]]")

    @Test
    fun `large arrays are cut in the middle`() =
            expect(IntArray(300) { it }.formatted() is_ Equal
                    to_ "[${(0..15).joinToString(", ")}, ...(268 more)..., " +
                    "${(284..299).joinToString(", ")}]")

    @Test
    fun `arrays having exactly max uncut length are not cut`() =
            expect(IntArray(32) { it }.formatted() is_ Equal
                    to_ "[${(0..31).joinToString(", ")}]")

    @Test
    fun `arrays exceeding max uncut length by one are cut`() =
            expect(IntArray(33) { it }.formatted() is_ Equal
                    to_ "[${(0..15).joinToString(", ")}, ...(1 more)..., " +
                    "${(17..32).joinToString(", ")}]")
}