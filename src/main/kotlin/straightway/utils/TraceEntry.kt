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

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * A trace entry resulting from a trace call.
 */
data class TraceEntry(
        val timeStamp: LocalDateTime,
        val threadId: Long,
        val stackTraceElement: StackTraceElement,
        val nestingLevel: Int,
        val event: TraceEvent,
        val level: TraceLevel,
        val value: Any?
) {
    override fun toString(): String {
        val threadPrefix = "${timeStamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"))} [$threadIdHex] "
        val content = "$levelString$stackTraceElement ${event.description}$valueString"
        val indentation = threadPrefix.length + 2 * nestingLevel
        return "$threadPrefix${content.indent(indentation).removeRange(0 until threadPrefix.length)}"
    }

    private val threadIdHex get() =
            threadIdBytes.map { it.toHex() }.joinToString("")

    private val threadIdBytes get() =
            with(threadId.toByteArray().dropWhile { it == NULL }) {
                if (isEmpty()) listOf(NULL) else this
            }

    private val valueString get() = if (value == null) "" else ": ${value.formatted()}"
    private val levelString get() = if (level == TraceLevel.Unknown) "" else "$level: "

    private companion object {
        const val NULL: Byte = 0
    }
}