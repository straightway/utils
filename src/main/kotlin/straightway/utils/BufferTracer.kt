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
import kotlin.reflect.jvm.jvmName
import java.lang.ThreadLocal

/**
 * Tracer which stores the trace messages in a buffer list.
 */
class BufferTracer(private val timeProvider: TimeProvider) : Tracer {

    private var onTraceAction: (TraceEntry) -> Any? = {  it }
    private val _traces = synchronizedList(mutableListOf<Any>())
    private var nestingLevel = ThreadLocal.withInitial { 0 }

    private val traceInterceptor get() = Interceptor<Tracer>(this) {
        onReturn {
            nestingLevel.set(nestingLevel.get() - 1)
            addTrace(TraceEvent.Return, TraceLevel.Unknown, it)
        }
        onException {
            addTrace(TraceEvent.Exception, TraceLevel.Unknown, it)
            nestingLevel.set(nestingLevel.get() - 1)
        }
    }

    override fun onTrace(action: (TraceEntry) -> Any?) { onTraceAction = action }

    override val traces: List<Any> get() = _traces

    override fun clear() { _traces.clear() }

    override fun traceMessage(level: TraceLevel, message: () -> String) =
            addTrace(TraceEvent.Message, level, message())

    override operator fun <TResult> invoke(vararg params: Any?, action: Tracer.() -> TResult) =
            traceInterceptor {
                addTrace(TraceEvent.Enter, TraceLevel.Unknown, params)
                nestingLevel.set(nestingLevel.get() + 1)
                action()
            }

    private fun getCallerOf(name: String): List<StackTraceElement> =
            Thread.currentThread().stackTrace.dropWhile { !it.isCallTo(name) }.drop(1).take(2)

    private fun addTrace(event: TraceEvent, level: TraceLevel, value: Any?) {
        var caller = getCallerOf("traceMessage")
        if (caller.isEmpty()) caller = getCallerOf("invoke")
        if (event == TraceEvent.Enter) tryAdd(level, TraceEvent.Calling, caller.last(), null)
        tryAdd(level, event, caller.first(), value)
    }

    private fun tryAdd(level: TraceLevel, traceEvent: TraceEvent, caller: StackTraceElement, value: Any?) =
        tryAdd(TraceEntry(
                timeProvider.now,
                Thread.currentThread().id,
                caller,
                nestingLevel.get(),
                traceEvent,
                level,
                value))

    private fun tryAdd(traceEntry: TraceEntry) {
        val transformedTraceEntry = onTraceAction(traceEntry)
        if (transformedTraceEntry != null) _traces.add(transformedTraceEntry)
    }

    private fun StackTraceElement?.isCallTo(name: String): Boolean {
        return this != null &&
               className == BufferTracer::class.jvmName &&
               methodName == name
    }
}