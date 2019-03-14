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
@file:Suppress("MagicNumber")

package straightway.utils

fun Byte.toIntUnsigned(): Int = this.toInt().let { if (it < 0) it and 0xff else it }

fun Byte.toHex(): String = toIntUnsigned().toString(16).padStart(2, '0')

fun ByteArray.toHexBlocks(blockSize: Int) =
        map { it.toHex() }.chunked(blockSize).map { it.joinToString(" ") }.joinToString("\n")