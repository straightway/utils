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

import java.util.Collections.synchronizedList

/**
 * Tracer which stores the trace messages in a buffer list.
 */
class BufferTracer : Tracer {

    private var onTraceAction: (TraceEntry) -> Any? = { it }
    private val _traces = synchronizedList(mutableListOf<Any>())

    private val traceInterceptor = Interceptor<Tracer>(this) {
        onReturn { addTrace(TraceEvent.Return, TraceLevel.Unknown, it) }
        onException { addTrace(TraceEvent.Exception, TraceLevel.Unknown, it) }
    }

    override fun onTrace(action: (TraceEntry) -> Any?) { onTraceAction = action }

    override val traces: List<Any> get() = _traces

    override fun clear() { _traces.clear() }

    override fun trace(level: TraceLevel, message: () -> String) =
            addTrace(TraceEvent.Message, level, message())

    override operator fun <TResult> invoke(vararg params: Any?, action: Tracer.() -> TResult) =
            traceInterceptor {
                addTrace(TraceEvent.Enter, TraceLevel.Unknown, params)
                action()
            }

    private val invokeCaller: StackTraceElement
        get() {
            val stackTrace = Thread.currentThread().stackTrace
            return stackTrace.dropWhile { !it.isInvokeCall }.drop(1).first()
        }

    private fun addTrace(event: TraceEvent, level: TraceLevel, value: Any?) {
        val traceEntry = onTraceAction(TraceEntry(invokeCaller, event, level, value))
        if (traceEntry != null) _traces.add(traceEntry)
    }

    private val StackTraceElement?.isInvokeCall get() =
        this != null && className == this@BufferTracer::class.qualifiedName && methodName == "invoke"
}