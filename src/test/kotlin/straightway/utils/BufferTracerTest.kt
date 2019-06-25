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

import org.junit.jupiter.api.Test
import straightway.error.Panic
import straightway.testing.bdd.Given
import straightway.testing.bdd.WhenResult
import straightway.testing.flow.*
import kotlin.reflect.KCallable

class BufferTracerTest {

    private class Tester {
        val tracer = BufferTracer()
        var traced = tracer.traces
        fun testReturn(value: Int) = tracer(value) { value + 1 }
        fun testPanic(): Int = tracer { throw Panic("Panic") }
        fun testTrace(level: TraceLevel, message: String) = tracer {
            trace(level) { message }
        }
    }

    private val test = Given { Tester() }

    @Test
    fun `result is traced`() {
        test when_ {
            testReturn(83)
        } then {
            assertEventSequence(
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) })
        }
    }

    @Test
    fun `multiple events are traced`() {
        test when_ {
            testReturn(83)
            testReturn(83)
        } then {
            assertEventSequence(
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) },
                    { assertEnterEvent(Tester::testReturn, 83) },
                    { assertReturnEvent(Tester::testReturn, 84) })
        }
    }

    @Test
    fun `exception is traced`() {
        test when_ {
            testPanic()
        } then {
            assertExceptionHasBeenThrown(it)
            assertEventSequence(
                    { assertEnterEvent(Tester::testPanic) },
                    { assertPanic() })
        }
    }

    @Test
    fun `returning null in onTrace interceptor disables tracing enter and return events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testReturn(83)
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `returning null in onTrace interceptor disables tracing exception events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testPanic()
            } then {
                assertExceptionHasBeenThrown(it)
                expect(traced is_ Empty)
            }

    @Test
    fun `returning null in onTrace interceptor disables tracing message events`() =
            test while_ {
                tracer.onTrace { null }
            } when_ {
                testTrace(TraceLevel.Info, "Hello")
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `clear removes all traces`() =
            test while_ {
                testReturn(83)
            } when_ {
                tracer.clear()
            } then {
                expect(traced is_ Empty)
            }

    @Test
    fun `trace traces a message`() =
            test when_ {
                testTrace(TraceLevel.Debug, "Hello World")
            } then {
                lateinit var enterTrace: StackTraceElement
                assertEventSequence(
                        {
                            enterTrace = stackTraceElement
                            assertEnterEvent(Tester::testTrace)
                        },
                        {
                            expect(stackTraceElement.fileName is_ Equal to_ enterTrace.fileName)
                            expect(stackTraceElement.lineNumber is_ Equal to_ enterTrace.lineNumber + 1)
                            expect(event is_ Equal to_ TraceEvent.Message)
                            expect(level is_ Equal to_ TraceLevel.Debug)
                            expect(value is_ Equal to_ "Hello World")
                        },
                        { assertReturnEvent(Tester::testTrace) })
            }

    private companion object {

        fun assertExceptionHasBeenThrown(test: WhenResult<*>) {
            expect({ test.result } does Throw.exception)
        }

        fun Tester.assertEventSequence(vararg asserts: TraceEntry.() -> Unit) {
            expect(traced has Size of asserts.size)
            asserts.forEachIndexed { index, assert ->
                @Suppress("UNCHECKED_CAST")
                (traced[index] as TraceEntry).assert()
            }
        }

        fun TraceEntry.assertPanic() {
            assertFunctionCall(Tester::testPanic)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Exception)
            expect(value is Panic) { "Unexpected exception type: $value" }
        }

        fun TraceEntry.assertEnterEvent(method: KCallable<*>, vararg params: Any? ) {
            assertFunctionCall(method)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Enter)
            expect(value is_ Equal to_ params)
        }

        fun TraceEntry.assertReturnEvent(method: KCallable<*>, returnValue: Any = Unit) {
            assertFunctionCall(method)
            expect(level is_ Equal to_ TraceLevel.Unknown)
            expect(event is_ Equal to_ TraceEvent.Return)
            expect(value is_ Equal to_ returnValue)
        }

        fun TraceEntry.assertFunctionCall(method: KCallable<*>) {
            expect(stackTraceElement.isTestFunCall(method)) { "Unexpected function call: $stackTraceElement" }
        }

        fun StackTraceElement.isTestFunCall(method: KCallable<*>) =
                className.startsWith(Tester::class.java.name) && methodName == method.name
    }
}