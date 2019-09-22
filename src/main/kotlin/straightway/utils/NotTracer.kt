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
 * Tracer implementation which does nothing.
 */
class NotTracer : Tracer, TraceProvider {
    override val traces = listOf<TraceEntry>()
    override fun clear() {}
    override fun onTrace(action: (TraceEntry) -> Any?) {}
    override fun traceMessage(level: TraceLevel, message: () -> String) {}
    override fun <TResult> invoke(vararg params: Any?, action: Tracer.() -> TResult): TResult =
            action()
}