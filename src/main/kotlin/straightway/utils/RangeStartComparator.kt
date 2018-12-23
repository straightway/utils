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

import java.util.Comparator

/**
 * Compare two ranges by their start.
 */
class RangeStartComparator<T : Comparable<T>> : Comparator<ClosedRange<T>> {
    override fun compare(p0: ClosedRange<T>?, p1: ClosedRange<T>?) =
            when {
                p0 != null && p1 != null -> p0.start.compareTo(p1.start)
                else -> p0.compareTo(p1)
            }

    private fun ClosedRange<*>?.compareTo(other: ClosedRange<*>?) =
            when {
                this == null && other != null -> 1
                this != null && other == null -> -1
                else -> 0
            }
}