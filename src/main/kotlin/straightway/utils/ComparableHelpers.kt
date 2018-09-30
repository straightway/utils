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

import straightway.error.Panic

fun <T: Comparable<T>> min(vararg items: T): T = extreme(*items) { a, b -> a < b }

fun <T: Comparable<T>> max(vararg items: T): T = extreme(*items) { a, b -> b < a }

fun <T> extreme(vararg items: T, isFirstOne: (T, T) -> Boolean): T =
        when {
            items.isEmpty() -> throw Panic("extreme without arguments")
            items.size == 1 -> items[0]
            else -> extreme(
                    *items.sliceArray(1..items.lastIndex),
                    isFirstOne = isFirstOne).let {
                if (isFirstOne(items[0], it)) items[0] else it
            }
        }