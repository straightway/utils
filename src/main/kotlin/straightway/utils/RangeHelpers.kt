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
@file:Suppress("UNUSED_PARAMETER")

package straightway.utils

infix fun <T: Comparable<T>> ClosedRange<T>.intersectsWith(other: ClosedRange<T>) =
        !isEmpty() && !other.isEmpty() &&
        (other.start in this || other.endInclusive in this || start in other)

infix fun <T: Comparable<T>> ClosedRange<T>.u(other: ClosedRange<T>) =
        when {
            isEmpty() -> if (other.isEmpty()) listOf() else listOf(other)
            other.isEmpty() -> listOf(this)
            this == other -> listOf(this)
            this intersectsWith other -> listOf(min(start, other.start)..(max(endInclusive, other.endInclusive)))
            else -> listOf(this, other)
        }

operator fun <T: Comparable<T>> ClosedRange<T>.div(other: ClosedRange<T>) =
        if (intersectsWith(other)) listOf(max(start, other.start)..min(endInclusive, other.endInclusive))
        else listOf()

operator fun <T: Comparable<T>> ClosedRange<T>.minus(other: ClosedRange<T>) =
        when {
            !intersectsWith(other) || other.start == other.endInclusive->
                listOf(this)
            other.start < start && endInclusive < other.endInclusive  || this == other->
                listOf()
            start < other.start && other.endInclusive < endInclusive ->
                listOf(start..other.start, other.endInclusive..endInclusive)
            start < other.start ->
                listOf(start..other.start)
            else ->
                listOf(other.endInclusive..endInclusive)
        }
