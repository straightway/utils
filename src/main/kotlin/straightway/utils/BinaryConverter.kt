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

const val BYTE_MASK = 0xff

fun ByteArray.getInt() = take(Integer.BYTES).fold(0) { acc, byte ->
    (acc shl java.lang.Byte.SIZE) or ((byte.toInt() and BYTE_MASK))
}

fun ByteArray.getUnsignedInt() =
        getInt() and Int.MAX_VALUE

fun Int.toByteArray() = ByteArray(Integer.BYTES) { getByte(Integer.BYTES - 1 - it) }

private fun Int.getByte(byteIndex: Int) =
        ((this and byteIndex.byteMask) ushr byteIndex.bitIndex).toByte()

private val Int.byteMask get() = BYTE_MASK shl bitIndex
private val Int.bitIndex get() = this * java.lang.Byte.SIZE