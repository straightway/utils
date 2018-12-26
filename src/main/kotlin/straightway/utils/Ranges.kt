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
        val intersections = extractIntersectionsWith(toExclude)
        intersections.forEach { ranges.addSorted(it - toExclude) }
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
        val intersections = extractIntersectionsWith(toInclude)
        val union = getUnion(intersections + toInclude)
        ranges.addSorted(union)
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
        val intersections = extractIntersectionsWith(intersecting)
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

    operator fun minus(other: Iterable<ClosedRange<T>>): Ranges<T> =
            Ranges(this).apply { this -= other }

    operator fun minus(other: ClosedRange<T>): Ranges<T> =
            Ranges(this).apply { this -= other }

    operator fun plus(other: Iterable<ClosedRange<T>>): Ranges<T> =
            Ranges(this).apply { this += other }

    operator fun plus(other: ClosedRange<T>): Ranges<T> =
            Ranges(this).apply { this += other }

    val size get() = ranges.size

    companion object {

        /**
         * Create a new ranges object from the union of the given ranges.
         */
        operator fun <T : Comparable<T>> invoke(vararg ranges: ClosedRange<T>) =
                Ranges(ranges.asIterable())

        private fun <T : Comparable<T>> MutableList<ClosedRange<T>>.addSorted(
                new: Iterable<ClosedRange<T>>
        ) = new.forEach { addSorted(it) }

        private fun <T : Comparable<T>> MutableList<ClosedRange<T>>.addSorted(
                new: ClosedRange<T>
        ) {
            forEachIndexed { index, closedRange ->
                if (new.start <= closedRange.start) {
                    add(index, new)
                    return
                }
            }

            add(new)
        }

        private val <T : Comparable<T>> ClosedRange<T>.hasZeroLength get() = endInclusive <= start

        private fun <T : Comparable<T>> getUnion(elements: List<ClosedRange<T>>): ClosedRange<T> {
            val minStart = elements.minBy { it.start }!!.start
            val maxEndInclusive = elements.maxBy { it.endInclusive }!!.endInclusive
            return minStart..maxEndInclusive
        }

        private infix fun <T : Comparable<T>> List<ClosedRange<T>>.getIntersectionsWith(
                intersecting: ClosedRange<T>
        ): List<ClosedRange<T>> {
            val result = mutableListOf<ClosedRange<T>>()
            val rangesIterator = iterator()
            while (rangesIterator.hasNext()) {
                val curr = rangesIterator.next()
                if (curr intersectsWith intersecting) result.add(curr)
                if (intersecting.endInclusive < curr.start) break
            }

            return result
        }
    }

    // region Private

    private var ranges = mutableListOf<ClosedRange<T>>()

    init { ranges.forEach { plusAssign(it) } }

    private fun getIntersectionsWith(intersecting: ClosedRange<T>) =
            Ranges(this).apply { divAssign(intersecting) }

    private fun extractIntersectionsWith(toExclude: ClosedRange<T>): List<ClosedRange<T>> {
        val intersections = ranges getIntersectionsWith toExclude
        ranges.removeAll(intersections)
        return intersections
    }

    // endregion
}
