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

import java.util.NavigableSet
import java.util.TreeSet

/**
 * Set of disjoint ranges.
 */
class Ranges<T : Comparable<T>>(ranges: Iterable<ClosedRange<T>>)
    : Iterable<ClosedRange<T>> {

    /**
     * Iterate through all ranges in ascending order
     */
    override fun iterator() = ranges.iterator()

    /**
     * String representation.
     */
    override fun toString() = "Ranges[${joinToString(", ")}]"

    /**
     * Exclude the given range (set difference).
     */
    operator fun minusAssign(toExclude: ClosedRange<T>) {
        if (toExclude.hasZeroLength) return
        val intersections = IntersectionsPoller(toExclude).intersections
        ranges.addAll(intersections.flatMap { it - toExclude })
    }

    /**
     * Exclude the given ranges (set difference).
     */
    operator fun minusAssign(toExclude: Iterable<ClosedRange<T>>) =
            toExclude.forEach(this::minusAssign)

    /**
     * Add the given range (set union).
     */
    operator fun plusAssign(toInclude: ClosedRange<T>) {
        if (toInclude.hasZeroLength) return
        val intersections = IntersectionsPoller(toInclude).intersections
        val union = getUnion(intersections + toInclude)
        ranges.add(union)
    }

    /**
     * Add the given ranges (set union).
     */
    operator fun plusAssign(toInclude: Iterable<ClosedRange<T>>) =
            toInclude.forEach(this::plusAssign)

    /**
     * Compute the intersection of the current ranges with the given range.
     */
    operator fun divAssign(intersecting: ClosedRange<T>) {
        val intersections = IntersectionsPoller(intersecting).intersections
        ranges.clear()
        intersections.forEach { ranges.addAll(it / intersecting) }
    }

    /**
     * Compute the intersection of the current ranges with the given ranges.
     */
    operator fun divAssign(intersecting: Iterable<ClosedRange<T>>) {
        val allIntersections = intersecting.map { getIntersectionsWith(it) }
        ranges.clear()
        allIntersections.forEach(this::plusAssign)
    }

    /**
     * Compute the difference to another set of ranges.
     */
    operator fun minus(other: Iterable<ClosedRange<T>>): Ranges<T> =
            Ranges(this).apply { this -= other }

    /**
     * Compute the difference to another range.
     */
    operator fun minus(other: ClosedRange<T>): Ranges<T> =
            Ranges(this).apply { this -= other }

    /**
     * Compute the union with another set of ranges.
     */
    operator fun plus(other: Iterable<ClosedRange<T>>): Ranges<T> =
            Ranges(this).apply { this += other }

    /**
     * Compute the union with another range.
     */
    operator fun plus(other: ClosedRange<T>): Ranges<T> =
            Ranges(this).apply { this += other }

    /**
     * Gets the number of included ranges.
     */
    val size get() = ranges.size

    companion object {

        /**
         * Create a new ranges object from the union of the given ranges.
         */
        operator fun <T : Comparable<T>> invoke(vararg ranges: ClosedRange<T>) =
                Ranges(ranges.asIterable())

        private val <T : Comparable<T>> ClosedRange<T>.hasZeroLength get() = endInclusive <= start

        private fun <T : Comparable<T>> getUnion(elements: List<ClosedRange<T>>): ClosedRange<T> {
            val minStart = elements.minBy { it.start }!!.start
            val maxEndInclusive = elements.maxBy { it.endInclusive }!!.endInclusive
            return minStart..maxEndInclusive
        }
    }

    // region Private

    private var ranges = TreeSet<ClosedRange<T>>(RangeStartComparator())

    init { ranges.forEach { plusAssign(it) } }

    private fun getIntersectionsWith(intersecting: ClosedRange<T>) =
            Ranges(this).apply { divAssign(intersecting) }

    private inner class IntersectionsPoller(private val intersecting: ClosedRange<T>) {
        val intersections = mutableListOf<ClosedRange<T>>()
        init {
            extractIntersections(ranges.tailSet(intersecting, true)!!) { pollFirst() }
            extractIntersections(ranges.headSet(intersecting, true)!!) { pollLast() }
        }

        private inline fun extractIntersections(
                part: NavigableSet<ClosedRange<T>>,
                extractor: NavigableSet<ClosedRange<T>>.() -> ClosedRange<T>?
        ) {
            while (true) {
                val candidate = part.extractor()
                when {
                    candidate == null -> return
                    candidate intersectsWith intersecting -> intersections.add(candidate)
                    else -> { ranges.add(candidate); return }
                }
            }
        }
    }

    // endregion
}
