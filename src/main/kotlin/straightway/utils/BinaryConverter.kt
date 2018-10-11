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
const val BYTE_MASK_LONG = 0xffL

/**
 * Get an integer from the first four bytes of the given byte array. It is
 * expected that the data is stored in big endian byte order.
 */
fun ByteArray.getInt() = take(Integer.BYTES).fold(0) { acc, byte ->
    (acc shl java.lang.Byte.SIZE) or byte.intValue
}

/**
 * Get a long integer from the first four bytes of the given byte array. It is
 * expected that the data is stored in big endian byte order.
 */
fun ByteArray.getLong() = take(java.lang.Long.BYTES).fold(0L) { acc, byte ->
    (acc shl java.lang.Byte.SIZE) or byte.intValue.toLong()
}

/**
 * Get an unsigned integer from the first four bytes of the given byte array. It is
 * expected that the data is stored in big endian byte order.
 */
fun ByteArray.getUnsignedInt() =
        getInt() and Int.MAX_VALUE

/**
 * Get an unsigned long integer from the first four bytes of the given byte array. It is
 * expected that the data is stored in big endian byte order.
 */
fun ByteArray.getUnsignedLong() =
        getLong() and Long.MAX_VALUE

/**
 * Encode the given value to a byte array in big endian byte order.
 */
fun Int.toByteArray() = ByteArray(Integer.BYTES) {
    getByte(Integer.BYTES - 1 - it)
}

/**
 * Encode the given value to a byte array in big endian byte order.
 */
fun Long.toByteArray() = ByteArray(java.lang.Long.BYTES) {
    getByte(java.lang.Long.BYTES - 1 - it)
}

private val Byte.intValue get() = toInt() and BYTE_MASK

private fun Int.getByte(byteIndex: Int) =
        ((this and byteIndex.byteMask) ushr byteIndex.bitIndex).toByte()

private fun Long.getByte(byteIndex: Int) =
        ((this and byteIndex.byteMaskLong) ushr byteIndex.bitIndex).toByte()

private val Int.byteMask get() = BYTE_MASK shl bitIndex
private val Int.byteMaskLong get() = BYTE_MASK_LONG shl bitIndex
private val Int.bitIndex get() = this * java.lang.Byte.SIZE
