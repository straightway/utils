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

import straightway.utils.Private.toList
import straightway.utils.Private.maxOverlapWith
import straightway.utils.Private.hasZeroLength
import straightway.utils.Private.isFullyOverlappedBy
import straightway.utils.Private.exclude

/**
 * Determine if the two given ranges intersect.
 */
infix fun <T : Comparable<T>> ClosedRange<T>.intersectsWith(other: ClosedRange<T>) =
        !isEmpty() && !other.isEmpty() &&
        (other.start in this || other.endInclusive in this || start in other)

/**
 * Determine if the current range fully contains the given one.
 */
operator fun <T : Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>) =
        !other.isEmpty() && other.start in this && other.endInclusive in this

/**
 * Compute the set union of the two ranges.
 */
infix fun <T : Comparable<T>> ClosedRange<T>.u(other: ClosedRange<T>) =
        when {
            isEmpty() -> other.toList()
            other.isEmpty() -> Ranges(this)
            this intersectsWith other -> Ranges(maxOverlapWith(other))
            else -> Ranges(this, other)
        }

/**
 * Compute the set union of the two range sets.
 */
infix fun <T : Comparable<T>> Iterable<ClosedRange<T>>.u(other: Iterable<ClosedRange<T>>) =
        Ranges(this) + other

/**
 * Compute the intersection of the two ranges.
 */
operator fun <T : Comparable<T>> ClosedRange<T>.div(other: ClosedRange<T>) =
        if (intersectsWith(other))
            Ranges(max(start, other.start)..min(endInclusive, other.endInclusive))
        else Ranges()

/**
 * Compute the set difference of the two ranges.
 */
operator fun <T : Comparable<T>> ClosedRange<T>.minus(other: ClosedRange<T>) =
        when {
            !intersectsWith(other) || other.hasZeroLength() -> Ranges(this)
            isFullyOverlappedBy(other) -> Ranges()
            else -> exclude(other)
        }

/**
 * Compute the set difference ot the two range sets.
 */
infix fun <T : Comparable<T>> Iterable<ClosedRange<T>>.without(other: Iterable<ClosedRange<T>>) =
        Ranges(this).apply { this -= other }

// region Private

private object Private {

    fun <T : Comparable<T>> ClosedRange<T>.toList() =
            if (isEmpty()) Ranges() else Ranges(this)

    fun <T : Comparable<T>> ClosedRange<T>.maxOverlapWith(other: ClosedRange<T>) =
            min(start, other.start)..(max(endInclusive, other.endInclusive))

    fun <T : Comparable<T>> ClosedRange<T>.hasZeroLength() =
            start == endInclusive

    infix fun <T : Comparable<T>> ClosedRange<T>.isFullyOverlappedBy(other: ClosedRange<T>) =
            other.start <= start && endInclusive <= other.endInclusive

    fun <T : Comparable<T>> ClosedRange<T>.exclude(other: ClosedRange<T>) =
            when {
                other isFullyOverlappedBy this -> excludeInnerRange(other)
                start < other.start -> keepLeftOf(other)
                else -> keepRightOf(other)
            }

    fun <T : Comparable<T>> ClosedRange<T>.excludeInnerRange(inner: ClosedRange<T>) =
            Ranges(start..inner.start, inner.endInclusive..endInclusive)

    fun <T : Comparable<T>> ClosedRange<T>.keepRightOf(other: ClosedRange<T>) =
            Ranges(other.endInclusive..endInclusive)

    fun <T : Comparable<T>> ClosedRange<T>.keepLeftOf(other: ClosedRange<T>) =
            Ranges(start..other.start)
}

// endregion